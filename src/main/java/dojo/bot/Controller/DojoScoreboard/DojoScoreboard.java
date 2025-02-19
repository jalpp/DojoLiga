package dojo.bot.Controller.DojoScoreboard;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import dojo.bot.Controller.League.Mode;
import dojo.bot.Controller.League.Time_Control;
import dojo.bot.Controller.League.Type;
import dojo.bot.Controller.User.ChessPlayer;
import dojo.bot.Runner.Main;
import okhttp3.*;
import org.bson.Document;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Heart of connection to Jack's amazing DojoScoreboard frontend integration
 * @author Jack Stenglein
 */
public class DojoScoreboard {

    private static final String BETA = "https://c2qamdaw08.execute-api.us-east-1.amazonaws.com/tournaments";
    private static final String BETA_UPDATE = "https://c2qamdaw08.execute-api.us-east-1.amazonaws.com/tournaments/leaderboard";
    private static final String CREATE_TOURNAMENT_URL = "https://g4shdaq6ug.execute-api.us-east-1.amazonaws.com/tournaments";
    private static final String UPDATE_LEADERBOARD_URL = "https://g4shdaq6ug.execute-api.us-east-1.amazonaws.com/tournaments/leaderboard";
    private static final String GET_LEADERBOARD_URL  = "https://g4shdaq6ug.execute-api.us-east-1.amazonaws.com/public/tournaments/leaderboard";

    /**
     * Sends a POST request to the provided URL with the provided body.
     *
     * @param url      The URL to send the POST request to.
     * @param jsonBody The body to set in the request.
     */
    private static void post(String url, String jsonBody) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic " + Main.dotenv.get("CHESSDOJO_API_TOKEN"))
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Response Code: " + response.code());
                if (response.body() != null) {
                    System.out.println("Response Body: " + response.body().string());
                }
            } else {
                System.out.println("Request failed: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Sends a create tournament request to the Dojo Scoreboard.
     *
     * @param tournamentUrl The URL of the tournament to create.
     */
    public static void createTournament(String tournamentUrl) {
        String URL = Main.IS_BETA ? BETA : CREATE_TOURNAMENT_URL;
        String jsonBody = "{\"url\": \"" + tournamentUrl + "\"}";
        post(URL, jsonBody);
    }

    /**
     * Sends an update leaderboard request to the Dojo Scoreboard.
     *
     * @param timeControl    The time control of the leaderboard to update.
     * @param tournamentType The type of the tournament to update.
     * @param collection     The list of players to update.
     * @param scoreField     The field used to get a player's score.
     */
    public static void updateLeaderboard(Time_Control timeControl, String tournamentType,
                                         MongoCollection<Document> collection, String scoreField, Mode mode) {

        String URL = Main.IS_BETA ? BETA_UPDATE : UPDATE_LEADERBOARD_URL;

        Document body = new Document();
        body.put("timeControl", timeControl.toString());
        body.put("type", tournamentType);
        body.put("site", "lichess.org");
        body.put("mode", mode.toString()); // open or under1800

        ArrayList<Document> players = new ArrayList<>();
        collection.find().sort(Sorts.descending(scoreField)).map(d -> {
            Document player = new Document();
            player.put("username", d.get("Lichessname"));
            player.put("rating", d.get(timeControl.toString() + "_rating"));
            player.put("score", d.get(scoreField));
            player.put("prov_blitz", d.get("prov_blitz"));
            player.put("prov_rapid", d.get("prov_rapid"));
            player.put("prov_cla", d.get("prov_cla"));

            return player;
        }).into(players);

        if (players.size() == 0) {
            return;
        }
        body.put("players", players);

        post(URL, body.toJson());
    }


    /**
     * Sends an update leaderboard request to the Dojo Scoreboard.
     *
     * @param timeControl    The time control of the leaderboard to update.
     * @param tournamentType The type of the tournament to update.
     * @param collection     The list of players to update.
     * @param scoreField     The field used to get a player's score.
     */
    public static void updateLeaderboardCC(Time_Control timeControl, String tournamentType,
                                         MongoCollection<Document> collection, String scoreField) {

        String URL = Main.IS_BETA ? BETA_UPDATE : UPDATE_LEADERBOARD_URL;

        Document body = new Document();
        body.put("timeControl", timeControl.toString());
        body.put("type", tournamentType);
        body.put("site", "chess.com");

        ArrayList<Document> players = new ArrayList<>();
        collection.find().sort(Sorts.descending(scoreField)).map(d -> {
            Document player = new Document();
            player.put("username", d.get("Chesscomname"));
            player.put("rating", d.get(timeControl.toString() + "_rating"));
            player.put("score", d.get(scoreField));


            return player;
        }).into(players);

        if (players.size() == 0) {
            return;
        }
        body.put("players", players);

        post(URL, body.toJson());
    }


    public static ArrayList<ChessPlayer> getLeaderboard(Time_Control timeControl, String period, String date, Type type){

        OkHttpClient client = new OkHttpClient();

        String endpoint = GET_LEADERBOARD_URL + "?timePeriod="+ period + "&tournamentType=" + type.fetchLeaderboard() + "&timeControl=" + timeControl.toString()
                + "&date=" + date;

        System.out.println(endpoint);

        Request request = new Request.Builder()
                .url(endpoint)
                .build();

        // Perform the request and handle the response
        try {
            // Execute the request
            Response response = client.newCall(request).execute();

            // Check if the request was successful (status code 200)
            if (response.isSuccessful()) {
                // Get the response body as a string
                assert response.body() != null;

                ArrayList<ChessPlayer> players = new ArrayList<>();

                JSONObject js = new JSONObject(response.body().string());

                for(int i  = 0; i < 10; i++){
                    ChessPlayer player = new ChessPlayer(js.getJSONArray("players").getJSONObject(i).getString("username"), js.getJSONArray("players").getJSONObject(i).getInt("rating"), js.getJSONArray("players").getJSONObject(i).getInt("score"));
                    players.add(player);
                }

                return players;


            } else {
                System.out.println("Unexpected response code: " + response.code());
            }

            // Close the response body
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

      return null;

    }



}