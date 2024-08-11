package dojo.bot.Controller.League;

import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bson.Document;

import java.time.DayOfWeek;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class CreateLeagueManager {


    /**
     * Discord Action to create an arena league
     *
     * @param event      Slashcommand event
     * @param day        the day of the week for the league tournament to be played
     * @param interval   the interval for league to happen
     * @param collection the collection of players
     */

    public void createLeagueArena(SlashCommandInteractionEvent event, DayOfWeek day, Interval interval,
                                  MongoCollection<Document> collection) {
         ArenaLeague league = new ArenaLeague(
                event.getOption("league-name").getAsString(),
                event.getOption("league-desc").getAsString(),
                event.getOption("tournament-count").getAsInt(),
                Type.ARENA,
                interval,
                collection,
                event.getOption("time-start").getAsInt(),
                event.getOption("clock-time").getAsInt(),
                event.getOption("clock-increment").getAsInt(),
                event.getOption("duration").getAsInt(),
                event.getOption("arena-fen").getAsString(),
                event.getOption("max-rating").getAsInt(),
                day);

        league.setLEAGUE_TIME_TYPE();
        DisplayLoading(event);
        event.getChannel().sendMessage(league.createTournament()).queue();
    }

    /**
     * Discord Action to create a swiss league
     *
     * @param event      Slashcommand event
     * @param day        the day of the week for the league tournament to be played
     * @param interval   the interval for league to happen
     * @param collection the collection of players
     */

    public void createLeagueSwiss(SlashCommandInteractionEvent event, DayOfWeek day, Interval interval,
                                  MongoCollection<Document> collection) {
        SwissLeague league = new SwissLeague(
                event.getOption("league-name-s").getAsString(),
                event.getOption("league-desc-s").getAsString(),
                event.getOption("tournament-count-s").getAsInt(),
                Type.SWISS,
                interval,
                collection,
                event.getOption("time-start-s").getAsInt(),
                event.getOption("clock-time-s").getAsInt(),
                event.getOption("clock-increment-s").getAsInt(),
                event.getOption("nb-rounds").getAsInt(),
                event.getOption("round-interval").getAsInt() * 60,
                event.getOption("swiss-fen").getAsString(),
                event.getOption("max-rating-swiss").getAsInt(),
                day);

        league.setLEAGUE_TIME_TYPE();
        event.reply(
                        "Thinking.. \n Checking inputs... \n Connecting to Dojo Server .. \n Connecting to Lichess server .. \n Watching ChessDojo.. \n Doing endgame ninja workout ..")
                .queue();
        event.getChannel().sendMessage(league.createTournament()).queue();
    }


    /**
     * Loading service which beeps in discord channel load animation
     *
     * @param event
     */

    public void DisplayLoading(SlashCommandInteractionEvent event) {
        String[] tasks = {"Sparring against Jack", "Watching U1000 Grad show", "Doing Tactics Test", "Learning chess"};
        event.reply("Creating the league! Please wait").queue(msg ->
        {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int count = 0;

                @Override
                public void run() {
                    msg.editOriginal(tasks[new Random().nextInt(tasks.length)]).queue();
                    count++;
                    if (count > 10) {
                        timer.cancel();
                    }
                }
            }, 0, 1000);


        });
    }


}
