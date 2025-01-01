package dojo.bot.Controller.CalculateScores;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import dojo.bot.Controller.Database.MongoConnect;
import dojo.bot.Controller.DojoScoreboard.DojoScoreboard;
import dojo.bot.Controller.League.Time_Control;
import dojo.bot.Controller.League.Type;
import dojo.bot.Controller.User.CCProfile;
import dojo.bot.Controller.User.ChessPlayer;
import io.github.sornerol.chess.pubapi.client.TournamentClient;
import io.github.sornerol.chess.pubapi.domain.tournament.TournamentPlayer;
import io.github.sornerol.chess.pubapi.domain.tournament.TournamentRound;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static dojo.bot.Controller.CalculateScores.ScoresUtil.*;

public class ComputeScorescc {


    public ComputeScorescc(){

    }


    /**
     * Sets the provided player's total combined score for the given time control.
     *
     * @param playerName  The cc username of the player to update.
     * @param collection  The collection containing the player to update.
     * @param timeControl The time control to update the player's score for.
     */
    public void addCombinedPlayerTotalScores(String playerName, MongoCollection<Document> collection,
                                             Time_Control timeControl) {
        Document query = new Document("Chesscomname", playerName);
        Document result = collection.find(query).first();

        if (result == null) {
            return;
        }

        String fieldPrefix = "";

        switch (timeControl) {
            case BLITZ:
                fieldPrefix = "blitz";
                break;

            case RAPID:
                fieldPrefix = "rapid";
                break;

            case CLASSICAL:
                fieldPrefix = "classical";
                break;
        }

        if (fieldPrefix.length() == 0) {
            return;
        }

        int arena_gp = result.getInteger(fieldPrefix + "_score_gp");
        int swiss_gp = result.getInteger(fieldPrefix + "_score_swiss_gp");
        int sum_gp = arena_gp + swiss_gp;
        collection.updateOne(query, Updates.set(fieldPrefix + "_comb_total_gp", sum_gp));
    }



    /**
     * Sets the value for the given player and field name.
     *
     * @param playerName The cc username of the player to update.
     * @param value      The new value to set on the player.
     * @param fieldName  The field name to set.
     * @param collection The collection containing the player.
     *
     *
     */
    private void updatePlayer(String playerName, int value, String fieldName,
                              MongoCollection<Document> collection) {
        Document query = new Document("Chesscomname", playerName);

        if (collection.countDocuments() <= 0) {
            System.out.println("Player not found: " + playerName);
            return;
        }

        collection.updateOne(query, Updates.inc(fieldName, value));
        System.out.println("Value updated for player: " + playerName + " and field: " + fieldName);
    }

    /**
     * A cross collection chess.com to Lichess algorithm to add Chess.com user's
     * linked Lichess score with Chess.com score
     * @param playerName player name
     * @param value score
     * @param fieldName field to update to
     * @param chesscom chess.com collection
     * @param lichess Lichess collection
     */

    private void findPlayerToUpateLichessScores(String playerName, int value, String fieldName,
                                                MongoCollection<Document> chesscom, MongoCollection<Document> lichess){
        Document query = new Document("Chesscomname", playerName);

        if(chesscom.find(query).first() != null){
            String searchID = query.getString("Discordid");

            Document searchTheId = new Document("Discordid", searchID);


            if(lichess.find(searchTheId).first() != null){
                lichess.updateOne(searchTheId, Updates.inc(fieldName, value));
            }else{
                System.out.println("The Player has not linked Lichess account!");
            }
        }else{
            System.out.println("The Player is not present in Chess.com Collection!");
        }



    }


    private void updatePlayerSwiss(String playerName, double value, String fieldName,
                                   MongoCollection<Document> collection) {
        Document query = new Document("Chesscomname", playerName);

        if (collection.countDocuments() <= 0) {
            System.out.println("Player not found: " + playerName);
            return;
        }

        collection.updateOne(query, Updates.inc(fieldName, value));
        System.out.println("Value updated for player: " + playerName + " and field: " + fieldName);
    }


