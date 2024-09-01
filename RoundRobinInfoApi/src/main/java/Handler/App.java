package Handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * The type App.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {


        Map<String, String> queryParams = requestEvent.getQueryStringParameters();

        String tournamntId = queryParams.get("tournamentid");
        RoundRobinInfoApi api = new RoundRobinInfoApi();
        Response response = null;


        System.out.println(tournamntId);


        try{
            response = api.getApiInfoResponse(RRcollection, tournamntId);
        } catch (Exception e) {
            return createErrorResponse("Internal error! " + e.getMessage());
        }



        try {
            APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
            Map<String, String> headerspass = new HashMap<>();

            // Add CORS headers
            headerspass.put("Access-Control-Allow-Origin", "*");
            headerspass.put("Access-Control-Allow-Headers", "*");
            headerspass.put("Access-Control-Allow-Methods", "GET, OPTIONS");
            String responseBody = objectMapper.writeValueAsString(response);
            apiResponse.setBody(responseBody);
            apiResponse.setStatusCode(200);
            apiResponse.setHeaders(headerspass);
            System.out.println(apiResponse.getHeaders());
            System.out.println(apiResponse);
            return apiResponse;

        } catch (IOException e) {
            APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
            context.getLogger().log("Error converting response to JSON: " + e.getMessage());
            apiResponse.setStatusCode(500);
            apiResponse.setHeaders(Map.of("Content-Type", "application/json"));
            apiResponse.setHeaders(Map.of("Access-Control-Allow-Origin", "*"));
            apiResponse.setBody("{\"message\": \"Internal Server Error\"" + e.getMessage() + " }");
            System.out.println(apiResponse.getHeaders());
            System.out.println(apiResponse);
            return apiResponse;
        }

    }

    private APIGatewayProxyResponseEvent createErrorResponse(String errorMessage) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(400); // Bad Request
        response.setBody("{\"message\": \"" + errorMessage + "\"}");
        response.setHeaders(Map.of("Content-Type", "application/json"));
        response.setHeaders(Map.of("Access-Control-Allow-Origin", "*"));
        System.out.println(response.getHeaders());
        System.out.println(response);
        return response;
    }


}
