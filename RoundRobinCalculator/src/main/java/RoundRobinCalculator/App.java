package RoundRobinCalculator;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    /**
     * The R rplayercollection.
     */
    MongoCollection<Document> RRplayercollection = database.getCollection("rr-players");


    @Override
    public String handleRequest(Map<String, String> event, Context context) {

        LambdaLogger logger = context.getLogger();

        Gson gson = new GsonBuilder().create();

        logger.log("Events Info: " + gson.toJson(event));

        String id = event.get("tournamentId");

        RoundRobinCalculator calculator = new RoundRobinCalculator(
                RRcollection, RRplayercollection
        );

        LeaderboardCalculator leaderboardCalculator = new LeaderboardCalculator(
                RRcollection, RRplayercollection
        );

        try {
            calculator.performCalculation(id);
            leaderboardCalculator.calculateLeaderboard(id);
        } catch (RoundRobinException | InterruptedException e) {
            return e.getMessage();
        }

        return "Successfully computed the scores!";

    }
}