    private void updatePlayerr(String playerName, int value, String fieldName,
                               MongoCollection<Document> collection) {
        Document query = new Document("Chesscomname", playerName);

        if (collection.countDocuments() <= 0) {
            System.out.println("Player not found: " + playerName);
            return;
        }

        collection.updateOne(query, Updates.set(fieldName, value));
        System.out.println("Value updated for player: " + playerName + " and field: " + fieldName);
    }


    /**
     * Sets the rating for the given player and time control.
     *
     * @param playerName  The cc username of the player to update.
     * @param rating      The new rating to set on the player.
     * @param collection  The collection of players to update.
     * @param timeControl The time control to set the score for.
     */
    public void updatePlayerRatings(String playerName, int rating, MongoCollection<Document> collection,
                                    Time_Control timeControl) {
        updatePlayerr(playerName, rating, timeControl.toString() + "_rating", collection);
    }



    /**
     * Sets the Swiss Grand Prix score for the given player and time control.
     *
     * @param playerName  The cc username of the player to update.
     * @param newScore    The new Swiss Grand Prix score to set on the player.
     * @param collection  The collection of players to update.
     * @param timeControl The time control to set the score for.
     */
    public void updatePlayerScoresSwissGp(String playerName, int newScore, MongoCollection<Document> collection,
                                          Time_Control timeControl) {
        updatePlayer(playerName, newScore, timeControl.toString() + "_score_swiss_gp", collection);
    }

    /**
     * Sets the Arena Grand Prix score for the given player and time control.
     *
     * @param playerName  The cc username of the player to update.
     * @param newScore    The new Arena Grand Prix score to set on the player.
     * @param collection  The collection of players to update.
     * @param timeControl The time control to set the score for.
     */
    public void updatePlayerScoresArenaGp(String playerName, int newScore, MongoCollection<Document> collection,
                                          Time_Control timeControl) {
        updatePlayer(playerName, newScore, timeControl.toString() + "_score_gp", collection);
        findPlayerToUpateLichessScores(playerName, newScore, timeControl.toString() + "_score_gp", collection, MongoConnect.getLichessplayers());
    }

    /**
     * Sets the Arena score for the given player and time control.
     *
     * @param playerName  The cc username of the player to update.
     * @param newScore    The new score to set on the player.
     * @param collection  The collection of players to update.
     * @param timeControl The time control to set the score for.
     */
    public void updatePlayerScores(String playerName, int newScore, MongoCollection<Document> collection,
                                   Time_Control timeControl) {
        updatePlayer(playerName, newScore, timeControl.toString() + "_score", collection);
        findPlayerToUpateLichessScores(playerName, newScore, timeControl.toString() + "_score", collection, MongoConnect.getLichessplayers());

    }

    /**
     * Sets the Swiss score for the given player and time control.
     *
     * @param playerName  The cc username of the player to update.
     * @param newScore    The new Swiss score to set on the player.
     * @param collection  The collection of players to update.
     * @param timeControl The time control to set the score for.
     */
    public void updatePlayerSwissScores(String playerName, double newScore, MongoCollection<Document> collection,
                                        Time_Control timeControl) {
        updatePlayerSwiss(playerName, newScore, timeControl.toString() + "_score_swiss", collection);
    }

    /**
     * Sets the Middlegame Sparring score for the given player
     *
     * @param playerName the cc username of the player to update
     * @param newScore   the new score to set on the player
     * @param collection the collection of players to update
     */

    public void updateSparringScores(String playerName, double newScore, MongoCollection<Document> collection) {
        updatePlayerSwiss(playerName, newScore, "sp_score", collection);
    }

    /**
     * Sets the Endgame Sparring score for the given player
     *
     * @param playerName the cc username of the player to update
     * @param newScore   the new score to set on the player
     * @param collection the collection of players to update
     */

