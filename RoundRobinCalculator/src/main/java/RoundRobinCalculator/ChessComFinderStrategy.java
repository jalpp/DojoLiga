package RoundRobinCalculator;

import com.mongodb.client.MongoCollection;
import io.github.sornerol.chess.pubapi.client.PlayerClient;
import io.github.sornerol.chess.pubapi.domain.game.ArchiveGame;
import org.bson.Document;

import java.time.LocalDate;
import java.util.List;

/**
 * The type Chess com finder strategy.
 */
public class ChessComFinderStrategy extends GameFinder implements GameFinderStrategy {


    /**
     * Instantiates a new Chess com finder strategy.
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
    public ChessComFinderStrategy(int timeControl, int timeControlInc, String FEN, boolean isRated, String pairing, MongoCollection<Document> playercol, MongoCollection<Document> rRtournamentcollection, String tourneyID) {
        super(timeControl, timeControlInc, FEN, isRated, pairing, Platform.CHESSCOM, playercol, rRtournamentcollection, tourneyID);
    }

    @Override
    public boolean findGame(String player1, String player2, String color1, String color2) {
        try {


            String convertedPlayer1 = getActions().searchGameFinderNames(player1, getRRcollection(), Platform.CHESSCOM);
            String convertedPlayer2 = getActions().searchGameFinderNames(player2, getRRcollection(), Platform.CHESSCOM);

            if (convertedPlayer1 == null || convertedPlayer2 == null || convertedPlayer1.contains("null") || convertedPlayer2.contains("null")) {
                System.out.println(Platform.CHESSCOM);
                System.out.println(convertedPlayer1 + " Player 1");
                System.out.println(convertedPlayer2 + " Player 2");
                System.out.println("INVALID Player conversion names");
                return false;
            }

            PlayerClient client = new PlayerClient();

            LocalDate today = LocalDate.now();
            int year = today.getYear();
            int month = today.getMonthValue();

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
            System.out.println(Platform.CHESSCOM);
            System.out.println(convertedPlayer1 + " Player 1");
            System.out.println(convertedPlayer2 + " Player 2");


            List<ArchiveGame> games = client.getMonthlyArchiveForPlayer(convertedPlayer2, month, year).getGames().stream().
                    filter(fl -> fl.getRated() == isRated())
                    .filter(fl -> fl.getInitialSetup().equalsIgnoreCase(getFEN()))
                    .filter(fl -> Integer.parseInt(fl.getTimeControl().replace("+", "-").split("-")[0]) / 60 == getTimeControl())
                    .filter(fl -> fl.getWhite().getUsername().equalsIgnoreCase(searchColorName))
                    .filter(fl -> fl.getBlack().getUsername().equalsIgnoreCase(searchColorNamep2))
                    .toList();

            if (games.size() == 0) {
                System.out.println("No Games Found For Match: " + new PairingBuilder().buildPairingNormal(player1, player2));
                return false;
            }

            ArchiveGame game = games.get(0);
            String gameresult = extractResult(game.getPgn());

            GameState gameState = null;

            switch (gameresult) {
                case "1-0" -> gameState = GameState.PLAYER_ONE_WON;
                case "0-1" -> gameState = GameState.PLAYER_TWO_WON;
                case "1/2-1/2" -> gameState = GameState.DRAW;

            }

            getUpdateAction().updatePlayerScore(player1, player2, getPlatform(), gameState, getRRcollection(), getRRtournamentcollection(), getTourneyID());
            getUpdateAction().submitGameURL(getRRtournamentcollection(), game.getUrl(), getTourneyID());
            getCrosstableGenerator().updateCrossTableScores(player1, player2, gameState);

            return false;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;

        }

    }


}
