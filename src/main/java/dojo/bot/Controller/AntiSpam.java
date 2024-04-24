package dojo.bot.Controller;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class to check the spam
 */

public class AntiSpam {


    private Map<String, Long> userRequestMap;


    private long timeFrame;


    private int maxRequests;

    public AntiSpam(long timeFrame, int maxRequests) {
        userRequestMap = new HashMap<>();
        this.timeFrame = timeFrame;
        this.maxRequests = maxRequests;
    }

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


        userRequestMap.put(userId, System.currentTimeMillis());
        return false;
    }

    public boolean checkSpamBot(MessageReceivedEvent event) {
        String userId = Objects.requireNonNull(event.getMember()).getUser().getId();


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


        userRequestMap.put(userId, System.currentTimeMillis());
        return false;
    }
}
