package dojo.bot.Controller;

import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bson.Document;

import java.time.DayOfWeek;
import java.util.Arrays;

import static dojo.bot.Controller.DiscordAdmin.isDiscordAdmin;

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


}