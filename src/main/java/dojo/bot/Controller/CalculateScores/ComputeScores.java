
package dojo.bot.Controller.CalculateScores;

import chariot.Client;
import chariot.ClientAuth;
import chariot.model.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dojo.bot.Controller.User.Profile;
import dojo.bot.Controller.DojoScoreboard.DojoScoreboard;
import dojo.bot.Controller.League.Mode;
import dojo.bot.Controller.League.Time_Control;
import dojo.bot.Controller.League.Type;
import dojo.bot.Controller.User.ChessPlayer;
import dojo.bot.Runner.Main;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import dojo.bot.Controller.Database.MongoConnect;
import java.awt.*;
import java.io.IOException;
import java.util.List;

import static dojo.bot.Controller.CalculateScores.ScoresUtil.*;
import static dojo.bot.Controller.CalculateScores.ScoresUtil.getTeamLinearIndex;

public class ComputeScores {

    private final ClientAuth client = Client.auth(Main.botToken);

    /**
     * Creates a new ComputeScores object.
     */
    public ComputeScores() {
    }

    /**
     * Sets the provided player's total combined score for the given time control.
     *
     * @param playerName  The Lichess username of the player to update.
     * @param collection  The collection containing the player to update.
     * @param timeControl The time control to update the player's score for.
     */
    public void addCombinedPlayerTotalScores(String playerName, MongoCollection<Document> collection,
                                             Time_Control timeControl) {
        Document query = new Document("Lichessname", playerName);
        Document result = collection.find(query).first();

        if (result == null) {
            return;
        }

        String fieldPrefix = switch (timeControl) {
            case BLITZ -> "blitz";
            case RAPID -> "rapid";
            case CLASSICAL -> "classical";
            default -> "";
        };

        if (fieldPrefix.length() == 0) {
            return;
        }

        int arena_gp = result.getInteger(fieldPrefix + "_score_gp");
        int swiss_gp = result.getInteger(fieldPrefix + "_score_swiss_gp");
        int sum_gp = arena_gp + swiss_gp;
        collection.updateOne(query, Updates.set(fieldPrefix + "_comb_total_gp", sum_gp));
    }

    private void updatePlayerr(String playerName, int value, String fieldName,
                               MongoCollection<Document> collection) {
        Document query = new Document("Lichessname", playerName);

        if (collection.countDocuments() <= 0) {
            System.out.println("Player not found: " + playerName);
            return;
        }

        collection.updateOne(query, Updates.set(fieldName, value));
        System.out.println("Value updated for player: " + playerName + " and field: " + fieldName);
    }



    public static void checkProv(String username, MongoCollection<Document> collection){
        Document query = new Document("Lichessname", username);
        Document playerDocument = collection.find(query).first();

        if (playerDocument != null) {


            // Perform your logic to determine values for new fields
            boolean provRapidValue = false;
            boolean provBlitzValue = false;
            boolean provClaValue = false;

            if(Client.basic().users().byId(username).isPresent() &&
                    !Client.basic().users().byId(username).get().tosViolation()
                    && !Client.basic().users().byId(username).get().disabled()){
                Profile profile = new Profile(Client.basic(), username);

                if(profile.getSingleBlitzRating() == -1){
                    provBlitzValue = true;
                }

                if(profile.getSingleClassicalRating() == -1){
                    provClaValue = true;
                }

                if(profile.getSingleRapidRating() == -1){
                    provRapidValue = true;
                }
            }
            // Add or update the new fields based on conditions
            Document updateFields = new Document();
            updateFields.put("prov_rapid", provRapidValue);
            updateFields.put("prov_blitz", provBlitzValue);
            updateFields.put("prov_cla", provClaValue);

            // Update the document with new fields
            collection.updateOne(query, new Document("$set", updateFields));

            System.out.println("Fields updated for player: " + username);
        } else {
            System.out.println("Player not found: " + username);
        }
    }




    /**
     * Sets the value for the given player and field name.
     *
     * @param playerName The Lichess username of the player to update.
     * @param value      The new value to set on the player.
     * @param fieldName  The field name to set.
     * @param collection The collection containing the player.
     *
     *
     */
    private void updatePlayer(String playerName, int value, String fieldName,
                              MongoCollection<Document> collection) {
        Document query = new Document("Lichessname", playerName);

        if (collection.countDocuments() <= 0) {
            System.out.println("Player not found: " + playerName);
            return;
        }

        collection.updateOne(query, Updates.inc(fieldName, value));
        System.out.println("Value updated for player: " + playerName + " and field: " + fieldName);
    }

