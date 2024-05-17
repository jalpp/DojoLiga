package dojo.bot.Controller;

import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bson.Document;

import java.util.Objects;

public class UserLeagueActionManager {



    /**
     * Sends top 10 for blitz, rapid, classical ratings
     *
     * @param event      Slash command event
     * @param collection collection of players
     * @param standings  Standings object
     */

    public void sendTop10(SlashCommandInteractionEvent event, MongoCollection<Document> collection,
                          MongoCollection<Document> cc,
                          ComputeStandings standings, AntiSpam Slow_down_buddy) {
        if (!Slow_down_buddy.checkSpam(event)) {
            switch (event.getOptionsByName("select-site").get(0).getAsString()) {
                case "li" -> {
                    switch (event.getOptionsByName("select-tc").get(0).getAsString()) {

                        case "btc" -> {
                            event.reply("generating..").setEphemeral(true).queue();
                            event.getChannel()
                                    .sendMessageEmbeds(standings.getTop10Leaderboard(collection, Time_Control.BLITZ, "Lichessname").build())
                                    .queue();
                        }

                        case "rtc" -> {
                            event.reply("generating..").setEphemeral(true).queue();
                            event.getChannel()
                                    .sendMessageEmbeds(standings.getTop10Leaderboard(collection, Time_Control.RAPID, "Lichessname").build())
                                    .queue();
                        }

                        case "ctc" -> {
                            event.reply("generating..").setEphemeral(true).queue();
                            event.getChannel().sendMessageEmbeds(
                                    standings.getTop10Leaderboard(collection, Time_Control.CLASSICAL, "Lichessname").build()).queue();
                        }

                    }
                }

                case "cc" -> {
                    switch (event.getOptionsByName("select-tc").get(0).getAsString()) {

                        case "btc" -> {
                            event.reply("generating..").setEphemeral(true).queue();
                            event.getChannel()
                                    .sendMessageEmbeds(standings.getTop10Leaderboard(cc, Time_Control.BLITZ, "Chesscomname").build())
                                    .queue();
                        }

                        case "rtc" -> {
                            event.reply("generating..").setEphemeral(true).queue();
                            event.getChannel()
                                    .sendMessageEmbeds(standings.getTop10Leaderboard(cc, Time_Control.RAPID, "Chesscomname").build())
                                    .queue();
                        }

                        case "ctc" -> event.reply("Chess.com rapid is equal to rapid! Please select Rapid time control!").setEphemeral(true).queue();

                    }
                }

            }
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").queue();
        }

    }



    /**
     * Discord action to fire player ranks
     *
     * @param event      Slash command event
     * @param compute    Compute scores object
     * @param collection collection of players
     */

    public void getRankReact(SlashCommandInteractionEvent event, ComputeScores compute,
                             MongoCollection<Document> collection, AntiSpam Slow_down_buddy) {
        if (!Slow_down_buddy.checkSpam(event)) {
            event.reply("generating..").setEphemeral(true).queue();
            event.getChannel().sendMessageEmbeds(compute.getPlayerRankCard(event.getUser().getId(), collection).build())
                    .queue();
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }
    }


    /**
     * Discord action to fire player scores
     *
     * @param event      Slash command event
     * @param compute    Compute scores object
     * @param collection collection of players
     */

    public void getScoreReact(SlashCommandInteractionEvent event, ComputeScores compute,
                              MongoCollection<Document> collection, AntiSpam Slow_down_buddy) {
        if (!Slow_down_buddy.checkSpam(event)) {
            event.reply("generating..").setEphemeral(true).queue();
            event.getChannel().sendMessageEmbeds(compute.getPlayerScore(event.getUser().getId(), collection).build())
                    .queue();
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }
    }


    /**
     * unlink the belt for the user
     * @param event Discord trigger event
     */
    public void unlinkeUserBelt(SlashCommandInteractionEvent event){
        ManageRoles.removePreviousRoles(event.getGuild(), Objects.requireNonNull(event.getMember()));
        event.reply("Successfully unlinked belt for " + event.getMember().getUser().getAsMention()).queue();
    }










}
