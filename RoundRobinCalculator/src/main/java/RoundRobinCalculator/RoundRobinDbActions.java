package RoundRobinCalculator;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The type Round robin db actions.
 */
public class RoundRobinDbActions {


    /**
     * Perform general search document.
     *
     * @param collection the collection
     * @param key        the key
     * @param val        the val
     * @return the document
     */
    public Document performGeneralSearch(@NotNull MongoCollection<Document> collection, String key, String val) {
        Document query = new Document(key, val);
        return collection.find(query).first();
    }


    /**
     * Gets tournament id doc.
     *
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @return the tournament id doc
     */
    public Document getTournamentIDDoc(MongoCollection<Document> RRcollection, String tournamentID) {
        Document query = new Document("tournamentId", tournamentID);
        return RRcollection.find(query).first();
    }


    /**
     * Search the ambiguous username boolean.
     *
     * @param username           the username
     * @param platform           the platform
     * @param RRplayercollection the r rplayercollection
     * @return the boolean
     */
    public boolean searchTheAmbiguousUsername(String username, Platform platform, MongoCollection<Document> RRplayercollection) {

        switch (platform) {
            case LICHESS -> {
                return performGeneralSearch(RRplayercollection, "Lichessname", username) != null;
            }
            case CHESSCOM -> {
                return performGeneralSearch(RRplayercollection, "Chesscomname", username) != null;
            }
            case DISCORD -> {
                return performGeneralSearch(RRplayercollection, "Discordname", username) != null;
            }
        }

        return false;
    }

    /**
     * Search game finder names string.
     *
     * @param Discordname        the discordname
     * @param RRplayercollection the r rplayercollection
     * @param platform           the platform
     * @return the string
     */
    public String searchGameFinderNames(String Discordname, MongoCollection<Document> RRplayercollection, Platform platform) {

        switch (platform) {
            case LICHESS -> {
                return getGeneralSearchBasedOnParams("Discordname", Discordname, RRplayercollection, "Lichessname");
            }
            case CHESSCOM -> {
                return getGeneralSearchBasedOnParams("Discordname", Discordname, RRplayercollection, "Chesscomname");
            }
        }

        return null;
    }


    /**
     * Push player score.
     *
     * @param username           the username
     * @param RRplayercollection the r rplayercollection
     * @param platform           the platform
     * @param newScore           the new score
     */
    public void pushPlayerScore(@NotNull String username, @NotNull MongoCollection<Document> RRplayercollection, @NotNull Platform platform, double newScore) {

        Document searchAmb = null;

        switch (platform) {
            case LICHESS -> searchAmb = performGeneralSearch(RRplayercollection, "Lichessname", username);
            case CHESSCOM -> searchAmb = performGeneralSearch(RRplayercollection, "Chesscomname", username);
            case DISCORD -> searchAmb = performGeneralSearch(RRplayercollection, "Discordname", username);
        }

        RRplayercollection.updateOne(searchAmb, Updates.inc("score", newScore));
        System.out.println("Successfully updated the player " + username + " Score by: " + newScore);

    }


    /**
     * Gets pairing from running tournament.
     *
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @return the pairing from running tournament
     * @throws RoundRobinException the round robin exception
     */
    public String getPairingFromRunningTournament(MongoCollection<Document> RRcollection, String tournamentID) throws RoundRobinException {

        Document tournamentDoc = getTournamentIDDoc(RRcollection, tournamentID);

        if (tournamentDoc != null) {
            String tournamentId = tournamentDoc.getString("tournamentId");
            System.out.println("First Active Tournament ID: " + tournamentId);

            return tournamentDoc.getString("pairings");


        } else {
            throw new RoundRobinException("Invalid Tournament ID!");
        }
    }

    /**
     * Push game submission for running tournament.
     *
     * @param RRcollection the r rcollection
     * @param gameInfo     the game info
     * @param tournamentID the tournament id
     */
    public void pushGameSubmissionForRunningTournament(MongoCollection<Document> RRcollection, String gameInfo, String tournamentID) {

        RRcollection.updateOne(
                new Document("tournamentId", tournamentID),
                Updates.push("game-submissions", gameInfo)
        );


    }

    /**
     * Push crosstable string.
     *
     * @param RRcollection the r rcollection
     * @param crosstable   the crosstable
     * @param tournamentID the tournament id
     */
    public void pushCrosstableString(MongoCollection<Document> RRcollection, String crosstable, String tournamentID) {
        RRcollection.updateOne(
                new Document("tournamentId", tournamentID),
                Updates.set("crosstable", crosstable)
        );
    }

    /**
     * Push cross table list.
     *
     * @param RRcollection   the r rcollection
     * @param crosstableList the crosstable list
     * @param tournamentID   the tournament id
     */
    public void pushCrossTableList(MongoCollection<Document> RRcollection, ArrayList<ArrayList<String>> crosstableList, String tournamentID) {
        RRcollection.updateOne(
                new Document("tournamentId", tournamentID),
                Updates.set("crosstable-data", crosstableList)
        );
    }

    /**
     * Gets general search based on params.
     *
     * @param targetSearch the target search
     * @param targetID     the target id
     * @param collection   the collection
     * @param returnId     the return id
     * @return the general search based on params
     */
    public String getGeneralSearchBasedOnParams(String targetSearch, String targetID, MongoCollection<Document> collection, String returnId) {
        Document query = new Document(targetSearch, targetID);

        Document result = collection.find(query).first();

        if (result != null) {

            return result.getString(returnId);

        } else {
            return "null";
        }
    }


}
