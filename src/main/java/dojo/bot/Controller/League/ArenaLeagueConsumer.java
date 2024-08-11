package dojo.bot.Controller.League;

import chariot.api.TournamentsAuth;

import java.time.ZonedDateTime;
import java.util.function.Consumer;

public class ArenaLeagueConsumer {


    /**
     * Consumer for arena league to consume the inputs and pass it to Lichess;s Java client to create
     * the tournaments
     *
     * @param Maxrating the max rating
     * @param team      the Lichess team
     * @param iszerk    can players zerk
     * @param name      name of the league
     * @param fen       arena's starting chess position
     * @param isRated   do players get rating effected
     * @param duration  the duration of the league
     * @param time      the time it starts
     * @param clockmins clock mins for the game
     * @param clocksecs clock secs for the game
     * @param desc      the small description
     * @return the Consumer Arena Builder
     */

    public static Consumer<TournamentsAuth.ArenaBuilder> leagueConsumerInvoker(Integer Maxrating, String team, Boolean iszerk, String name, String fen, Boolean isRated, Integer duration, ZonedDateTime time, Integer clockmins, Integer clocksecs, String desc) {
        Consumer<TournamentsAuth.ArenaBuilder> totalBuilder = builder -> {
        };

        if (clockmins != null && clocksecs != null && Maxrating != null && fen != null) {

            totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                    berserkable(iszerk).conditionMaxRating(Maxrating).minutes(duration).rated(isRated).position(fen).name(name).description(desc).startTime(time).conditionTeam(team));

        } else if (clockmins != null && clocksecs != null && Maxrating == null && fen != null) {

            totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                    berserkable(iszerk).minutes(duration).rated(isRated).position(fen).name(name).description(desc).startTime(time).conditionTeam(team));

        } else if (clockmins != null && clocksecs != null && Maxrating != null) {

            totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                    berserkable(iszerk).conditionMaxRating(Maxrating).minutes(duration).rated(isRated).name(name).description(desc).startTime(time).conditionTeam(team));

        } else if (clockmins != null && clocksecs != null && fen == null && Maxrating == null) {

            totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                    berserkable(iszerk).minutes(duration).rated(isRated).name(name).description(desc).startTime(time).conditionTeam(team));
        }


        return totalBuilder;

    }


}