    private void updatePlayerSwiss(String playerName, double value, String fieldName,
                                   MongoCollection<Document> collection) {
        Document query = new Document("Lichessname", playerName);

        if (collection.countDocuments() <= 0) {
            System.out.println("Player not found: " + playerName);
            return;
        }

        collection.updateOne(query, Updates.inc(fieldName, value));
        System.out.println("Value updated for player: " + playerName + " and field: " + fieldName);
    }

    /**
     * Sets the rating for the given player and time control.
     *
     * @param playerName  The Lichess username of the player to update.
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
     * @param playerName  The Lichess username of the player to update.
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
     * @param playerName  The Lichess username of the player to update.
     * @param newScore    The new Arena Grand Prix score to set on the player.
     * @param collection  The collection of players to update.
     * @param timeControl The time control to set the score for.
     */
    public void updatePlayerScoresArenaGp(String playerName, int newScore, MongoCollection<Document> collection,
                                          Time_Control timeControl) {
        updatePlayer(playerName, newScore, timeControl.toString() + "_score_gp", collection);
    }

    /**
     * Sets the score for the given player and time control.
     *
     * @param playerName  The Lichess username of the player to update.
     * @param newScore    The new score to set on the player.
     * @param collection  The collection of players to update.
     * @param timeControl The time control to set the score for.
     */
    public void updatePlayerScores(String playerName, int newScore, MongoCollection<Document> collection,
                                   Time_Control timeControl) {
        updatePlayer(playerName, newScore, timeControl.toString() + "_score", collection);
    }

    /**
     * Sets the Swiss score for the given player and time control.
     *
     * @param playerName  The Lichess username of the player to update.
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
     * @param playerName the Lichess username of the player to update
     * @param newScore   the new score to set on the player
     * @param collection the collection of players to update
     */

    public void updateSparringScores(String playerName, double newScore, MongoCollection<Document> collection) {
        updatePlayerSwiss(playerName, newScore, "sp_score", collection);
    }

