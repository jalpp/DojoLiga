package dojo.bot.Controller.TicketSystem;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GHIssue;

/**
 * Manages interactions with GitHub, specifically for creating issues.
 */
public class GithubManager {

    /**
     * Creates a new issue in the specified GitHub repository.
     *
     * @param token the OAuth token for authenticating with GitHub
     * @param owner the owner of the repository
     * @param repo the name of the repository
     * @param title the title of the issue
     * @param body the body content of the issue
     */
    public static void createIssue(String token, String owner, String repo, String title, String body) {
        try {
            // Connect to GitHub using the provided OAuth token
            GitHub github = GitHub.connectUsingOAuth(token);


            GHRepository repository = github.getRepository(owner + "/" + repo);

            // Create a new issue with the specified title and body, and label it as a bug
            GHIssue issue = repository.createIssue(title)
                    .body(body)
                    .label("user-submitted")
                    .create();

            // Print the URL of the created issue
            System.out.println("Issue created: " + issue.getHtmlUrl());
        } catch (Exception e) {
            // Print an error message if an exception occurs
            System.err.println("Error creating issue: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

