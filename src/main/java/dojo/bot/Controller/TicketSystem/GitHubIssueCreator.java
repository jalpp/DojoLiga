package dojo.bot.Controller.TicketSystem;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GitHubIssueCreator {

    public static String createIssue(String repoOwner, String repoName, String token, String issueTitle, String issueBody, String[] labels) {
        try {
            String apiUrl = String.format("https://api.github.com/repos/%s/%s/issues", repoOwner, repoName);

            // Open a connection
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Construct JSON payload using Gson
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("title", issueTitle);
            payloadMap.put("body", issueBody);
            payloadMap.put("labels", labels);

            Gson gson = new Gson();
            String payload = gson.toJson(payloadMap);

            // Debug Payload
            System.out.println("Payload: " + payload);

            // Write payload to the connection output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get response code and handle response
            int responseCode = connection.getResponseCode();
            System.out.println(connection.getResponseMessage());
            String success = "";

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }

                    String parsedIssue = response.toString().split("url")[1].split(",")[0].split("/")[7];
                    success = "Issue created successfully with number: https://github.com/jackstenglein/chess-dojo-scheduler/issues/" + parsedIssue.substring(0, parsedIssue.length() - 1);
                }
                return success;
            } else {
                System.err.println("Failed to create issue. Response Code: " + responseCode);
                try (var errorStream = connection.getErrorStream()) {
                    if (errorStream != null) {
                        String response = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                        System.err.println("Error Response: " + response);
                    }
                }
                return "Failed to create the issue.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }
    }


}