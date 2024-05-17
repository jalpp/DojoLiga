package dojo.bot.Controller;

import com.mongodb.client.MongoCollection;
import dojo.bot.Commands.Verification;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.Document;

import java.time.DayOfWeek;
import java.util.Arrays;

import static dojo.bot.Controller.DiscordAdmin.isDiscordAdmin;
import static dojo.bot.Controller.DiscordAdmin.isDiscordAdminMessage;

public class ConfigLeagueManager {

    private final CreateLeagueManager leagueManager = new CreateLeagueManager();


    /**
     * Configs an arena league
     *
     * @param event                 Slash command event
     * @param arenaLeagueCollection collection of arenas
     */

    public void configLeagueArena(SlashCommandInteractionEvent event, MongoCollection<Document> arenaLeagueCollection) {


            if (isDiscordAdmin(event)) {

                if (event.getOptionsByName("interval").get(0).getAsString().equalsIgnoreCase("daily")) {

                    switch (event.getOptionsByName("day-of-week").get(0).getAsString()) {
                        case "mon" ->
                                this.createLeagueArena(event, DayOfWeek.MONDAY, Interval.DAILY_INTERVAL,
                                        arenaLeagueCollection);

                        case "tue" -> this.createLeagueArena(event, DayOfWeek.TUESDAY, Interval.DAILY_INTERVAL,
                                arenaLeagueCollection);

                        case "wed" ->
                                this.createLeagueArena(event, DayOfWeek.WEDNESDAY, Interval.DAILY_INTERVAL,
                                        arenaLeagueCollection);

                        case "th" ->
                                this.createLeagueArena(event, DayOfWeek.THURSDAY, Interval.DAILY_INTERVAL,
                                        arenaLeagueCollection);

                        case "fri" ->
                                this.createLeagueArena(event, DayOfWeek.FRIDAY, Interval.DAILY_INTERVAL,
                                        arenaLeagueCollection);

                        case "sat" -> this.createLeagueArena(event, DayOfWeek.SATURDAY, Interval.DAILY_INTERVAL,
                                arenaLeagueCollection);

                        case "sun" ->
                                this.createLeagueArena(event, DayOfWeek.SUNDAY, Interval.DAILY_INTERVAL,
                                        arenaLeagueCollection);
                    }

                } else if (event.getOptionsByName("interval").get(0).getAsString().equalsIgnoreCase("weekly")) {
                    switch (event.getOptionsByName("day-of-week").get(0).getAsString()) {
                        case "mon" ->
                                this.createLeagueArena(event, DayOfWeek.MONDAY, Interval.WEEKLY_INTERVAL,
                                        arenaLeagueCollection);

                        case "tue" -> this.createLeagueArena(event, DayOfWeek.TUESDAY, Interval.WEEKLY_INTERVAL,
                                arenaLeagueCollection);

                        case "wed" ->
                                this.createLeagueArena(event, DayOfWeek.WEDNESDAY, Interval.WEEKLY_INTERVAL,
                                        arenaLeagueCollection);

                        case "th" ->
                                this.createLeagueArena(event, DayOfWeek.THURSDAY, Interval.WEEKLY_INTERVAL,
                                        arenaLeagueCollection);

                        case "fri" ->
                                this.createLeagueArena(event, DayOfWeek.FRIDAY, Interval.WEEKLY_INTERVAL,
                                        arenaLeagueCollection);

                        case "sat" -> this.createLeagueArena(event, DayOfWeek.SATURDAY, Interval.WEEKLY_INTERVAL,
                                arenaLeagueCollection);

                        case "sun" ->
                                this.createLeagueArena(event, DayOfWeek.SUNDAY, Interval.WEEKLY_INTERVAL,
                                        arenaLeagueCollection);
                    }

                } else if (event.getOptionsByName("interval").get(0).getAsString().equalsIgnoreCase("monthly")) {
                    switch (event.getOptionsByName("day-of-week").get(0).getAsString()) {
                        case "mon" -> this.createLeagueArena(event, DayOfWeek.MONDAY, Interval.MONTHLY_INTERVAL,
                                arenaLeagueCollection);

                        case "tue" -> this.createLeagueArena(event, DayOfWeek.TUESDAY, Interval.MONTHLY_INTERVAL,
                                arenaLeagueCollection);

                        case "wed" -> this.createLeagueArena(event, DayOfWeek.WEDNESDAY, Interval.MONTHLY_INTERVAL,
                                arenaLeagueCollection);

                        case "th" -> this.createLeagueArena(event, DayOfWeek.THURSDAY, Interval.MONTHLY_INTERVAL,
                                arenaLeagueCollection);

                        case "fri" -> this.createLeagueArena(event, DayOfWeek.FRIDAY, Interval.MONTHLY_INTERVAL,
                                arenaLeagueCollection);

                        case "sat" -> this.createLeagueArena(event, DayOfWeek.SATURDAY, Interval.MONTHLY_INTERVAL,
                                arenaLeagueCollection);

                        case "sun" ->
                                this.createLeagueArena(event, DayOfWeek.SUNDAY, Interval.MONTHLY_INTERVAL,
                                        arenaLeagueCollection);
                    }
                }

            } else {
                event.reply("Sorry! You are not an admin!").queue();
            }

    }



