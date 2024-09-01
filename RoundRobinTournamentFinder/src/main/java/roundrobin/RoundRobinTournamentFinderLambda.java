package roundrobin;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class RoundRobinTournamentFinderLambda {

    public ObjectNode handler(MongoCollection<Document> RRcollection) {

        ObjectMapper objectMapper = new ObjectMapper();


        Document filter = new Document("status", "running");
        FindIterable<Document> runningTournaments = RRcollection.find(filter);


        ArrayNode tournamentsArray = objectMapper.createArrayNode();


        for (Document doc : runningTournaments) {
            String tournamentID = doc.getString("tournamentId");
            tournamentsArray.add(tournamentID);
        }


        ObjectNode result = objectMapper.createObjectNode();
        result.set("tournaments", tournamentsArray);


        System.out.println(result.toString());


        return result;
    }

}