    public void updateEndgameSparringScores(String playerName, double newScore, MongoCollection<Document> collection) {
        updatePlayerSwiss(playerName, newScore, "eg_score", collection);
    }



    /**
     * Calculates and saves the player scores for the provided tournament URL.
     *
     * @param tournamentUrl   The tournament to calculate scores for.
     * @param arenaCollection The collection of tournaments tracked in the
     *                        arena collection in the database.
     * @param swissCollection The collection of tournaments tracked in the
     *                        swiss collection in the database.
     * @return A String description of the calculation result.
     */
    public String calculatePlayerScores(String tournamentUrl, MongoCollection<Document> arenaCollection,
                                        MongoCollection<Document> swissCollection) throws ChessComPubApiException, IOException {
       if(tournamentUrl.contains("https://www.chess.com/tournament/live/arena/")){
           return calculateArenaScores(tournamentUrl, arenaCollection);
       }else if(tournamentUrl.contains("https://www.chess.com/tournament/live/")){
           return calculateSwissScores(tournamentUrl, swissCollection);
       }

       return "Invalid tournament URL! Please provide only chess.com URLs!";
    }

    /**
     * Calculates the scores for the Arena with the provided URL and tournament
     * collection.
     *
     * @param arenaUrl             The URL of the Arena to calculate scores for.
     * @param tournamentCollection The collection of tournaments tracked in the
     *                             database.
     * @return A String description of the calculation result.
     */
    private String calculateArenaScores(String arenaUrl, MongoCollection<Document> tournamentCollection) throws ChessComPubApiException, IOException {

        String arenaID = arenaUrl.split("arena/")[1];

        TournamentClient client = new TournamentClient();

        System.out.println(arenaID);
        if (isTournamentIDresent(arenaID, MongoConnect.getComputedId())) {
            return "This Tournament is already computed! Try another tournament";
        }

        String tc = client.getTournamentByUrlId(arenaID).getSettings().getTimeClass().getValue().toLowerCase();

        String passer;

        if(tc.equalsIgnoreCase("standard")){
            passer = arenaID;
        }else{
            passer = tc;
        }

        Time_Control timeControl = Time_Control.fromString(passer);
        if (timeControl == null) {
            System.out.println("Time Control Found Null Switching based on url");
            if(arenaID.contains("friday night fights")){
                timeControl = Time_Control.CLASSICAL;
            }else {
                timeControl = Time_Control.RAPID;
            }
        }


        // Standard

        TournamentRound round = client.getTournamentRound(arenaID, 1);

        List<TournamentPlayer> players = round.getPlayers();

        players.sort((player1, player2) -> Integer.compare(player2.getPoints().intValue(), player1.getPoints().intValue()));

        if(arenaID.contains("middlegame") || arenaID.contains("MIDDLEGAME") || arenaID.contains("Middlegame")){
            // middlegame logic

            for (TournamentPlayer player : players) {
                int rating = new CCProfile(player.getUsername()).getRatingBasedOnTimeControl(timeControl);
                checkUserInDateBase(player.getUsername().toLowerCase(), MongoConnect.getChesscomplayers());
                updateSparringScores(player.getUsername().toLowerCase(), player.getPoints().intValue(),
                        MongoConnect.getChesscomplayers());
                updatePlayerRatings(player.getUsername().toLowerCase(), rating,
                        MongoConnect.getChesscomplayers(), Time_Control.MIX);
            }
            insertTournamentID(arenaID, MongoConnect.getComputedId());
            updateStandingsOnDojoScoreBoard(Time_Control.MIX, Type.SPARRING, MongoConnect.getChesscomplayers());

            return "Success! Updated player scores for " + arenaID.replace("-", " ");
        }

        if(arenaID.contains("endgame") || arenaID.contains("ENDGAME") || arenaID.contains("Endgame")){
            //endgame logic


            for (TournamentPlayer player : players) {
                int rating = new CCProfile(player.getUsername()).getRatingBasedOnTimeControl(timeControl);
                checkUserInDateBase(player.getUsername().toLowerCase(), MongoConnect.getChesscomplayers());
                updateEndgameSparringScores(player.getUsername().toLowerCase(), player.getPoints().intValue(),
                        MongoConnect.getChesscomplayers());
                updatePlayerRatings(player.getUsername().toLowerCase(), rating,
                        MongoConnect.getChesscomplayers(), Time_Control.MIX_ENDGAME);
            }
            insertTournamentID(arenaID, MongoConnect.getComputedId());
            updateStandingsOnDojoScoreBoard(Time_Control.MIX_ENDGAME, Type.SPARRING, MongoConnect.getChesscomplayers());

            return "Success! Updated player scores for " + arenaID.replace("-", " ");
        }

        // standard logic

        if (players.size() >= 10) {

            for (int g = 0; g < 10; g++) {
                checkUserInDateBase(players.get(g).getUsername().toLowerCase(), MongoConnect.getChesscomplayers());
                updatePlayerScoresArenaGp(players.get(g).getUsername().toLowerCase(), 10 - g,
                        MongoConnect.getChesscomplayers(), timeControl);
            }
        } else {
            for (int g = 0; g < players.size(); g++) {
                checkUserInDateBase(players.get(g).getUsername().toLowerCase(), MongoConnect.getChesscomplayers());
                updatePlayerScoresArenaGp(players.get(g).getUsername().toLowerCase(), 10 - g,
                        MongoConnect.getChesscomplayers(), timeControl);
            }
        }

        for (TournamentPlayer player : players) {
            try{
                int rating = new CCProfile(player.getUsername()).getRatingBasedOnTimeControl(timeControl);
                checkUserInDateBase(player.getUsername().toLowerCase(), MongoConnect.getChesscomplayers());
                updatePlayerScores(player.getUsername().toLowerCase(), player.getPoints().intValue(),
                        MongoConnect.getChesscomplayers(), timeControl);
                updatePlayerRatings(player.getUsername().toLowerCase(), rating,
                        MongoConnect.getChesscomplayers(), timeControl);
                addCombinedPlayerTotalScores(player.getUsername().toLowerCase(),MongoConnect.getChesscomplayers(),
                        timeControl);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }
        }

        insertTournamentID(arenaID, MongoConnect.getComputedId());
        updateStandingsOnDojoScoreBoard(timeControl, Type.ARENA, MongoConnect.getChesscomplayers());
        updateStandingsOnDojoScoreBoard(timeControl, Type.COMB_GRAND_PRIX, MongoConnect.getChesscomplayers());


        return "Success! Updated player scores for " + arenaID.replace("-", " ");



    }

