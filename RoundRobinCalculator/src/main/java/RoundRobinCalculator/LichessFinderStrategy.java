package RoundRobinCalculator;

import chariot.Client;
import chariot.model.Game;
import com.mongodb.client.MongoCollection;

import org.bson.Document;

import java.util.List;

/**
 * The type Lichess finder strategy.
 */
public class LichessFinderStrategy extends GameFinder implements GameFinderStrategy {


    /**
     * Instantiates a new Lichess finder strategy.
     *
     * @param timeControl            the time control
     * @param timeControlInc         the time control inc
     * @param FEN                    the fen
     * @param isRated                the is rated
     * @param pairing                the pairing
     * @param playercol              the playercol
     * @param rRtournamentcollection the r rtournamentcollection
     * @param tourneyID              the tourney id
     */
    public LichessFinderStrategy(int timeControl, int timeControlInc, String FEN, boolean isRated, String pairing, MongoCollection<Document> playercol, MongoCollection<Document> rRtournamentcollection, String tourneyID) {
        super(timeControl, timeControlInc, FEN, isRated, pairing, Platform.LICHESS, playercol, rRtournamentcollection, tourneyID);
    }


    @Override
    public boolean findGame(String player1, String player2, String color1, String color2) {
        try {

            System.out.println("Incoming Player 1 Discord Name: " + player1);
            System.out.println("Incoming Player 2 Discord Name: " + player2);

            String convertedPlayer1 = getActions().searchGameFinderNames(player1, getRRcollection(), Platform.LICHESS);
            String convertedPlayer2 = getActions().searchGameFinderNames(player2, getRRcollection(), Platform.LICHESS);

            if (convertedPlayer1 == null || convertedPlayer2 == null || convertedPlayer1.contains("null") || convertedPlayer2.contains("null")) {
                System.out.println(Platform.LICHESS);
                System.out.println(convertedPlayer1 + " Player 1 ");
                System.out.println(convertedPlayer2 + " Player 2 ");
                System.out.println("INVALID Player conversion names");
                return false;
            }

            Client client = Client.auth(System.getenv("LICHESS_TOKEN"));
            String searchColorName;
            String searchColorNamep2;

            if (color1.contains("White")) {
                searchColorName = convertedPlayer1;
                searchColorNamep2 = convertedPlayer2;
            } else if (color2.contains("White")) {
                searchColorName = convertedPlayer2;
                searchColorNamep2 = convertedPlayer1;

            } else {
                searchColorName = null;
                searchColorNamep2 = null;
            }
            System.out.println(Platform.LICHESS);
            System.out.println(convertedPlayer1 + " Player 1");
            System.out.println(convertedPlayer2 + " Player 2");


            List<Game> gamesHistory = client.games().byUserId(convertedPlayer1, fl -> fl.vs(convertedPlayer2).rated()).stream().toList();

            if (gamesHistory.isEmpty()) {
                System.out.println("No Games Found For Match: " + new PairingBuilder().buildPairingNormal(player1, player2));
                return false;
            }

            String gamePGN = null;
            String gameURL = null;


            for (Game g : gamesHistory) {
                if (g.players().white().name().equalsIgnoreCase(searchColorName) &&
                        g.players().black().name().equalsIgnoreCase(searchColorNamep2) &&
                        g.clock().initial() / 60 == getTimeControl() &&
                        g.clock().increment() == getTimeControlInc()) {

                    gamePGN = g.pgn();
                    gameURL = "https://lichess.org/" + g.id();
                    break;
                }
            }

            if (gamePGN == null && gameURL == null) {
                System.out.println("No Games Found 2nd re-try For Match: " + new PairingBuilder().buildPairingNormal(player1, player2));
                return false;
            }

            String gameresult = extractResult(gamePGN);

            GameState gameState = null;

            switch (gameresult) {
                case "1-0" -> gameState = GameState.PLAYER_ONE_WON;
                case "0-1" -> gameState = GameState.PLAYER_TWO_WON;
                case "1/2-1/2" -> gameState = GameState.DRAW;

            }

            getUpdateAction().updatePlayerScore(player1, player2, getPlatform(), gameState, getRRcollection(), getRRtournamentcollection(), getTourneyID());
            getUpdateAction().submitGameURL(getRRtournamentcollection(), gameURL, getTourneyID());
            getCrosstableGenerator().updateCrossTableScores(player1, player2, gameState);

            return false;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