    /**
     * Configs a swiss league
     *
     * @param event                 Slash command event
     * @param swissLeagueCollection collection of swiss tournaments
     */

    public void configLeagueSwiss(SlashCommandInteractionEvent event, MongoCollection<Document> swissLeagueCollection) {


            if (isDiscordAdmin(event)) {

                if (event.getOptionsByName("interval-swiss").get(0).getAsString().equalsIgnoreCase("daily-swiss")) {
                    validateSwissInput(event);
                    switch (event.getOptionsByName("day-of-week-swiss").get(0).getAsString()) {
                        case "mon" -> this.createLeagueSwiss(event, DayOfWeek.MONDAY, Interval.DAILY_INTERVAL,
                                swissLeagueCollection);

                        case "tue" -> this.createLeagueSwiss(event, DayOfWeek.TUESDAY, Interval.DAILY_INTERVAL,
                                swissLeagueCollection);

                        case "wed" -> this.createLeagueSwiss(event, DayOfWeek.WEDNESDAY, Interval.DAILY_INTERVAL,
                                swissLeagueCollection);

                        case "th" -> this.createLeagueSwiss(event, DayOfWeek.THURSDAY, Interval.DAILY_INTERVAL,
                                swissLeagueCollection);

                        case "fri" -> this.createLeagueSwiss(event, DayOfWeek.FRIDAY, Interval.DAILY_INTERVAL,
                                swissLeagueCollection);

                        case "sat" -> this.createLeagueSwiss(event, DayOfWeek.SATURDAY, Interval.DAILY_INTERVAL,
                                swissLeagueCollection);

                        case "sun" -> this.createLeagueSwiss(event, DayOfWeek.SUNDAY, Interval.DAILY_INTERVAL,
                                swissLeagueCollection);
                    }

                } else if (event.getOptionsByName("interval-swiss").get(0).getAsString()
                        .equalsIgnoreCase("weekly-swiss")) {
                    validateSwissInput(event);
                    switch (event.getOptionsByName("day-of-week-swiss").get(0).getAsString()) {
                        case "mon" -> this.createLeagueSwiss(event, DayOfWeek.MONDAY, Interval.WEEKLY_INTERVAL,
                                swissLeagueCollection);

                        case "tue" -> this.createLeagueSwiss(event, DayOfWeek.TUESDAY, Interval.WEEKLY_INTERVAL,
                                swissLeagueCollection);

                        case "wed" -> this.createLeagueSwiss(event, DayOfWeek.WEDNESDAY, Interval.WEEKLY_INTERVAL,
                                swissLeagueCollection);

                        case "th" -> this.createLeagueSwiss(event, DayOfWeek.THURSDAY, Interval.WEEKLY_INTERVAL,
                                swissLeagueCollection);

                        case "fri" -> this.createLeagueSwiss(event, DayOfWeek.FRIDAY, Interval.WEEKLY_INTERVAL,
                                swissLeagueCollection);

                        case "sat" -> this.createLeagueSwiss(event, DayOfWeek.SATURDAY, Interval.WEEKLY_INTERVAL,
                                swissLeagueCollection);

                        case "sun" -> this.createLeagueSwiss(event, DayOfWeek.SUNDAY, Interval.WEEKLY_INTERVAL,
                                swissLeagueCollection);
                    }

                } else if (event.getOptionsByName("interval-swiss").get(0).getAsString()
                        .equalsIgnoreCase("monthly-swiss")) {
                    validateSwissInput(event);
                    switch (event.getOptionsByName("day-of-week-swiss").get(0).getAsString()) {
                        case "mon" -> this.createLeagueSwiss(event, DayOfWeek.MONDAY, Interval.MONTHLY_INTERVAL,
                                swissLeagueCollection);

                        case "tue" -> this.createLeagueSwiss(event, DayOfWeek.TUESDAY, Interval.MONTHLY_INTERVAL,
                                swissLeagueCollection);

                        case "wed" -> this.createLeagueSwiss(event, DayOfWeek.WEDNESDAY, Interval.MONTHLY_INTERVAL,
                                swissLeagueCollection);

                        case "th" -> this.createLeagueSwiss(event, DayOfWeek.THURSDAY, Interval.MONTHLY_INTERVAL,
                                swissLeagueCollection);

                        case "fri" -> this.createLeagueSwiss(event, DayOfWeek.FRIDAY, Interval.MONTHLY_INTERVAL,
                                swissLeagueCollection);

                        case "sat" -> this.createLeagueSwiss(event, DayOfWeek.SATURDAY, Interval.MONTHLY_INTERVAL,
                                swissLeagueCollection);

                        case "sun" -> this.createLeagueSwiss(event, DayOfWeek.SUNDAY, Interval.MONTHLY_INTERVAL,
                                swissLeagueCollection);
                    }

                }

            } else {
                event.reply("Sorry! You are not an admin!").queue();
            }

    }

