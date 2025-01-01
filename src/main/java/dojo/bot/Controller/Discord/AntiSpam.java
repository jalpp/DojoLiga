package dojo.bot.Controller.Discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Anti spam.
 */
public class AntiSpam {

    private final Map<String, Long> userRequestMap;

    private final long timeFrame;

    private final int maxRequests;

    /**
     * Instantiates a new Anti spam.
     *
     * @param timeFrame   the time frame
     * @param maxRequests the max requests
     */
    public AntiSpam(long timeFrame, int maxRequests) {
        userRequestMap = new HashMap<>();
        this.timeFrame = timeFrame;
        this.maxRequests = maxRequests;
    }

    /**
     * Check spam boolean.
     *
     * @param event the event
     * @return the boolean
     */
    public boolean checkSpam(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();

        Long lastRequestTime = userRequestMap.get(userId);
        if (lastRequestTime != null && System.currentTimeMillis() - lastRequestTime < timeFrame) {
            int numRequests = 0;
            for (long timestamp : userRequestMap.values()) {
                if (System.currentTimeMillis() - timestamp < timeFrame) {
                    numRequests++;
                }
            }
            if (numRequests >= maxRequests) {
                return true;
            }
        }

        // Update the user's request timestamp
        userRequestMap.put(userId, System.currentTimeMillis());
        return false;
    }


}