    /**
     * Calculates the scores for the Swiss tournament with the provided URL and
     * tournament
     * collection.
     *
     * @param swissUrl             The URL of the Swiss tournament to calculate
     *                             scores for.
     * @param tournamentCollection The collection of tournaments tracked in the
     *                             database.
     * @return A String description of the calculation result.
     */
    private String calculateSwissScores(String swissUrl, MongoCollection<Document> tournamentCollection) throws ChessComPubApiException, IOException {
       try{
           String live = swissUrl.split("live/")[1];
           System.out.println(live);
           TournamentClient client = new TournamentClient();


           if (isTournamentIDresent(live, MongoConnect.getComputedId())) {
               return "This Tournament is already computed! Try another tournament";
           }

           String tc = client.getTournamentByUrlId(live).getSettings().getTimeClass().getValue().toLowerCase();

           String passer;

           if(tc.equalsIgnoreCase("standard")){
               passer = live;
           }else{
               passer = tc;
           }

           Time_Control timeControl = Time_Control.fromString(passer);
           if (timeControl == null) {
               return "Invalid time control: " + null;
           }


           int rounds = client.getTournamentByUrlId(live).getSettings().getTotalRounds();

           if(!(client.getTournamentRound(live, rounds).getGroupsApiUrls().size() > 1)){
               return "Invalid pairings size!";
           }

           String url = client.getTournamentRound(live, rounds).getGroupsApiUrls().getFirst();

           List<TournamentPlayer> pl = client.getTournamentRoundGroupByApiUrl(url).getPlayers();

           System.out.println(url);
           System.out.println(rounds);
           System.out.println(pl);

           pl.sort((player1, player2) -> Double.compare(player2.getPoints().doubleValue(), player1.getPoints().doubleValue()));

           // middlegame


           if(live.contains("middlegame") || live.contains("MIDDLEGAME") || live.contains("Middlegame")){
               // middlegame logic

               for (TournamentPlayer player : pl) {
                   int rating = new CCProfile(player.getUsername()).getRatingBasedOnTimeControl(timeControl);
                   checkUserInDateBase(player.getUsername().toLowerCase(), MongoConnect.getChesscomplayers());
                   updateSparringScores(player.getUsername().toLowerCase(), player.getPoints().doubleValue(),
                           MongoConnect.getChesscomplayers());
                   updatePlayerRatings(player.getUsername().toLowerCase(), rating,
                           MongoConnect.getChesscomplayers(), Time_Control.MIX);
               }
               insertTournamentID(live, MongoConnect.getComputedId());
               updateStandingsOnDojoScoreBoard(Time_Control.MIX, Type.SPARRING, MongoConnect.getChesscomplayers());

               return "Success! Updated player scores for " + live.replace("-", " ");
           }


           // endgame

           if(live.contains("endgame") || live.contains("ENDGAME") || live.contains("Endgame")){

               for (TournamentPlayer player : pl) {
                   int rating = new CCProfile(player.getUsername()).getRatingBasedOnTimeControl(timeControl);
                   checkUserInDateBase(player.getUsername().toLowerCase(), MongoConnect.getChesscomplayers());
                   updateEndgameSparringScores(player.getUsername().toLowerCase(), player.getPoints().doubleValue(),
                           MongoConnect.getChesscomplayers());
                   updatePlayerRatings(player.getUsername().toLowerCase(), rating,
                           MongoConnect.getChesscomplayers(), Time_Control.MIX_ENDGAME);
               }
               insertTournamentID(live, MongoConnect.getComputedId());
               updateStandingsOnDojoScoreBoard(Time_Control.MIX_ENDGAME, Type.SPARRING, MongoConnect.getChesscomplayers());

               return "Success! Updated player scores for " + live.replace("-", " ");
           }

           //standard

           if (pl.size() >= 10) {

               for (int g = 0; g < 10; g++) {
                   checkUserInDateBase(pl.get(g).getUsername().toLowerCase(), MongoConnect.getChesscomplayers());
                   updatePlayerScoresSwissGp(pl.get(g).getUsername().toLowerCase(), 10 - g,
                           MongoConnect.getChesscomplayers(), timeControl);
               }
           } else {
               for (int g = 0; g < pl.size(); g++) {
                   checkUserInDateBase(pl.get(g).getUsername().toLowerCase(), MongoConnect.getChesscomplayers());
                   updatePlayerScoresArenaGp(pl.get(g).getUsername().toLowerCase(), 10 - g,
                           MongoConnect.getChesscomplayers(), timeControl);
               }
           }

           for (TournamentPlayer player : pl) {
               try{
                   int rating = new CCProfile(player.getUsername()).getRatingBasedOnTimeControl(timeControl);
                   checkUserInDateBase(player.getUsername().toLowerCase(), MongoConnect.getChesscomplayers());
                   updatePlayerSwissScores(player.getUsername().toLowerCase(), player.getPoints().doubleValue(),
                           MongoConnect.getChesscomplayers(), timeControl);
                   updatePlayerRatings(player.getUsername().toLowerCase(), rating,
                           MongoConnect.getChesscomplayers(), timeControl);
                   addCombinedPlayerTotalScores(player.getUsername().toLowerCase(),MongoConnect.getChesscomplayers(),
                           timeControl);
               }catch (Exception e){
                   System.out.println(e.getMessage());
               }
           }

           insertTournamentID(live, MongoConnect.getComputedId());
           updateStandingsOnDojoScoreBoard(timeControl, Type.ARENA, MongoConnect.getChesscomplayers());
           updateStandingsOnDojoScoreBoard(timeControl, Type.COMB_GRAND_PRIX, MongoConnect.getChesscomplayers());


           return "Success! Updated player scores for " + live.replace("-", " ");
       }catch (Exception e){
           return e.getMessage();
       }
    }



