package dojo.bot.Controller.Database;

import com.mongodb.client.MongoCollection;
import dojo.bot.Controller.User.Verification;
import dojo.bot.Runner.Main;
import org.bson.Document;

/**
 * The type Search query.
 */
public class SearchQuery {

    private final Verification verificationSearch = new Verification();

    /**
     * Instantiates a new Search query.
     */
    public SearchQuery() {

    }

    /**
     * Search user by discord id string.
     *
     * @param DiscordID  the discord id
     * @param collection the collection
     * @return the string
     */
    public String searchUserByDiscordID(String DiscordID, MongoCollection<Document> collection) {

        StringBuilder builder = new StringBuilder();

        if (this.verificationSearch.userPresentNormal(collection, DiscordID)) {
            builder.append("Lichess Username: ").append(this.verificationSearch.getReletatedLichessName(DiscordID, collection))
                    .append("\n").append("Chesscom Username: ").append(verificationSearch.getReletatedChessName(DiscordID, Main.chesscomplayers))
                    .append("\n").append("Discord ID ").append(DiscordID);
            return builder.toString();
        }

        return "User Discord ID is not present!";
    }

    /**
     * Search user by lichess user string.
     *
     * @param LichessUser the lichess user
     * @param collection  the collection
     * @return the string
     */
    public String searchUserByLichessUser(String LichessUser, MongoCollection<Document> collection) {
        String DiscordID = verificationSearch.getDiscordIdByLichessUsername(collection, LichessUser.toLowerCase());
        StringBuilder builder = new StringBuilder();
        if (!DiscordID.equalsIgnoreCase("null")) {
            builder.append("Lichess Username: ").append(this.verificationSearch.getReletatedLichessName(DiscordID, collection))
                    .append("\n").append("Chesscom Username: ").append(verificationSearch.getReletatedChessName(DiscordID, Main.chesscomplayers))
                    .append("\n").append("Discord ID ").append(DiscordID);
            ;
            return builder.toString();
        }

        return "Lichess User is not present!";
    }

    /**
     * Search user by chess com user string.
     *
     * @param ccUser     the cc user
     * @param collection the collection
     * @return the string
     */
    public String searchUserByChessComUser(String ccUser, MongoCollection<Document> collection) {
        String DiscordID = verificationSearch.getDiscordIdByChesscomUsername(collection, ccUser.toLowerCase());
        StringBuilder builder = new StringBuilder();
        if (!DiscordID.equalsIgnoreCase("null")) {
            builder.append("Lichess Username: ").append(verificationSearch.getReletatedLichessName(DiscordID, Main.collection))
                    .append("\n").append("Chesscom Username: ").append(verificationSearch.getReletatedChessName(DiscordID, collection))
                    .append("\n").append("Discord ID ").append(DiscordID);
            ;
            return builder.toString();
        }
        return "ChessCom User is not present!";
    }


}