    /**
     * Sets the Endgame Sparring score for the given player
     *
     * @param playerName the Lichess username of the player to update
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
        ComputeScorescc cc = new ComputeScorescc();

        if (tournamentUrl.contains("https://lichess.org/tournament/")) {
            return calculateArenaScores(tournamentUrl, arenaCollection);
        } else if (tournamentUrl.contains("https://lichess.org/swiss/")) {
            return calculateSwissScores(tournamentUrl, swissCollection);
        } else if(tournamentUrl.contains("https://www.chess.com")){
            return cc.calculatePlayerScores(tournamentUrl, arenaCollection, swissCollection);
        }

        return "Invalid tournament URL: " + tournamentUrl;
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
    private String calculateArenaScores(String arenaUrl, MongoCollection<Document> tournamentCollection) {
        String[] spliturl = arenaUrl.split("tournament/");
        String touryID = spliturl[1];

        One<chariot.model.Arena> arenaResult1 = client.tournaments().arenaById(touryID);

        if (!arenaResult1.isPresent()) {
            return "Seems like this arena is not present :( Can't compute player scores.";
        }

        if (tournamentCollection.countDocuments() <= 0) {
            return "Can't calculate scores since you did not create an Arena League!";
        }
        if (tournamentPresent(tournamentCollection, touryID)) {
            return "This tournament is not in the current arena League! Please double check tournament URL and check ChessDojo team \n" +
                    "if you are trying to compute Lichess liga results run /inject [URL] to inject the URL in the database.";
        }

        if (isTournamentIDresent(touryID, MongoConnect.getComputedId())) {
            return "This Tournament is already computed! Try another tournament";
        }


        Time_Control timeControl = Time_Control.fromString(arenaResult1.get().perf().key());
        if (timeControl == null) {
            return "Invalid time control: " + arenaResult1.get().perf().key();
        }

        if (arenaResult1.get().fullName().contains("Lichess Liga") || arenaResult1.get().fullName().contains("Lichess Mega")) {

            List<chariot.model.TeamBattleResults.Teams.Player> allPlayers = null;

            if(arenaResult1.get().fullName().contains("Lichess Liga")){
                int teamindex = getTeamLinearIndex(touryID, client);
                System.out.println(teamindex);
                allPlayers = client.tournaments()
                        .teamBattleResultsById(touryID).get().teams().get(teamindex).players();
            }else if(arenaResult1.get().fullName().contains("Lichess Mega")){
                int teamindex = getTeamIndex(touryID, client);
                System.out.println(teamindex);
                allPlayers = client.tournaments()
                        .teamBattleResultsById(touryID).get().teams().get(teamindex - 1).players();
            }

            assert allPlayers != null;
            if (allPlayers.size() >= 10) {
                for (int g = 0; g < 10; g++) {
                    checkUserInDateBase(allPlayers.get(g).user().name().toLowerCase(), MongoConnect.getLichessplayers());
                    updatePlayerScoresArenaGp(allPlayers.get(g).user().name().toLowerCase(), 10 - g,
                            MongoConnect.getLichessplayers(), timeControl);
                }
            } else {
                for (int g = 0; g < allPlayers.size(); g++) {
                    checkUserInDateBase(allPlayers.get(g).user().name().toLowerCase(), MongoConnect.getLichessplayers());
                    updatePlayerScoresArenaGp(allPlayers.get(g).user().name().toLowerCase(), 10 - g,
                            MongoConnect.getLichessplayers(), timeControl);
                }
            }

            for (chariot.model.TeamBattleResults.Teams.Player player : allPlayers) {
                checkUserInDateBase(player.user().name().toLowerCase(), MongoConnect.getLichessplayers());
                updatePlayerScores(player.user().name().toLowerCase(), player.score(),
                        MongoConnect.getLichessplayers(), timeControl);
                updatePlayerRatings(player.user().name().toLowerCase(), new Profile(client,player.user().name().toLowerCase()).getSingleBlitzRating(), MongoConnect.getLichessplayers(), timeControl);
                addCombinedPlayerTotalScores(player.user().name().toLowerCase(), MongoConnect.getLichessplayers(),
                        timeControl);

            }

            insertTournamentID(touryID, MongoConnect.getComputedId());
            updateStandingsOnDojoScoreBoard(timeControl, Type.ARENA, Mode.OPEN , MongoConnect.getLichessplayers());
            updateStandingsOnDojoScoreBoard(timeControl, Type.COMB_GRAND_PRIX, Mode.OPEN ,MongoConnect.getLichessplayers());

            return "Success! Updated player scores for " + arenaResult1.get().fullName();

        }

        if (arenaResult1.get().fullName().contains("middlegame") || arenaResult1.get().fullName().contains("Middlegame")
                || arenaResult1.get().fullName().contains("MIDDLEGAME")) {

            List<ArenaResult> allPlayers = client.tournaments()
                    .resultsByArenaId(touryID, params -> params.max(arenaResult1.get().nbPlayers()))
                    .stream().toList();

            for (ArenaResult player : allPlayers) {
                checkUserInDateBase(player.username().toLowerCase(), MongoConnect.getLichessplayers());
                updateSparringScores(player.username().toLowerCase(), player.score(), MongoConnect.getLichessplayers());
                updatePlayerRatings(player.username().toLowerCase(), player.rating(), MongoConnect.getLichessplayers(),
                        Time_Control.MIX);

            }

            insertTournamentID(touryID, MongoConnect.getComputedId());
            updateStandingsOnDojoScoreBoard(Time_Control.MIX, Type.SPARRING, Mode.OPEN, MongoConnect.getLichessplayers());

            return "Success! Updated player scores for " + arenaResult1.get().fullName();

        }

        if (arenaResult1.get().fullName().contains("endgame") || arenaResult1.get().fullName().contains("Endgame")
                || arenaResult1.get().fullName().contains("ENDGAME")) {

            List<ArenaResult> allPlayers = client.tournaments()
                    .resultsByArenaId(touryID, params -> params.max(arenaResult1.get().nbPlayers()))
                    .stream().toList();

            for (ArenaResult player : allPlayers) {
                checkUserInDateBase(player.username().toLowerCase(), MongoConnect.getLichessplayers());
                updateEndgameSparringScores(player.username().toLowerCase(), player.score(), MongoConnect.getLichessplayers());
                updatePlayerRatings(player.username().toLowerCase(), player.rating(), MongoConnect.getLichessplayers(),
                        Time_Control.MIX_ENDGAME);

            }

            insertTournamentID(touryID, MongoConnect.getComputedId());
            updateStandingsOnDojoScoreBoard(Time_Control.MIX_ENDGAME, Type.SPARRING_ENDGAME, Mode.OPEN ,MongoConnect.getLichessplayers());

            return "Success! Updated player scores for " + arenaResult1.get().fullName();

        }

        List<ArenaResult> allPlayers = client.tournaments()
                .resultsByArenaId(touryID, params -> params.max(arenaResult1.get().nbPlayers()))
                .stream().toList();

        if (allPlayers.size() >= 10) {

            for (int g = 0; g < 10; g++) {
                checkUserInDateBase(allPlayers.get(g).username().toLowerCase(), MongoConnect.getLichessplayers());
                updatePlayerScoresArenaGp(allPlayers.get(g).username().toLowerCase(), 10 - g,
                        MongoConnect.getLichessplayers(), timeControl);
            }
        } else {
            for (int g = 0; g < allPlayers.size(); g++) {
                checkUserInDateBase(allPlayers.get(g).username().toLowerCase(), MongoConnect.getLichessplayers());
                updatePlayerScoresArenaGp(allPlayers.get(g).username().toLowerCase(), 10 - g,
                        MongoConnect.getLichessplayers(), timeControl);
            }
        }

        for (ArenaResult player : allPlayers) {
            checkUserInDateBase(player.username().toLowerCase(), MongoConnect.getLichessplayers());
            updatePlayerScores(player.username().toLowerCase(), player.score(),
                    MongoConnect.getLichessplayers(), timeControl);
            updatePlayerRatings(player.username().toLowerCase(), player.rating(),
                    MongoConnect.getLichessplayers(), timeControl);
            addCombinedPlayerTotalScores(player.username().toLowerCase(), MongoConnect.getLichessplayers(),
                    timeControl);
        }

        insertTournamentID(touryID, MongoConnect.getComputedId());
        updateStandingsOnDojoScoreBoard(timeControl, Type.ARENA, Mode.OPEN ,MongoConnect.getLichessplayers());
        updateStandingsOnDojoScoreBoard(timeControl, Type.COMB_GRAND_PRIX, Mode.OPEN ,MongoConnect.getLichessplayers());

        return "Success! Updated player scores for " + arenaResult1.get().fullName();
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
    private String calculateSwissScores(String swissUrl, MongoCollection<Document> tournamentCollection) {
        String[] splitswiss = swissUrl.split("swiss/");
        String touryIDSwiss = splitswiss[1];

        if (tournamentCollection.countDocuments() <= 0) {
            return "Can't calculate scores since you did not create an Swiss League!";
        }

        if (tournamentPresent(tournamentCollection, touryIDSwiss) || tournamentPresent(MongoConnect.getU1800swissCollection(), touryIDSwiss)) {
            return "This tournament is not in the current Swiss League! Please double check tournament URL and check ChessDojo team";
        }

        if (isTournamentIDresent(touryIDSwiss, MongoConnect.getComputedId())) {
            return "This Tournament is already computed! Try another tournament";
        }

        One<Swiss> swissOne = client.tournaments().swissById(touryIDSwiss);
        System.out.println(swissOne);

        if (!swissOne.isPresent()) {
            return "This swiss tournament is not present or its forced canceled";
        }

        if (swissOne.get().name().contains("middlegame") || swissOne.get().name().contains("Middlegame")
                || swissOne.get().name().contains("MIDDLEGAME")) {

            Many<SwissResult> swissResultResult = client.tournaments().resultsBySwissId(touryIDSwiss);
            List<SwissResult> results = swissResultResult.stream().toList();

            for (SwissResult result : results) {
                checkUserInDateBase(result.username().toLowerCase(), MongoConnect.getLichessplayers());
                updateSparringScores(result.username(), result.points(), MongoConnect.getLichessplayers());


            }
            insertTournamentID(touryIDSwiss, MongoConnect.getComputedId());
            updateStandingsOnDojoScoreBoard(Time_Control.MIX, Type.SPARRING, Mode.OPEN ,MongoConnect.getLichessplayers());

            return "Success! Updated player scores for " + swissOne.get().name();

        }

        if (swissOne.get().name().contains("endgame") || swissOne.get().name().contains("Endgame")
                || swissOne.get().name().contains("ENDGAME")) {

            Many<SwissResult> swissResultResult = client.tournaments().resultsBySwissId(touryIDSwiss);
            List<SwissResult> results = swissResultResult.stream().toList();

            for (SwissResult result : results) {
                checkUserInDateBase(result.username().toLowerCase(), MongoConnect.getLichessplayers());
                updateEndgameSparringScores(result.username(), result.points(),
                        MongoConnect.getLichessplayers());

            }
            insertTournamentID(touryIDSwiss, MongoConnect.getComputedId());
            updateStandingsOnDojoScoreBoard(Time_Control.MIX_ENDGAME, Type.SPARRING_ENDGAME, Mode.OPEN ,MongoConnect.getLichessplayers());

            return "Success! Updated player scores for " + swissOne.get().name();

        }

        Many<SwissResult> swissResultResult = client.tournaments().resultsBySwissId(touryIDSwiss);
        List<SwissResult> results = swissResultResult.stream().toList();

        Time_Control timeControl;

        if (swissOne.get().clock().limit() >= 180 && swissOne.get().clock().limit() <= 480) {
            timeControl = Time_Control.BLITZ;
        } else if (swissOne.get().clock().limit() >= 540 && swissOne.get().clock().limit() <= 1740) {
            timeControl = Time_Control.RAPID;
        } else if (swissOne.get().clock().limit() >= 1800 && swissOne.get().clock().limit() <= 10800) {
            timeControl = Time_Control.CLASSICAL;
        } else {
            return "Error time control not supported! Time control must be Blitz, Rapid, Classical";
        }

        MongoCollection<Document> swissCollection = (tournamentPresent(MongoConnect.getU1800swissCollection(), touryIDSwiss)) ? MongoConnect.getLichessplayers() : MongoConnect.getU1800Playerswiss();
        Mode mode = (tournamentPresent(MongoConnect.getU1800swissCollection(), touryIDSwiss)) ? Mode.OPEN : Mode.UNDER1800;

        if (results.size() >= 10) {
            for (int g = 0; g < 10; g++) {
                checkUserInDateBase(results.get(g).username().toLowerCase(), swissCollection);
                updatePlayerScoresSwissGp(results.get(g).username().toLowerCase(),
                        10 - g, swissCollection,
                        timeControl);
            }
        } else {
            for (int g = 0; g < results.size(); g++) {
                checkUserInDateBase(results.get(g).username().toLowerCase(),swissCollection);
                updatePlayerScoresSwissGp(results.get(g).username().toLowerCase(),
                        10 - g, swissCollection,
                        timeControl);
            }

        }

        for (SwissResult result : results) {
            checkUserInDateBase(result.username().toLowerCase(), swissCollection);
            updatePlayerSwissScores(result.username().toLowerCase(),
                    result.points(), swissCollection,
                    timeControl);
            updatePlayerRatings(result.username().toLowerCase(), result.rating(),
                    swissCollection, timeControl);
            addCombinedPlayerTotalScores(result.username().toLowerCase(), swissCollection,
                    timeControl);
        }

        insertTournamentID(touryIDSwiss, MongoConnect.getComputedId());
        updateStandingsOnDojoScoreBoard(timeControl, Type.SWISS, mode ,swissCollection);
        updateStandingsOnDojoScoreBoard(timeControl, Type.COMB_GRAND_PRIX, mode ,swissCollection);

        return "Success! Updated player scores for " + swissOne.get().name();
    }

    /**
     * Returns the rank of the player based on the provided field.
     *
     * @param playerName The Discord ID of the player to fetch the rank for.
     * @param collection The collection of players to search.
     * @param field      The field name to base the ranking on.
     * @return The rank of the player based on the provided field or -1 if the
     *         player is not found.
     */

