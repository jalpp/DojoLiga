package dojo.bot.Controller.League;

import chariot.api.TournamentsAuth;

import java.time.ZonedDateTime;
import java.util.function.Consumer;

public class LeagueConsumer {


   public static Consumer<TournamentsAuth.ArenaBuilder> LeagueConsumerArenaInvoker(Integer Maxrating, String team, Boolean iszerk, String name, String fen, Boolean isRated, Integer duration, ZonedDateTime time, Integer clockmins, Integer clocksecs, String desc){
       Consumer<TournamentsAuth.ArenaBuilder> totalBuilder = builder -> {};


       if(clockmins != null && clocksecs != null && Maxrating != null && fen != null){
           totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                   berserkable(iszerk)
                   .conditionMaxRating(Maxrating)
                   .minutes(duration)
                   .rated(isRated)
                   .position(fen)
                   .name(name)
                   .description(desc)
                   .startTime(time)
                   .conditionTeam(team));
       }else if(clockmins != null && clocksecs != null && Maxrating == null && fen != null){
           totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                   berserkable(iszerk)
                   .minutes(duration)
                   .rated(isRated)
                   .position(fen)
                   .name(name)
                   .description(desc)
                   .startTime(time)
                   .conditionTeam(team));
       }else if(clockmins != null && clocksecs != null && Maxrating != null){
           totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                   berserkable(iszerk)
                   .conditionMaxRating(Maxrating)
                   .minutes(duration)
                   .rated(isRated)
                   .name(name)
                   .description(desc)
                   .startTime(time)
                   .conditionTeam(team));
       }else if(clockmins != null && clocksecs != null && fen == null && Maxrating == null) {
           totalBuilder = totalBuilder.andThen(builder -> builder.clock(clockmins, clocksecs).
                   berserkable(iszerk)
                   .minutes(duration)
                   .rated(isRated)
                   .name(name)
                   .description(desc)
                   .startTime(time)
                   .conditionTeam(team));

       }



       return totalBuilder;

   }




}