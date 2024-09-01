package dojo.bot.Controller.RoundRobin;

import com.mongodb.client.MongoCollection;
import io.github.sornerol.chess.pubapi.client.PlayerClient;
import io.github.sornerol.chess.pubapi.domain.game.ArchiveGame;
import org.bson.Document;

import java.time.LocalDate;
import java.util.List;

public class ChessComStrategy implements CalculateResultStrategy{

    private final Platform platform = Platform.CHESSCOM;
    private final String gameURL;
    private final String player1username;
    private final UpdatePlayerScores updateAction;
    private final MongoCollection<Document> RRplayercollection;
    private final MongoCollection<Document> RRcollection;
    private final RoundRobinCrosstable crosstableGen;
    private final String tournamentID;


    public ChessComStrategy(String gameURL, String player1username, MongoCollection<Document> rRplayercollection, MongoCollection<Document> rRcollection, String tournamentID){
        this.gameURL = gameURL;
        this.player1username = player1username;
        this.updateAction = new UpdatePlayerScores();
        RRplayercollection = rRplayercollection;
        RRcollection = rRcollection;
        this.tournamentID = tournamentID;
        this.crosstableGen = new RoundRobinCrosstable(RRcollection, RRplayercollection, tournamentID);
    }


    @Override
    public void calculateGameResult() throws RoundRobinException {

        PlayerClient client = new PlayerClient();
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();


        try {
            List<ArchiveGame> list = client.getMonthlyArchiveForPlayer(player1username, year, month).getGames().stream().filter(s -> s.getUrl().equalsIgnoreCase(gameURL)).toList();
            System.out.println(list);

            if(list.isEmpty()){
                System.out.println("URL not computed");
            }else{
                ArchiveGame game = list.get(0);
                String gameresult = (game.getPgn());

                System.out.println(gameresult + " |||RESULT");


                String player1 = game.getWhite().getUsername().toLowerCase().trim();
                String player2 = game.getBlack().getUsername().toLowerCase().trim();
                GameState gameState = null;

                switch (gameresult) {
                    case "1-0" -> gameState = GameState.PLAYER_ONE_WON;
                    case "0-1" -> gameState = GameState.PLAYER_TWO_WON;
                    case "1/2-1/2" -> gameState = GameState.DRAW;

                }

                updateAction.updatePlayerScore(player1, player2, platform, gameState, RRplayercollection, RRcollection, tournamentID);
                updateAction.submitGameURL(RRcollection, gameURL, tournamentID);
                crosstableGen.updateCrossTableScores(player1, player2, gameState);
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }


    }



}
