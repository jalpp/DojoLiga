package dojo.bot.Controller.Standing;

import chariot.Client;
import com.mongodb.client.MongoCollection;
import dojo.bot.Controller.Discord.AntiSpam;
import dojo.bot.Controller.User.UserArena;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.Document;

import java.util.Objects;

import static dojo.bot.Controller.Discord.DiscordAdmin.isDiscordAdmin;

public class StandingReactManager {


    /**
     * Discord Action to send standings in Discord
     *
     * @param event      SlashCommand event
     * @param standings  compute standings object
     * @param collection collection of players
     */

    public void StandingsReact(SlashCommandInteractionEvent event, ComputeStandings standings,
                               MongoCollection<Document> collection) {
            if (isDiscordAdmin(event)) {
                switch (event.getOptionsByName("time-control").get(0).getAsString()) {

                    case "sp-tc" -> {
                        switch (event.getOptionsByName("point-type").get(0).getAsString()) {

                            case "arena-total-points", "arena-gp-points", "swiss-total-points", "swiss-gp-points",
                                    "comb-total-points", "comb-gp-points" ->
                                    event.reply(
                                                    "Error! Mix time control only supports sparring point type! Please select sparring (Middlegame/Endgame) leaderboard next time!")
                                            .queue();

                            case "sp-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(standings.calculateSparringMixStandings(collection).build())
                                        .queue();
                            }

                            case "eg-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateEndgameSparringMixStandings(collection).build())
                                        .queue();
                            }
                        }
                    }

                    case "blitz-tc" -> {

                        switch (event.getOptionsByName("point-type").get(0).getAsString()) {

                            case "arena-total-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateBlitzArenaTotalStandings(collection).build())
                                        .queue();

                            }

                            case "arena-gp-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(standings.calculateBlitzArenaGpStandings(collection).build())
                                        .queue();

                            }

                            case "swiss-total-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateBlitzSwissTotalStandings(collection).build())
                                        .queue();

                            }

                            case "swiss-gp-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(standings.calculateBlitzSwissGpStandings(collection).build())
                                        .queue();

                            }

                            case "comb-total-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel().sendMessageEmbeds(
                                        standings.calculateBlitzCombTotalStandings(collection).build()).queue();
                            }

                            case "comb-gp-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateBlitzCombTotalGPStandings(collection).build())
                                        .queue();

                            }
                        }
                    }

                    case "rapid-tc" -> {

                        switch (event.getOptionsByName("point-type").get(0).getAsString()) {

                            case "arena-total-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateRapidArenaTotalStandings(collection).build())
                                        .queue();

                            }

                            case "arena-gp-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(standings.calculateRapidArenaGpStandings(collection).build())
                                        .queue();

                            }

                            case "swiss-total-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateRapidSwissTotalStandings(collection).build())
                                        .queue();

                            }

                            case "swiss-gp-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(standings.calculateRapidSwissGpStandings(collection).build())
                                        .queue();

                            }

                            case "comb-total-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel().sendMessageEmbeds(
                                        standings.calculateRapidCombTotalStandings(collection).build()).queue();
                            }

                            case "comb-gp-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateRapidCombTotalGPStandings(collection).build())
                                        .queue();

                            }
                        }
                    }

                    case "classical-tc" -> {

                        switch (event.getOptionsByName("point-type").get(0).getAsString()) {

                            case "arena-total-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateClassicalArenaTotalStandings(collection).build())
                                        .queue();

                            }
                            case "arena-gp-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateClassicalArenaGpStandings(collection).build())
                                        .queue();

                            }

                            case "swiss-total-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateClassicalSwissTotalStandings(collection).build())
                                        .queue();

                            }

                            case "swiss-gp-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateClassicalSwissGpStandings(collection).build())
                                        .queue();

                            }

                            case "comb-total-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateClassicalCombTotalStandings(collection).build())
                                        .queue();
                            }

                            case "comb-gp-points" -> {
                                event.reply("Generating...").queue();
                                event.getChannel()
                                        .sendMessageEmbeds(
                                                standings.calculateClassicalCombTotalGPStandings(collection).build())
                                        .queue();
                            }
                        }
                    }
                }

            } else {
                event.reply("Sorry! You are not an admin!").queue();
            }


    }


    /**
     * Sends the standing of Lichess tournament based on Lichess URL
     * @param event Discord trigger event
     * @param client Lichess java client
     * @param Slow_down_buddy Antispam checker
     */

    public void getStandingsForURL(SlashCommandInteractionEvent event, Client client, AntiSpam Slow_down_buddy) {
        if (!Slow_down_buddy.checkSpam(event)) {
            String targetUrl = Objects.requireNonNull(event.getOption("tournament-url")).getAsString();
            if (targetUrl.contains("https://lichess.org/tournament/")
                    || targetUrl.contains("https://lichess.org/swiss/")) {
                UserArena base = new UserArena(client, targetUrl);
                event.replyEmbeds(base.getUserArena().build()).addActionRow(Button.link(targetUrl, "View On Lichess"))
                        .queue();

            } else {
                event.reply("Error! URL must be a Lichess Swiss or Arena URL").setEphemeral(true).queue();
            }
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }
    }


    /**
     * handle sending pairings for the Lichess url
     * @param event Discord trigger event
     * @param Slow_down_buddy Antispam checker
     */

    public void getPairingsReact(SlashCommandInteractionEvent event, AntiSpam Slow_down_buddy) {
        if (!Slow_down_buddy.checkSpam(event)) {
            String url = Objects.requireNonNull(event.getOption("request-url")).getAsString();
            if (url.contains("https://lichess.org/tournament/") || url.contains("https://lichess.org/swiss/")) {
                event.reply("View Pairings on Lichess!").addActionRow(Button.link(url, "View Pairings")).queue();
            } else {
                event.reply("Please provide valid URL!").setEphemeral(true).queue();
            }
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }
    }


    /**
     * Sends 3 leaderboards into #general chat at 1:00 PM everyday
     * @param jda        JDA object
     * @param channelId  Channel ID
     * @param collection collection of players
     */

    public void sendStandingEmbeds(JDA jda, String channelId, MongoCollection<Document> collection, MongoCollection<Document> ccCollection) {

        ComputeStandings standings = new ComputeStandings();
        TextChannel channel = jda.getTextChannelById(channelId);

        if (channel != null) {
            channel.sendMessageEmbeds(standings.calculateBlitzCombTotalGPStandings(collection).build()).queue();
            channel.sendMessageEmbeds(standings.calculateRapidCombTotalGPStandings(ccCollection).build()).queue();
            channel.sendMessageEmbeds(standings.calculateClassicalCombTotalGPStandings(collection).build()).queue();

        } else {
            System.out.println("Channel not found or bot does not have access to the channel with ID: " + channelId);
        }
    }








}