    private void createLeagueSwiss(SlashCommandInteractionEvent event, DayOfWeek dayOfWeek, Interval interval, MongoCollection<Document> swissLeagueCollection) {
        leagueManager.createLeagueSwiss(event,dayOfWeek,interval,swissLeagueCollection);
    }


    private void createLeagueArena(SlashCommandInteractionEvent event, DayOfWeek dayOfWeek, Interval interval, MongoCollection<Document> arenaLeagueCollection) {
        leagueManager.createLeagueArena(event,dayOfWeek,interval,arenaLeagueCollection);
    }


    /**
     * Validates Swiss Input
     *
     * @param event Slashcommand event
     */

    public static void validateSwissInput(SlashCommandInteractionEvent event) {
        int[] validIntervals = { 1, 2, 3, 5, 10, 15, 20, 30, 45, 60, 1440, 2880, 10080 };
        boolean val = false;

        for (int v : validIntervals) {
            if (event.getOption("round-interval").getAsInt() == v) {
                val = true;
                break;
            }
        }

        if (!val) {
            event.reply("Error! The round interval must be within this mins " + Arrays.toString(validIntervals))
                    .queue();

        } else if (event.getOption("nb-rounds").getAsInt() < 3 || (event.getOption("nb-rounds").getAsInt() > 10)) {
            event.reply("Error! Number of rounds must be between 3 and 9").queue();

        }
    }



    /**
     * Discord action to send ChessDojo URL based on if user is verified
     *
     * @param event      Slash command event
     * @param passport   Verification object
     * @param collection collection of players
     */

    public void leagueRegister(SlashCommandInteractionEvent event, Verification passport,
                               MongoCollection<Document> collection) {
            if (!passport.userPresentNormal(collection, event.getUser().getId())) {
                event.reply("Registration failed, please run **/verify** to authenticate your Lichess.org account").setEphemeral(true)
                        .queue();
            } else {
                event.reply(
                                "Please join ChessDojo team to enter in upcoming league/tournaments! Your request will be accepted by Chessdojo mods. \n"
                                        +
                                        "Once you get accepted in the team, you will receive Lichess DM for confirmation")
                        .addActionRow(Button.link("https://lichess.org/team/chessdojo", "Join Team")).queue();
            }
    }


    /**
     * Send Liga message
     * @param event Discord trigger event
     */

    public void sendLigaMessage(MessageReceivedEvent event){
        if(isDiscordAdminMessage(event)){
            Messenger messenger = new Messenger();
            messenger.sendMessage();
            event.getChannel().sendMessage("I have successfully notified Lichess team! Good luck to everyone playing!").queue();
        }else{
            event.getChannel().sendMessage("Error! Automated Message have been shut of, please call admin to send the messages!").queue();
        }

    }


}