    /**
     * A non return method to check given user is present in players collection, if
     * not create the player entry if already present than check the prov fields
     *
     * @param playerName Lichess username of the player
     * @param collection collection of players in the database
     */

    public void checkUserInDateBase(String playerName, MongoCollection<Document> collection) {

        Document query = new Document("Chesscomname", playerName);

        if (!(collection.countDocuments(query) > 0)) {
            ChessPlayer player = new ChessPlayer(playerName, "null", 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0,
                    0, 0, 0,
                    0, 0, 0, 0,
                    0, 0);
            Document document = new Document("Chesscomname", player.getLichessname())
                    .append("Discordid", player.getDiscordId())
                    .append("blitz_score", 0)
                    .append("rapid_score", 0)
                    .append("blitz_rating", 0)
                    .append("rapid_rating", 0).append("blitz_score_gp", 0)
                    .append("rapid_score_gp", 0)
                    .append("blitz_score_swiss", 0.0).append("rapid_score_swiss", 0.0)
                    .append("blitz_score_swiss_gp", 0)
                    .append("rapid_score_swiss_gp", 0)
                    .append("blitz_comb_total", 0).append("blitz_comb_total_gp", 0)
                    .append("rapid_comb_total", 0).append("rapid_comb_total_gp", 0)
                    .append("sp_score", 0.0)
                    .append("sparring_rating", 0).append("eg_score", 0.0)
                    .append("eg_rating", 0);
            collection.insertOne(document);
        }else{
           System.out.println("Player Already present!");
        }



    }