    public long getPlayerRankDouble(String playerName, MongoCollection<Document> collection, String field) {
        Document query = new Document("Discordid", playerName);

        // Find the player's document
        Document playerDocument = collection.find(query).first();

        if (playerDocument == null) {
            return -1; // Player not found
        }

        Double playerScore = playerDocument.getDouble(field);

        // Calculate the number of players with equal or higher scores for the given
        // field

        return collection.countDocuments(Filters.gte(field, playerScore));
    }

    public long getPlayerRank(String playerName, MongoCollection<Document> collection, String field) {
        Document query = new Document("Discordid", playerName);

        // Find the player's document
        Document playerDocument = collection.find(query).first();

        if (playerDocument == null) {
            return -1; // Player not found
        }

        Integer playerScore = playerDocument.getInteger(field);

        // Calculate the number of players with equal or higher scores for the given
        // field

        return collection.countDocuments(Filters.gte(field, playerScore));
    }

    /**
     * Returns a JDA EmbedBuilder containining the provided player's rank card.
     *
     * @param playerName The Discord ID of the player to generate the rank card for.
     * @param collection The collection of players to search.
     * @return A JDA EmbedBuilder containing the player's rank card.
     */
    public EmbedBuilder getPlayerRankCard(String playerName, MongoCollection<Document> collection) {
        try{
            Document query = new Document("Discordid", playerName);
            Document result = collection.find(query).first();
            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (collection.countDocuments(query) <= 0) {
                return new EmbedBuilder().setDescription("You have not verified your Lichess account, run /verify!")
                        .setColor(Color.red);
            }
            assert result != null;

            embedBuilder.setColor(Color.GREEN);
            embedBuilder.setThumbnail(
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRDEcmvHJKxeF0L1dmlpickvkGNpTWPcNSGPV_c-ZiL6T8MsIxey-61J3VkehDCIi6tN5s&usqp=CAU");
            embedBuilder.setTitle(result.getString("Lichessname") + "'s Ranks:");
            embedBuilder.setDescription(
                    " **League Player Count:** " + collection.countDocuments() + " \n\n \uD83D\uDD25 **Blitz**"
                            + "\n **Arena Rank:** "
                            + "\n" + getPlayerRank(playerName, collection, "blitz_score") + "\n" +
                            "**Swiss Rank:** " + "\n" + getPlayerRankDouble(playerName, collection, "blitz_score_swiss")
                            + "\n" +
                            "**Grand Prix:** " + "\n"
                            + getPlayerRank(playerName, collection, "blitz_comb_total_gp") + "\n\n" +
                            "  **\uD83D\uDC07 Rapid**"
                            + "\n **Arena Rank:** " + "\n"
                            + getPlayerRank(playerName, collection, "rapid_score") + "\n" +
                            "**Swiss Rank:** " + "\n" + getPlayerRankDouble(playerName, collection, "rapid_score_swiss")
                            + "\n" +
                            "**Grand Prix:**" + "\n"
                            + getPlayerRank(playerName, collection, "rapid_comb_total_gp") + "\n\n" +
                            " \uD83D\uDC22 **Classical**"
                            + "\n **Arena Rank:** " + "\n"
                            + getPlayerRank(playerName, collection, "classical_score") + "\n" +
                            "**Swiss Rank:** " + "\n"
                            + getPlayerRankDouble(playerName, collection, "classical_score_swiss") + "\n" +
                            "**Grand Prix: **" + "\n"
                            + getPlayerRank(playerName, collection, "classical_comb_total_gp") + "\n\n" +
                            "♻️ **Sparring**" + "\n **Middlegame Sparring Rank:** " + "\n"
                            + getPlayerRank(playerName, collection, "sp_score") + "\n"
                            + "**Endgame Sparring Rank:** \n" + getPlayerRank(playerName, collection, "eg_score"));

            embedBuilder.setFooter(
                    "**Note: Players can view their league scores with /score");
            System.out.println(embedBuilder.getDescriptionBuilder().toString());

            return embedBuilder;
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new EmbedBuilder().setDescription("Error!");
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

        Document query = new Document("Lichessname", playerName);

        if (!(collection.countDocuments(query) > 0)) {
            ChessPlayer player = new ChessPlayer(playerName, "null", 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0,
                    0, 0, 0,
                    0, 0, 0, 0,
                    0, 0);
            Document document = new Document("Lichessname", player.getLichessname())
                    .append("Discordid", player.getDiscordId())
                    .append("blitz_score", player.getBLITZ_SCORE())
                    .append("rapid_score", player.getRAPID_SCORE())
                    .append("classical_score", player.getCLASSICAL_SCORE())
                    .append("blitz_rating", 0).append("classical_rating", 0)
                    .append("rapid_rating", 0).append("blitz_score_gp", 0)
                    .append("rapid_score_gp", 0).append("classical_score_gp", 0)
                    .append("blitz_score_swiss", 0.0).append("rapid_score_swiss", 0.0)
                    .append("classical_score_swiss", 0.0).append("blitz_score_swiss_gp", 0)
                    .append("rapid_score_swiss_gp", 0).append("classical_score_swiss_gp", 0)
                    .append("blitz_comb_total", 0).append("blitz_comb_total_gp", 0)
                    .append("rapid_comb_total", 0).append("rapid_comb_total_gp", 0)
                    .append("classical_comb_total", 0).append("classical_comb_total_gp", 0).append("sp_score", 0)
                    .append("sparring_rating", 0).append("eg_score", 0).append("eg_rating", 0);
            collection.insertOne(document);
        }else{
            checkProv(playerName, collection);
        }

    }

    /**
     * Returns a JDA EmbedBuilder containing the provided player's score card.
     *
     * @param playerName The Discord ID of the player to generate the score card
     *                   for.
     * @param collection The collection of players to search.
     * @return A JDA EmbedBuilder containing the player's score card.
     */
    public EmbedBuilder getPlayerScore(String playerName, MongoCollection<Document> collection) {
        try{
            Document query = new Document("Discordid", playerName);
            Document result = collection.find(query).first();
            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (collection.countDocuments(query) <= 0) {
                return new EmbedBuilder().setDescription("You have not verified your Lichess account, run /verify!")
                        .setColor(Color.red);
            }
            assert result != null;

            embedBuilder.setColor(Color.GREEN);
            embedBuilder.setThumbnail(
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRDEcmvHJKxeF0L1dmlpickvkGNpTWPcNSGPV_c-ZiL6T8MsIxey-61J3VkehDCIi6tN5s&usqp=CAU");
            embedBuilder.setTitle(result.getString("Lichessname") + "'s Scores:");
            embedBuilder.setDescription(" **League Player Count:** " + collection.countDocuments() +
                    "\n\n \uD83D\uDD25 **Blitz**"
                    + "\n**Arena Score:** " + "\n" + result.getInteger("blitz_score") + "\n" +
                    "**Swiss Score:** " + "\n" + result.getDouble("blitz_score_swiss") + "\n" +
                    "**Grand Prix:** " + "\n" + result.getInteger("blitz_comb_total_gp") + "\n" +
                    " \n \uD83D\uDC07 **Rapid**" + "\n **Arena Score:** "
                    + "\n" + result.getInteger("rapid_score") + "\n" +
                    "**Swiss Score:** " + "\n" + result.getDouble("rapid_score_swiss") + "\n" +
                    "**Grand Prix:**" + "\n" + result.getInteger("rapid_comb_total_gp") + "\n" +
                    " \n \uD83D\uDC22 **Classical**" +
                    "\n **Arena Score:** " + "\n" + result.getInteger("classical_score") + "\n" +
                    "**Swiss Score:** " + "\n" + result.getDouble("classical_score_swiss") + "\n" +
                    "**Grand Prix: **" + "\n" + result.getInteger("classical_comb_total_gp") + "\n" +
                    "\n ♻️ **Sparring**" + "\n **Middlegame Sparring Score:** " + "\n"
                    + result.getInteger("sp_score") + "\n" + "**Endgame Sparring Score:** \n"
                    + result.getInteger("eg_score"));

            embedBuilder.setFooter(
                    "**Note: scores of 0 means the player did not participate/play or earned zero points in a league, players can view their ranks with /rank");

            System.out.println(embedBuilder.getDescriptionBuilder().toString());

            return embedBuilder;
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new EmbedBuilder().setDescription("Error!");
        }
    }

    /**
     * update standing on the Dojoscoreboard by sending the time control,
     * leaderboard type, and players collection
     *
     * @param timeControl time control for the leaderboard
     * @param type        the type of league/leaderboard
     * @param collection  the collection of players
     */

    public void updateStandingsOnDojoScoreBoard(Time_Control timeControl, Type type, Mode mode,
                                                MongoCollection<Document> collection) {
        switch (type) {
            case ARENA, SWISS, COMB_GRAND_PRIX -> DojoScoreboard.updateLeaderboard(timeControl, type.getName(),
                    collection, timeControl + type.toString(), mode);

            case SPARRING, SPARRING_ENDGAME ->
                    DojoScoreboard.updateLeaderboard(timeControl, type.getName(), collection, type.toString(), mode);

        }
    }

    /**
     * Calculates the scores for the Lichess liga with the provided URL and
     * tournament collection.
     *
     * @param arenaUrl             The URL of the Lichess liga to calculate scores
     *                             for.
     * @param tournamentCollection The collection of tournaments tracked in the
     *                             database.
     * @return A String description of the calculation result.
     */

    public String calculateLichessLigaScores(String arenaUrl, MongoCollection<Document> tournamentCollection) {
        return calculateArenaScores(arenaUrl, tournamentCollection);
    }

}