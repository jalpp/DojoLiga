package dojo.bot.Controller.User;

import chariot.model.Trophy;
import java.util.HashMap;
import java.util.Map;

/**
 * The type User trophy.
 */
public class UserTrophy {

    private final String trophyName;
    private final Trophy trophy;

    /**
     * Instantiates a new User trophy.
     *
     * @param trophy the trophy
     */
    public UserTrophy(Trophy trophy) {
        this.trophy = trophy;
        this.trophyName = trophy.name();
    }

    /**
     * Get image link string.
     *
     * @return the string
     */
    public String getImageLink() {
        Map<String, String> getLink = new HashMap<>();
        getLink.put("Marathon Winner", "\uD83D\uDD2E");
        getLink.put("Marathon Top 10", "\uD83C\uDF15");
        getLink.put("Other", "\uD83C\uDFC6");
        getLink.put("Verified account", "✅");
        getLink.put("Lichess moderator", "\uD83D\uDC41️");
        getLink.put("Lichess content team", "✍️");
        getLink.put("Lichess developer", "\uD83D\uDEE0️");

        String imageLink = getLink.getOrDefault(trophyName, getLink.get("Other"));
        return imageLink + " " + trophyName;
    }
}