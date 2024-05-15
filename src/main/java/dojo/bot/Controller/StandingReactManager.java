package dojo.bot.Controller;

import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bson.Document;

import static dojo.bot.Controller.DiscordAdmin.isDiscordAdmin;

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

}
