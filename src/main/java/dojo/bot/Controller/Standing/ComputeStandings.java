package dojo.bot.Controller.Standing;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import dojo.bot.Controller.Discord.Helper;
import dojo.bot.Controller.League.Time_Control;

import dojo.bot.Runner.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ComputeStandings {

    private static final String DOJO_LOGO = Helper.DOJO_LOGO;
    private static final String[] PODIUM = { "\uD83C\uDFC6", "\uD83E\uDD48", "\uD83E\uDD49" };
    private final Calendar calendar = Calendar.getInstance();

    private final String MonthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
    private static final String[] EMOJI_LEADERBOARD = { "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣",
            "\uD83D\uDD1F" };

    /**
     * Constructs a default ComputeStandings object.
     */
    public ComputeStandings() {
    }



    private static void addPodiumdouble(StringBuilder sb, List<Document> top10Players, String scoreFieldName,
                                  String ratingFieldName) {
        sb.append("**Podium**\n");

        for (int j = 0; j < 3; j++) {
            Document player = top10Players.get(j);
            String name = player.getString("Lichessname");
            double score = player.getDouble(scoreFieldName);
            int rating = player.getInteger(ratingFieldName);
            sb.append(PODIUM[j])
                    .append(" ")
                    .append("**")
                    .append(name)
                    .append(" (")
                    .append(rating)
                    .append(")")
                    .append("**")
                    .append(" | Score: ")
                    .append("**")
                    .append(score)
                    .append("**")
                    .append("\n");
        }
    }

    /**
     * Appends the leaderboard text to the given StringBuilder.
     *
     * @param sb              The StringBuilder to append the leaderboard to.
     * @param top10Players    The list of the top 10 players.
     * @param scoreFieldName  The field name used to fetch a player's score.
     * @param ratingFieldName The field name used to fetch a player's rating.
     */
    private static void addLeaderboarddouble(StringBuilder sb, List<Document> top10Players, String scoreFieldName,
                                       String ratingFieldName) {
        sb.append("**Leaderboard**\n");

        for (int i = 0; i < top10Players.size(); i++) {
            Document player = top10Players.get(i);
            String name = player.getString("Lichessname");
            double score = player.getDouble(scoreFieldName);
            int rating = player.getInteger(ratingFieldName);
            sb.append(EMOJI_LEADERBOARD[i])
                    .append(" ")
                    .append("**")
                    .append(name)
                    .append(" (")
                    .append(rating)
                    .append(")")
                    .append("**")
                    .append(" | Score: ")
                    .append("**")
                    .append(score)
                    .append("**")
                    .append("\n")
                    .append("\n");
        }
    }

    /**
     * Returns a JDA EmbedBuilder containing the standings for
     * the given players.
     *
     * @param collection      The list of players to calculate standings for.
     * @param title           The title to use in the EmbedBuilder.
     * @param scoreFieldName  The field name to use when fetching player scores.
     * @param ratingFieldName The field name to use when fetching player ratings.
     * @return An EmbedBuilder containing the requested standings.
     */
    private static EmbedBuilder calculateStandingsdouble(MongoCollection<Document> collection, String title,
                                                   String scoreFieldName,
                                                   String ratingFieldName) {
        List<Document> top10Players = new ArrayList<>();
        StringBuilder standing = new StringBuilder();

        try (MongoCursor<Document> cursor = collection.find().sort(Sorts.descending(scoreFieldName)).iterator()) {
            while (cursor.hasNext() && top10Players.size() < 10) {
                top10Players.add(cursor.next());
            }
        }

        addPodiumdouble(standing, top10Players, scoreFieldName, ratingFieldName);
        standing.append("\n\n");
        addLeaderboarddouble(standing, top10Players, scoreFieldName, ratingFieldName);


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setThumbnail(DOJO_LOGO);
        embedBuilder.setDescription("**League Players Count**: " + collection.countDocuments() + "\n"
                + "**Rank | Lichess Username (Rating) | Score** \n\n" + standing.toString());
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setFooter("Pro tip: Players can view their ranks and scores with /rank and /score");

        return embedBuilder;
    }






    /**
     * Appends the podium text to the given StringBuilder.
     *
     * @param sb              The StringBuilder to append the podium to.
     * @param top10Players    The list of the top 10 players.
     * @param scoreFieldName  The field name used to fetch a player's score.
     * @param ratingFieldName The field name used to fetch a player's rating.
     */
    private static void addPodium(StringBuilder sb, List<Document> top10Players, String scoreFieldName,
                                  String ratingFieldName, Platform platform) {
        sb.append("**Podium**\n");

        for (int j = 0; j < 3; j++) {
            Document player = top10Players.get(j);
            String name = player.getString(platform.toString());
            int score = player.getInteger(scoreFieldName);
            int rating = player.getInteger(ratingFieldName);
            sb.append(PODIUM[j])
                    .append(" ")
                    .append("**")
                    .append(name)
                    .append(" (")
                    .append(rating)
                    .append(")")
                    .append("**")
                    .append(" | Score: ")
                    .append("**")
                    .append(score)
                    .append("**")
                    .append("\n");
        }
    }

    /**
     * Appends the leaderboard text to the given StringBuilder.
     *
     * @param sb              The StringBuilder to append the leaderboard to.
     * @param top10Players    The list of the top 10 players.
     * @param scoreFieldName  The field name used to fetch a player's score.
     * @param ratingFieldName The field name used to fetch a player's rating.
     */
    private static void addLeaderboard(StringBuilder sb, List<Document> top10Players, String scoreFieldName,
                                       String ratingFieldName, Platform platform) {
        sb.append("**Leaderboard**\n");

        for (int i = 0; i < top10Players.size(); i++) {
            Document player = top10Players.get(i);
            String name = player.getString(platform.toString());
            int score = player.getInteger(scoreFieldName);
            int rating = player.getInteger(ratingFieldName);
            sb.append(EMOJI_LEADERBOARD[i])
                    .append(" ")
                    .append("**")
                    .append(name)
                    .append(" (")
                    .append(rating)
                    .append(")")
                    .append("**")
                    .append(" | Score: ")
                    .append("**")
                    .append(score)
                    .append("**")
                    .append("\n")
                    .append("\n");
        }
    }

    /**
     * Returns a JDA EmbedBuilder containing the standings for
     * the given players.
     *
     * @param collection      The list of players to calculate standings for.
     * @param title           The title to use in the EmbedBuilder.
     * @param scoreFieldName  The field name to use when fetching player scores.
     * @param ratingFieldName The field name to use when fetching player ratings.
     * @return An EmbedBuilder containing the requested standings.
     */
    private static EmbedBuilder calculateStandings(MongoCollection<Document> collection, String title,
                                                   String scoreFieldName,
                                                   String ratingFieldName) {
        List<Document> top10Players = new ArrayList<>();
        StringBuilder standing = new StringBuilder();

        try (MongoCursor<Document> cursor = collection.find().sort(Sorts.descending(scoreFieldName)).iterator()) {
            while (cursor.hasNext() && top10Players.size() < 10) {
                top10Players.add(cursor.next());
            }
        }

        if(title.toLowerCase().contains("chess.com")){
            addPodium(standing, top10Players, scoreFieldName, ratingFieldName, Platform.CHESSCOM);
            standing.append("\n\n");
            addLeaderboard(standing, top10Players, scoreFieldName, ratingFieldName, Platform.CHESSCOM);
        }else{
            addPodium(standing, top10Players, scoreFieldName, ratingFieldName, Platform.LICHESS);
            standing.append("\n\n");
            addLeaderboard(standing, top10Players, scoreFieldName, ratingFieldName, Platform.LICHESS);
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        if(title.toLowerCase().contains("chess.com")){
            embedBuilder.setTitle(title);
            embedBuilder.setThumbnail(DOJO_LOGO);
            embedBuilder.setDescription("**Combined League Players Count**: " + Main.getPlayerSize() + "\n"
                    + "**Rank | Chess.com Username (Rating) | Score** \n\n" + standing.toString());
            embedBuilder.setColor(Color.GREEN);
            embedBuilder.setFooter("Pro tip: Players can view their ranks and scores with /rank and /score", "https://images.chesscomfiles.com/uploads/v1/images_users/tiny_mce/SamCopeland/phpmeXx6V.png");
        }else{
            embedBuilder.setTitle(title);
            embedBuilder.setThumbnail(DOJO_LOGO);
            embedBuilder.setDescription("**Combined League Players Count**: " + Main.getPlayerSize() + "\n"
                    + "**Rank | Lichess Username (Rating) | Score** \n\n" + standing.toString());
            embedBuilder.setColor(Color.WHITE);
            embedBuilder.setFooter("Pro tip: Players can view their ranks and scores with /rank and /score", "https://cdn-1.webcatalog.io/catalog/lichess/lichess-icon.png");
        }

        return embedBuilder;
    }

    /**
     * Returns a JDA EmbedBuilder containing the Classical Grand Prix standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateClassicalCombTotalGPStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                MonthName +" Classical Grand Prix Lichess Leaderboard",
                "classical_comb_total_gp",
                "classical_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Rapid Grand Prix standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateRapidCombTotalGPStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                MonthName + " Rapid Grand Prix Chess.com Leaderboard",
                "rapid_comb_total_gp",
                "rapid_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Blitz Grand Prix standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateBlitzCombTotalGPStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                MonthName + " Blitz Grand Prix Lichess Leaderboard",
                "blitz_comb_total_gp",
                "blitz_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Classical standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateClassicalCombTotalStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                "Combined Total Standings | Classical | Top10",
                "classical_comb_total",
                "classical_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Rapid standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateRapidCombTotalStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                "Combined Total Standings | Rapid | Top10",
                "rapid_comb_total",
                "rapid_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Blitz standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateBlitzCombTotalStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                "Combined Total Standings | Blitz | Top10",
                "blitz_comb_total",
                "blitz_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Swiss Classical Grand Prix
     * standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateClassicalSwissGpStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                "Swiss Grand Prix Standings | Classical | Top10",
                "classical_score_swiss_gp",
                "classical_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Swiss Rapid Grand Prix standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateRapidSwissGpStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                "Swiss Grand Prix Standings | Rapid | Top10",
                "rapid_score_swiss_gp",
                "rapid_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Swiss Blitz Grand Prix standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateBlitzSwissGpStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                "Swiss Grand Prix Standings | Blitz | Top10",
                "blitz_score_swiss_gp",
                "blitz_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Swiss Classical standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateClassicalSwissTotalStandings(MongoCollection<Document> collection) {
        return calculateStandingsdouble(collection,
                MonthName + " Classical Swiss Leaderboard",
                "classical_score_swiss",
                "classical_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Swiss Rapid standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateRapidSwissTotalStandings(MongoCollection<Document> collection) {
        return calculateStandingsdouble(collection,
                MonthName + " Rapid Swiss Leaderboard",
                "rapid_score_swiss",
                "rapid_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Swiss Blitz standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateBlitzSwissTotalStandings(MongoCollection<Document> collection) {
        return calculateStandingsdouble(collection,
                MonthName + " Blitz Swiss Leaderboard",
                "blitz_score_swiss",
                "blitz_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Classical Arena standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateClassicalArenaTotalStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                MonthName + " Classical Arena Leaderboard",
                "classical_score",
                "classical_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Rapid Arena standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateRapidArenaTotalStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                MonthName +" Rapid Arena Leaderboard",
                "rapid_score",
                "rapid_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Blitz Arena standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateBlitzArenaTotalStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                MonthName + " Blitz Arena Leaderboard",
                "blitz_score",
                "blitz_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Classical Arena Grand Prix
     * standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateClassicalArenaGpStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                "Arena Grand Prix Standings | Classical | Top10",
                "classical_score_gp",
                "classical_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Rapid Arena Grand Prix standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateRapidArenaGpStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                "Arena Grand Prix Standings | Rapid | Top10",
                "rapid_score_gp",
                "rapid_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Blitz Arena Grand Prix standings.
     *
     * @param collection The list of players to calculate standings for.
     * @return An EmbedBuilder containing the standings.
     */
    public EmbedBuilder calculateBlitzArenaGpStandings(MongoCollection<Document> collection) {
        return calculateStandings(collection,
                "Arena Grand Prix Standings | Blitz | Top10",
                "blitz_score_gp",
                "blitz_rating");
    }


    /**
     * Returns a JDA EmbedBuilder containing the Mix Middlegame Sparring standings.
     * @param collection the collection of players to calculate the standings for.
     * @return An EmbedBuilder containing the standings.
     */


    public EmbedBuilder calculateSparringMixStandings(MongoCollection<Document> collection){
        return calculateStandingsdouble(collection, MonthName +" Middlegame Sparring Total Standings | Top10",
                "sp_score",
                "sparring_rating");
    }

    /**
     * Returns a JDA EmbedBuilder containing the Mix Endgame Sparring standings.
     * @param collection the collection of players to calculate the standings for.
     * @return An EmbedBuilder containing the standings.
     */


    public EmbedBuilder calculateEndgameSparringMixStandings(MongoCollection<Document> collection){
        return calculateStandingsdouble(collection, MonthName + " Endgame Sparring Total Standings | Top10",
                "eg_score",
                "eg_rating");
    }

    /**
     * Returns the top10 Embed leaderboard for given time control ratings
     * @param playersCollection the collection of players to calculate the standings for.
     * @param timeControl the time control of rating
     * @return JDA EmbedBuilder containing leaderboard
     */


    public EmbedBuilder getTop10Leaderboard(MongoCollection<Document> playersCollection,
                                            Time_Control timeControl, String search) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setThumbnail(Helper.DOJO_LOGO);
        builder.setTitle(timeControl.getTitle() + " Rating Top10");
        FindIterable<Document> sortedPlayers = playersCollection.find().sort(new Document( timeControl.toString() + "_rating", -1));

        List<Document> top10Players = sortedPlayers.limit(10).into(new ArrayList<>());


        StringBuilder leaderboardString = new StringBuilder();

        int position = 0;
        for (Document player : top10Players) {
            String playerName = player.getString(search);
            int rating = player.getInteger(timeControl.toString() + "_rating");

            leaderboardString.append(EMOJI_LEADERBOARD[position]).append(" **").append(playerName).append(" - ").append(rating).append("**\n\n");
            position++;
        }

        builder.setDescription(leaderboardString.toString());
        builder.setColor(Color.green);
        builder.setFooter("**Note: Players can update their live ratings with /update");
        return builder;


    }








}