    public static List<String> compareCollections(MongoCollection<Document> collectionA, MongoCollection<Document> collectionB, String target) {
        List<String> idList = new ArrayList<>();

        // Get all IDs from collection B
        List<String> idsInCollectionB = new ArrayList<>();
        collectionB.find().forEach(document -> idsInCollectionB.add(document.getString("tournament-id")));

        // Check IDs from collection A against IDs in collection B
        collectionA.find().forEach(document -> {
            String idFromA = document.getString(target);
            // If the ID from collection A is not present in collection B, add it to the list
            if (!idsInCollectionB.contains(idFromA)) {
                idList.add(idFromA);
            }
        });

        return idList;
    }


    /**
     * update standing on the Dojoscoreboard by sending the time control,
     * leaderboard type, and players collection
     *
     * @param timeControl time control for the leaderboard
     * @param type        the type of league/leaderboard
     * @param collection  the collection of players
     */

    public void updateStandingsOnDojoScoreBoard(Time_Control timeControl, Type type,
                                                MongoCollection<Document> collection) {
        switch (type) {
            case ARENA, SWISS, COMB_GRAND_PRIX -> DojoScoreboard.updateLeaderboardCC(timeControl, type.getName(),
                    collection, timeControl + type.toString());

            case SPARRING, SPARRING_ENDGAME ->
                    DojoScoreboard.updateLeaderboardCC(timeControl, type.getName(), collection, type.toString());

        }
    }




}
