package dojo.bot.Controller.League;

import chariot.api.TournamentsAuth;

import java.time.ZonedDateTime;
import java.util.function.Consumer;

public class SwissLeagueConsumer {


    public static Consumer<TournamentsAuth.SwissBuilder> leagueConsumerInvoker(Integer Maxrating, String team, int nbRounds, String name, String fen, Boolean isRated, Integer interval, ZonedDateTime time, Integer clockmins, Integer clocksecs, String desc){
        Consumer<TournamentsAuth.SwissBuilder> totalBuilder = builder -> {};


        if(clockmins != null && clocksecs != null && Maxrating != null && fen != null){ // everything

            totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                    name(name).startsAt(time).nbRounds(nbRounds).position(fen).rated(isRated).conditionMaxRating(Maxrating).roundInterval(interval).description(desc));

        }else if(clockmins != null && clocksecs != null && Maxrating == null && fen != null){ // no max

            totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                    name(name).startsAt(time).nbRounds(nbRounds).position(fen).rated(isRated).roundInterval(interval).description(desc));

        }else if(clockmins != null && clocksecs != null && Maxrating != null){ // no FEN

            totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                    name(name).startsAt(time).nbRounds(nbRounds).rated(isRated).roundInterval(interval).description(desc).conditionMaxRating(Maxrating));

        }else if(clockmins != null && clocksecs != null && fen == null && Maxrating == null) { // no fen and max
            totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                    name(name).startsAt(time).nbRounds(nbRounds).rated(isRated).roundInterval(interval).description(desc));

        }

        return totalBuilder;

    }






}
