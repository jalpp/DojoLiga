package dojo.bot.Controller.Database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dojo.bot.Runner.Main;
import org.bson.Document;

/**
 * The type Mongo connect.
 */
public class MongoConnect {


    private static MongoCollection<Document> chesscomplayers;

    private static MongoCollection<Document> computedId;

    private static MongoCollection<Document> u1800Playerswiss;

    private static MongoCollection<Document> swissLeagueCollection;

    private static MongoCollection<Document> arenaLeagueCollection;

    private static MongoCollection<Document> RRplayercollection;

    private static MongoCollection<Document> RRcollection;

    private static MongoCollection<Document> u1800swissCollection;

    private static MongoCollection<Document> collection;

    /**
     * Instantiates a new Mongo connect.
     */
    public MongoConnect() {
    }

    /**
     * Connect.
     */
    public static void connect(){
        String connectionString = Main.dotenv.get("CONNECTION_STRING");

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        MongoClient mongoClient = MongoClients.create(settings);

        MongoDatabase database = mongoClient.getDatabase("Lisebot-database");

        collection =  database.getCollection("players");
        chesscomplayers = database.getCollection("chesscom-players");
        swissLeagueCollection = database.getCollection("swiss-league");
        arenaLeagueCollection = database.getCollection("arena-league");
        computedId = database.getCollection("computed-id");
        u1800Playerswiss = database.getCollection("u1800");
        u1800swissCollection = database.getCollection("u1800-swiss");
        RRcollection = database.getCollection("rr-tournaments ");
        RRplayercollection = database.getCollection("rr-players");
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        connect();
    }


    /**
     * Gets chesscomplayers.
     *
     * @return the chesscomplayers
     */
    public static MongoCollection<Document> getChesscomplayers() {
        return chesscomplayers;
    }

    /**
     * Gets computed id.
     *
     * @return the computed id
     */
    public static MongoCollection<Document> getComputedId() {
        return computedId;
    }

    /**
     * Gets u 1800 playerswiss.
     *
     * @return the u 1800 playerswiss
     */
    public static MongoCollection<Document> getU1800Playerswiss() {
        return u1800Playerswiss;
    }

    /**
     * Gets swiss league collection.
     *
     * @return the swiss league collection
     */
    public static MongoCollection<Document> getSwissLeagueCollection() {
        return swissLeagueCollection;
    }

    /**
     * Gets arena league collection.
     *
     * @return the arena league collection
     */
    public static MongoCollection<Document> getArenaLeagueCollection() {
        return arenaLeagueCollection;
    }

    /**
     * Gets r rplayercollection.
     *
     * @return the r rplayercollection
     */
    public static MongoCollection<Document> getRRplayercollection() {
        return RRplayercollection;
    }

    /**
     * Gets r rcollection.
     *
     * @return the r rcollection
     */
    public static MongoCollection<Document> getRRcollection() {
        return RRcollection;
    }

    /**
     * Gets u 1800 swiss collection.
     *
     * @return the u 1800 swiss collection
     */
    public static MongoCollection<Document> getU1800swissCollection() {
        return u1800swissCollection;
    }

    /**
     * Gets lichessplayers.
     *
     * @return the lichessplayers
     */
    public static MongoCollection<Document> getLichessplayers() {
        return collection;
    }
}