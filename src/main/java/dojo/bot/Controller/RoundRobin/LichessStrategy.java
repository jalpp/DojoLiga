package dojo.bot.Controller.RoundRobin;

import chariot.Client;
import chariot.model.Enums;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * The type Lichess strategy.
 */
public class LichessStrategy implements CalculateResultStrategy{
    private final Platform platform = Platform.LICHESS;
    private final UpdatePlayerScores updateAction;
    private final RoundRobinCrosstable crosstableGen;
    private final String gameURL;
    private final MongoCollection<Document> RRplayercollection;
    private final MongoCollection<Document> RRcollection;

    private final Client client = Client.basic();
    private final String tournamentID;


    /**
     * Instantiates a new Lichess strategy.
     *
     * @param gameURL            the game url
     * @param players            the players
     * @param tournaments        the tournaments
     * @param targetTournamentID the target tournament id
     */
    public LichessStrategy(String gameURL, MongoCollection<Document> players, MongoCollection<Document> tournaments, String targetTournamentID){
        this.gameURL = gameURL;
        this.updateAction = new UpdatePlayerScores();
        this.RRplayercollection = players;
        this.RRcollection = tournaments;
        this.tournamentID = targetTournamentID;
        this.crosstableGen = new RoundRobinCrosstable(RRcollection, RRplayercollection, targetTournamentID);
    }

    private String isGameURLValid(String link, Client client) {
        String[] spliter = link.split("/");
        String validGameId = "";

        if (spliter.length <= 3)
            return null;

        if (spliter[3].length() == 12) {
            validGameId += spliter[3].substring(0, spliter[3].length() - 4);
        } else {
            validGameId += spliter[3];
        }

        if (!(link.contains("https://lichess.org/") && client.games().byGameId(validGameId).isPresent())) {
            return null;
        }

        return validGameId;
    }


    @Override
    public void calculateGameResult() throws RoundRobinException{

        Client client = Client.basic();

        String gameID = isGameURLValid(gameURL, client);

        System.out.println(gameID);

        Enums.Color c = client.games().byGameId(gameID).get().winner();
        String player1 = client.games().byGameId(gameID).get().players().white().name().toLowerCase().trim();
        String player2 = client.games().byGameId(gameID).get().players().black().name().toLowerCase().trim();
        GameState gameState = null;

        if(c != null) {

            String winnerColor = client.games().byGameId(gameID).get().winner().asPref().toString();

            switch (winnerColor) {
                case "white" -> gameState = GameState.PLAYER_ONE_WON;
                case "black" -> gameState = GameState.PLAYER_TWO_WON;
            }
        }else{
            gameState = GameState.DRAW;

        }

        updateAction.updatePlayerScore(player1, player2, platform, gameState, RRplayercollection, RRcollection, tournamentID);
        crosstableGen.updateCrossTableScores(player1, player2, gameState);
        updateAction.submitGameURL(RRcollection, gameURL, tournamentID);

    }
}
