package roundrobin;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


import java.util.Map;

/**
 * This is the entry point for the Lambda function
 */
public class App implements RequestHandler<Map<String, String>,String> {

    /**
     * The Connection string.
     */
    String connectionString = System.getenv("CONNECTION_STRING");

    /**
     * The Server api.
     */
    ServerApi serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build();

    /**
     * The Settings.
     */
    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .serverApi(serverApi)
            .build();

    /**
     * The Mongo client.
     */
    MongoClient mongoClient = MongoClients.create(settings);
    /**
     * The Database.
     */
    MongoDatabase database = mongoClient.getDatabase("Lisebot-database");
    /**
     * The R rcollection.
     */
    MongoCollection<Document> RRcollection = database.getCollection("rr-tournaments ");


    //@Override
    public String handleRequest(Map<String, String> event, Context context) {

        RoundRobinTournamentFinderLambda tournamentFinder = new RoundRobinTournamentFinderLambda();

        return tournamentFinder.handler(RRcollection).toString();
    }
}