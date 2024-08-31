package dojo.bot.Controller.CalculateScores;

import chariot.Client;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoresUtil {


    /**
     * Inserts a tournament id into the database.
     *
     * @param id The tournament id to insert.
     * @param collection The collection to insert into.
     */

    public static void insertTournamentID(String id, MongoCollection<Document> collection) {
        Document document = new Document("tournament-id", id);

        collection.insertOne(document);

        System.out.println("Document inserted successfully.");
    }

    /**
     * Checks if a tournament id is present in the database.
     *
     * @param id The tournament id to check.
     * @param collection The collection to check in.
     * @return True if the tournament id is present, false otherwise.
     */

    public static boolean isTournamentIDresent(String id, MongoCollection<Document> collection) {
        Document query = new Document("tournament-id", id);
        FindIterable<Document> result = collection.find(query);

        return result.iterator().hasNext();
    }



    /**
     * Gets the index of a team in a tournament.
     *
     * @param id The tournament id to get the team index from.
     * @param client The client to get the team index from.
     * @return The index of the team in the tournament.
     */

    public static int getTeamIndex(String id, Client client) {

        String target = "chessdojo";

        List<String> getTeamNames = new ArrayList<>();

        for(chariot.model.TeamBattleResults.Teams t: client.tournaments().teamBattleResultsById(id).get().teams()){
            getTeamNames.add(t.id().toLowerCase());
        }

        Collections.sort(getTeamNames);

        return binarySearch(getTeamNames, target);

    }

    /**
     * Binary search algorithm.
     *
     * @param list The list to search in.
     * @param target The target value to search for.
     * @return The index of the target value if found, -1 otherwise.
     */

    public static int binarySearch(List<String> list, String target) {
        int left = 0;
        int right = list.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int result = target.compareTo(list.get(mid));


            if (result == 0) {
                System.out.println(mid);
                return mid;
            }


            if (result > 0) {
                System.out.println(left);
                left = mid + 1;
            }

            else {
                System.out.println(right);
                right = mid - 1;
            }
        }

        return -1;
    }

    public static int getTeamLinearIndex(String id, Client client) {

        for (int i = 0; i < client.tournaments().teamBattleResultsById(id).get().teams().size(); i++) {
            if (client.tournaments().teamBattleResultsById(id).get().teams().get(i).id()
                    .equalsIgnoreCase("chessdojo")) {
                return i;
            }
        }

        return -1;

    }

    /**
     * Returns true if the provided tournament does not exist in the provided collection.
     *
     * @param collection   The collection to check for the tournament.
     * @param tournamentID The ID of the tournament to check for.
     * @return True if the provided tournament ID exists in the collection.
     */
    public static boolean tournamentPresent(MongoCollection<Document> collection, String tournamentID) {

        Document query = new Document("Id", tournamentID);
        FindIterable<Document> result = collection.find(query);
        return !result.iterator().hasNext();
    }



}