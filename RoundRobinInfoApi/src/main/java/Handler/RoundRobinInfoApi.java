package Handler;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Round robin info api.
 */
public class RoundRobinInfoApi {


    private Document getTournamentIDDoc (MongoCollection<Document> RRcollection, String tournamentID){
        Document query = new Document("tournamentId", tournamentID);
        return RRcollection.find(query).first();
    }

    /**
     * Get api info response response.
     *
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @return the response
     */
    public Response getApiInfoResponse(MongoCollection<Document> RRcollection, String tournamentID){

        Document tournamentDoc = getTournamentIDDoc(RRcollection, tournamentID);

        if (tournamentDoc != null) {
            String tournamentId = tournamentDoc.getString("tournamentId");
            System.out.println("First Active Tournament ID: " + tournamentId);
            String pair = tournamentDoc.getString("pairings");
            Response response = new Response();

            response.setTournamentname(Parser.getTournamentName(pair));
            response.setDesc(Parser.getDesc(pair));
            response.setPairs(splitStringList(Parser.getPairingsInListFormat(pair)));
            response.setLeaderboard(tournamentDoc.getList("leaderboard", String.class));
            response.setPlayers(tournamentDoc.getList("players", String.class));
            response.setGameSub(tournamentDoc.getList("game-submissions", String.class));
            response.setCrosstable(getTournamentCrosstable(tournamentDoc));
            response.setCrosstableString(tournamentDoc.getString("crosstable"));
            response.setInfo(tournamentID);
            response.setMessage("Successfully Send Tournament Data");
            response.setScores(tournamentDoc.getList("scores", Double.class));
            response.setStatusCode(200);


            return response;



        } else {
            Response response = new Response();
            response.setInfo("Invalid ID");
            response.setMessage("Error! Invalid Tournament ID!");
            response.setStatusCode(400);
            return response;
        }
    }

    private List<ArrayList<String>> getTournamentCrosstable(Document document){

        List<?> rawList = document.getList("crosstable-data", ArrayList.class);
        List<ArrayList<String>> tournamentCrosstable = new ArrayList<>();

        for (Object obj : rawList) {
            if (obj instanceof ArrayList<?>) {
                ArrayList<String> innerList = new ArrayList<>();
                for (Object innerObj : (ArrayList<?>) obj) {
                    if (innerObj instanceof String) {
                        innerList.add((String) innerObj);
                    } else {
                        throw new IllegalArgumentException("Expected inner list of strings but found: " + innerObj.getClass().getName());
                    }
                }
                tournamentCrosstable.add(innerList);
            } else {
                throw new IllegalArgumentException("Expected list of lists but found: " + obj.getClass().getName());
            }
        }

        return tournamentCrosstable;
    }

    /**
     * Split string list array list.
     *
     * @param input the input
     * @return the array list
     */
    public static ArrayList<ArrayList<String>> splitStringList(List<String> input) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        for (String s : input) {
            // Split by comma and trim each element
            String[] splitArray = s.split(",\\s*");

            // Convert the array to an ArrayList and add it to the result list
            ArrayList<String> splitList = new ArrayList<>(Arrays.asList(splitArray));
            result.add(splitList);
        }

        return result;
    }


}
