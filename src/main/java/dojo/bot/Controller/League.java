package dojo.bot.Controller;

import chariot.Client;
import chariot.ClientAuth;
import chariot.model.Arena;
import chariot.model.Fail;
import chariot.model.Swiss;
import com.mongodb.client.MongoCollection;
import dojo.bot.Model.DbTournamentEntry;
import dojo.bot.Model.TournamentManager;
import dojo.bot.Runner.Main;
import org.bson.Document;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;

public class League implements TournamentManager {

    private final ClientAuth client = Client.auth(Main.botToken);
    private final String DOJO_TEAM = Main.IS_BETA ? "teamtesting" : "chessdojo";
    private final String LEAGUE_NAME;
    private final String LEAGUE_DES;
    private final int TournamentCount;
    private final Type LeagueType;

    private Time_Control LEAGUE_TIME_TYPE;

    private final MongoCollection<Document> tournamentCollection;

    private final Interval interval;

    private final int ClockTime;

    private int duration;

    private final int ClockIncrement;

    private int Swiss_rounds;

    private int Swiss_round_interval;

    private final int time_zone_start;

    private final String fen;

    private final int MaxRating;


    private final DayOfWeek dayOfWeekOrMonth;

    public int getClockTime() {
        return ClockTime;
    }


    public int getClockIncrement() {
        return ClockIncrement;
    }


    public League(String LEAGUE_NAME, String LEAGUE_DES, int tournamentCount, Type leagueType, Interval interval,
                  MongoCollection<Document> registry, int time_zone_start, int clockTime, int clockIncrement, int duration,
                  String fen, int maxRating, DayOfWeek dayOfWeek) {
        this.LEAGUE_NAME = LEAGUE_NAME;
        this.LEAGUE_DES = LEAGUE_DES;
        this.TournamentCount = tournamentCount;
        this.LeagueType = leagueType;
        this.interval = interval;
        this.tournamentCollection = registry;
        this.time_zone_start = time_zone_start;
        this.ClockIncrement = clockIncrement;
        this.ClockTime = clockTime;
        this.duration = duration;
        this.fen = fen;
        this.MaxRating = maxRating;
        this.dayOfWeekOrMonth = dayOfWeek;

    }

    public League(String LEAGUE_NAME, String LEAGUE_DES, int tournamentCount, Type leagueType, Interval interval,
                  MongoCollection<Document> registry, int time_zone_start, int clockTime, int clockIncrement,
                  int swiss_rounds, int swiss_round_interval, String fen, int maxRating, DayOfWeek dayOfWeek) {
        this.LEAGUE_NAME = LEAGUE_NAME;
        this.LEAGUE_DES = LEAGUE_DES;
        this.TournamentCount = tournamentCount;
        this.LeagueType = leagueType;
        this.interval = interval;
        this.tournamentCollection = registry;
        this.time_zone_start = time_zone_start;
        this.ClockIncrement = clockIncrement;
        this.ClockTime = clockTime;
        this.Swiss_rounds = swiss_rounds;
        this.Swiss_round_interval = swiss_round_interval;
        this.fen = fen;
        this.MaxRating = maxRating;
        this.dayOfWeekOrMonth = dayOfWeek;

    }

    public void setLEAGUE_TIME_TYPE() {
        if (ClockTime < 0 && ClockIncrement < 0) {
            this.LEAGUE_TIME_TYPE = Time_Control.MIX;
        } else if (ClockTime >= 3 && ClockTime <= 8 && (ClockIncrement >= 0 && ClockIncrement <= 180)) {
            this.LEAGUE_TIME_TYPE = Time_Control.BLITZ;
        } else if (ClockTime >= 9 && ClockTime <= 29 && (ClockIncrement >= 0 && ClockIncrement <= 180)) {
            this.LEAGUE_TIME_TYPE = Time_Control.RAPID;
        } else if (ClockTime >= 30 && ClockTime <= 180 && (ClockIncrement >= 0 && ClockIncrement <= 180)) {
            this.LEAGUE_TIME_TYPE = Time_Control.CLASSICAL;
        } else {
            this.LEAGUE_TIME_TYPE = Time_Control.BLITZ;
        }

    }

    public int getMilitaryClock() {


        if(this.time_zone_start == 0){
            return 0;
        }

        if (this.time_zone_start < -1 || this.time_zone_start >= 24) {
            return 13;
        }

        return this.time_zone_start;
    }


    public String getName_Pattern(int index) {
        return splitFENs(this.LEAGUE_NAME).get(0) + " No. " + index;
    }


    @Override
    public String createTournament() {

        switch (this.LeagueType) {

            case ARENA -> {


                int[] checkTime = {2,3,4,5,6,7,8,10,15,20,25,30,40,50,60};
                int[] checkSec = {0,1,2,3,4,5,6,7,10,15,20,25,30,40,50,60};
                int[] checkDuration = {20,25, 30, 35, 40, 45, 50, 55, 60, 70, 80, 90 ,100, 110, 120, 150, 180, 210,240, 270, 300, 330, 360, 420, 480, 540, 600, 720};
                int[] maxRating= {2200, 2100, 2000 ,1900, 1800 ,1700, 1600, 1500, 1400, 1300, 1200, 1100 ,1000, 900, 800, 0};





                        if (Arrays.stream(maxRating).anyMatch(n -> n == this.MaxRating)) {

                            if (Arrays.stream(checkSec).anyMatch(n -> n == this.ClockIncrement)) {

                                if (Arrays.stream(checkTime).anyMatch(n -> n == this.ClockTime)) {

                                    if (Arrays.stream(checkDuration).anyMatch(n -> n == this.duration)) {

                                        if (this.TournamentCount <= 0 || this.TournamentCount >= 13) {
                                            return "Invalid Request, Tournament count must be 1 to 12 tournament count (1 to 12 days)";
                                        } else {
                                            if (!this.fen
                                                    .equalsIgnoreCase("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")) {
                                                if (this.MaxRating != 0) {
                                                    switch (this.LEAGUE_TIME_TYPE){
                                                        case BLITZ -> {
                                                            return createArenaWithMaxAndFENParameter(true);
                                                        }
                                                        case RAPID, CLASSICAL -> {
                                                            return createArenaWithMaxAndFENParameter(false);
                                                        }
                                                    }
                                                } else {
                                                    switch (this.LEAGUE_TIME_TYPE){
                                                        case BLITZ -> {
                                                            return createArenaWithoutMaxAndWithFEN(true);
                                                        }
                                                        case RAPID, CLASSICAL -> {
                                                            return createArenaWithoutMaxAndWithFEN(false);
                                                        }
                                                    }

                                                }
                                            } else {
                                                if (this.MaxRating != 0) {
                                                    switch (this.LEAGUE_TIME_TYPE){
                                                        case BLITZ -> {
                                                            return createArenaWithoutFENAndWithMAX(true);
                                                        }
                                                        case RAPID, CLASSICAL -> {
                                                            return createArenaWithoutFENAndWithMAX(false);
                                                        }
                                                    }

                                                } else {
                                                    switch (this.LEAGUE_TIME_TYPE){
                                                        case BLITZ -> {
                                                            return createArenaWithoutFENAndMAX(true);
                                                        }
                                                        case RAPID, CLASSICAL -> {
                                                            return createArenaWithoutFENAndMAX(false);
                                                        }
                                                    }

                                                }

                                            }
                                        }

                                    } else {
                                        return "Invalid Duration, Please provide proper duration, duration must be this values \n " + Arrays.toString(checkDuration);
                                    }

                                } else {
                                    return "Invalid Clock Time in mins, please provide proper mins, mins must be \n " + Arrays.toString(checkTime);

                                }

                            } else {
                                return "Invalid Clock Increment in secs, please provide proper increment, increment must be \n " + Arrays.toString(checkSec);
                            }

                        } else {
                            return "Invalid Player Max rating! please provide proper max rating value, max rating value must be \n " + Arrays.toString(maxRating);
                        }





            }

            case SWISS -> {

                int[] timesInMinutes = {
                        1, 2, 3, 4, 5, 6, 7, 8, 10, 15, 20, 25, 30, 40,
                        50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180
                };

                int[] maxRating= {2200, 2100, 2000 ,1900, 1800 ,1700, 1600, 1500, 1400, 1300, 1200, 1100 ,1000, 900, 800, 0};



                    if (Arrays.stream(maxRating).anyMatch(n -> n == this.MaxRating)) {

                        if (Arrays.stream(timesInMinutes).anyMatch(n -> n == this.ClockTime)) {

                            if (!(this.ClockIncrement < 0 || this.ClockIncrement > 120)) {


                                if (this.TournamentCount <= 0 || this.TournamentCount >= 13) {
                                    return "Invalid Request, Swiss tournament count must be 1 to 12 tournament count (1 to 12 days)";
                                } else {
                                    if (!this.fen
                                            .equalsIgnoreCase("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")) {
                                        if (this.MaxRating != 0) {
                                            return createSwissWithMaxAndFENParameter();
                                        } else {
                                            return createSwissWithFENWithoutMax();
                                        }

                                    } else {
                                        if (MaxRating != 0) {
                                            return createSwissWithMAXWithoutFEN();
                                        } else {
                                            return createSwissWithoutMaxAndFEN();
                                        }

                                    }
                                }

                            } else {
                                return "Invalid Clock Increment! Clock increment must be within [0, 120]";
                            }
                        } else {
                            return "Invalid Clock Time! Please provide proper clock time, clock must be \n " + Arrays.toString(timesInMinutes);

                        }

                    } else {
                        return "Invalid Player Max rating! please provide proper max rating value, max rating value must be \n " + Arrays.toString(maxRating);
                    }


            }

        }

        return "Error! Something went wrong..";
    }


    public static ArrayList<String> splitFENs(String input) {
        ArrayList<String> fenList = new ArrayList<>();

        String[] fens = input.split(",");

        for (String fen : fens) {
            fenList.add(fen.trim());
        }

        return fenList;
    }


    public String createArenaWithMaxAndFENParameter(Boolean isZerk) {


        if(this.TournamentCount != splitFENs(this.fen).size() || this.TournamentCount != splitFENs(this.LEAGUE_NAME).size() || splitFENs(this.fen).size() != splitFENs(this.LEAGUE_NAME).size()) {
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < this.TournamentCount; d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> {

                        daysIndex = tmr.plusDays(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                    case WEEKLY_INTERVAL -> {
                        var tmr1 = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmr1.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmr1 = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmr1.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                }

                int finalD = d + 1;
                ZonedDateTime finalDaysIndex = daysIndex;

                    var res = this.client.tournaments().createArena(addparam -> addparam.clock(
                                    this.getClockTime(), this.getClockIncrement())
                            .berserkable(isZerk)
                            .name(this.getName_Pattern(finalD)).description(this.LEAGUE_DES)
                            .conditionTeam(DOJO_TEAM)
                            .minutes(duration)
                            .position(splitFENs(this.fen).get(0))
                            .rated(true)
                            .conditionMaxRating(this.MaxRating)
                            .startTime(finalDaysIndex));

                    if(res instanceof Fail<Arena> fail){
                        return fail.message();
                    }

                    addIds.append("https://lichess.org/tournament/").append(res.get().id())
                            .append("\n");
                    DojoScoreboard
                            .createTournament("https://lichess.org/tournament/" + res.get().id());

                    if (res.isPresent()) {

                        DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                                res.get().id());
                        Document document = new Document("Name", entry.getTournamentName())
                                .append("Id", entry.getLichessTournamentId());
                        this.tournamentCollection.insertOne(document);

                    }else{
                        return res.toString();
                    }

            }


            return addIds.toString();

        }else{
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < splitFENs(this.fen).size(); d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> {
                        daysIndex = tmr.plusDays(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                    case WEEKLY_INTERVAL -> {
                        var tmr1 = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmr1.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmr1 = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmr1.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                }
                int finalD = d + 1;
                int finalD1 = d;
                ZonedDateTime finalDaysIndex = daysIndex;

                    var res = this.client.tournaments().createArena(addparam -> addparam.clock(
                                    this.getClockTime(), this.getClockIncrement())
                            .berserkable(isZerk)
                            .name(splitFENs(this.LEAGUE_NAME).get(finalD1))
                            .conditionTeam(DOJO_TEAM)
                            .minutes(duration)
                            .position(splitFENs(this.fen).get(finalD1))
                            .rated(true)
                            .conditionMaxRating(this.MaxRating)
                            .startTime(finalDaysIndex));

                if(res instanceof Fail<Arena> fail){
                    return fail.message();
                }


                addIds.append("https://lichess.org/tournament/").append(res.get().id())
                            .append("\n");
                   DojoScoreboard
                            .createTournament("https://lichess.org/tournament/" + res.get().id());

                    if (res.isPresent()) {

                        DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                                res.get().id());
                        Document document = new Document("Name", entry.getTournamentName())
                                .append("Id", entry.getLichessTournamentId());
                        this.tournamentCollection.insertOne(document);

                    }else{
                        return res.toString();
                    }

            }


            return addIds.toString();
        }

    }


    public String createArenaWithoutMaxAndWithFEN(Boolean isZerk) {

        if(this.TournamentCount != splitFENs(this.fen).size() || this.TournamentCount != splitFENs(this.LEAGUE_NAME).size() || splitFENs(this.fen).size() != splitFENs(this.LEAGUE_NAME).size()) {
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < this.TournamentCount; d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> {
                        daysIndex = tmr.plusDays(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                    case WEEKLY_INTERVAL -> {
                        var tmr1 = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmr1.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmr1 = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmr1.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                }
                int finalD = d + 1;
                ZonedDateTime finalDaysIndex = daysIndex;

                    var res = this.client.tournaments().createArena(addparam -> addparam.clock(
                                    this.getClockTime(), this.getClockIncrement())
                            .berserkable(isZerk)
                            .name(this.getName_Pattern(finalD)).description(this.LEAGUE_DES)
                            .conditionTeam(DOJO_TEAM)
                            .minutes(duration)
                            .position(splitFENs(this.fen).get(0))
                            .rated(true)
                            .startTime(finalDaysIndex));

                if(res instanceof Fail<Arena> fail){
                    return fail.message();
                }


                addIds.append("https://lichess.org/tournament/").append(res.get().id())
                            .append("\n");
                    DojoScoreboard
                           .createTournament("https://lichess.org/tournament/" + res.get().id());

                    if (res.isPresent()) {

                        DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                                res.get().id());
                        Document document = new Document("Name", entry.getTournamentName())
                                .append("Id", entry.getLichessTournamentId());
                        this.tournamentCollection.insertOne(document);

                    }else{
                        return res.toString();
                    }


            }


            return addIds.toString();

        }else{
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < splitFENs(this.fen).size(); d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> {
                        daysIndex = tmr.plusDays(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                    case WEEKLY_INTERVAL -> {
                        var tmr1 = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmr1.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmr1 = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmr1.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                }
                int finalD = d + 1;
                int finalD1 = d;
                ZonedDateTime finalDaysIndex = daysIndex;

                    var res = this.client.tournaments().createArena(addparam -> addparam.clock(
                                    this.getClockTime(), this.getClockIncrement())
                            .berserkable(isZerk)
                            .name(splitFENs(this.LEAGUE_NAME).get(finalD1)).description(this.LEAGUE_DES)
                            .conditionTeam(DOJO_TEAM)
                            .minutes(duration)
                            .position(splitFENs(this.fen).get(finalD1))
                            .rated(true)
                            .startTime(finalDaysIndex));

                if(res instanceof Fail<Arena> fail){
                    return fail.message();
                }


                System.out.println(res);

                    addIds.append("https://lichess.org/tournament/").append(res.get().id())
                            .append("\n");
                    DojoScoreboard
                            .createTournament("https://lichess.org/tournament/" + res.get().id());

                    if (res.isPresent()) {

                        DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                                res.get().id());
                        Document document = new Document("Name", entry.getTournamentName())
                                .append("Id", entry.getLichessTournamentId());
                        this.tournamentCollection.insertOne(document);

                    }else{
                        return res.toString();
                    }

            }


            return addIds.toString();
        }

    }


    public String createArenaWithoutFENAndWithMAX(Boolean isZerk){

        if(this.LEAGUE_NAME.startsWith("-")){
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < this.TournamentCount; d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> {
                        daysIndex = tmr.plusDays(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                    case WEEKLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                }

                int finalD = d + 1;
                ZonedDateTime finalDaysIndex = daysIndex;

                var res = this.client.tournaments().createArena(addparam -> addparam.clock(
                                this.getClockTime(), this.getClockIncrement())
                        .berserkable(isZerk)
                        .name(this.LEAGUE_NAME.replaceFirst("-", "")).description(this.LEAGUE_DES)
                        .conditionTeam(DOJO_TEAM)
                        .minutes(duration)
                        .conditionMaxRating(this.MaxRating)
                        .rated(true)
                        .startTime(finalDaysIndex));

                if(res instanceof Fail<Arena> fail){
                    return fail.message();
                }


                System.out.println(res);

                addIds.append("https://lichess.org/tournament/").append(res.get().id())
                        .append("\n");
                DojoScoreboard
                       .createTournament("https://lichess.org/tournament/" + res.get().id());

                if (res.isPresent()) {

                    DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                            res.get().id());
                    Document document = new Document("Name", entry.getTournamentName())
                            .append("Id", entry.getLichessTournamentId());
                    this.tournamentCollection.insertOne(document);

                }else{
                    return res.toString();
                }


            }

            return addIds.toString();
        }else{
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < this.TournamentCount; d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> {
                        daysIndex = tmr.plusDays(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                    case WEEKLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                }

                int finalD = d + 1;
                ZonedDateTime finalDaysIndex = daysIndex;

                var res = this.client.tournaments().createArena(addparam -> addparam.clock(
                                this.getClockTime(), this.getClockIncrement())
                        .berserkable(isZerk)
                        .name(this.getName_Pattern(finalD)).description(this.LEAGUE_DES)
                        .conditionTeam(DOJO_TEAM)
                        .minutes(duration)
                        .conditionMaxRating(this.MaxRating)
                        .rated(true)
                        .startTime(finalDaysIndex));

                if(res instanceof Fail<Arena> fail){
                    return fail.message();
                }


                System.out.println(res);

                addIds.append("https://lichess.org/tournament/").append(res.get().id())
                        .append("\n");
                DojoScoreboard
                        .createTournament("https://lichess.org/tournament/" + res.get().id());

                if (res.isPresent()) {

                    DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                            res.get().id());
                    Document document = new Document("Name", entry.getTournamentName())
                            .append("Id", entry.getLichessTournamentId());
                    this.tournamentCollection.insertOne(document);

                }else{
                    return res.toString();
                }


            }

            return addIds.toString();
        }
    }



    public String createArenaWithoutFENAndMAX(Boolean isZerk){

      if(this.LEAGUE_NAME.startsWith("-")){
          StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

          for (int d = 0; d < this.TournamentCount; d++) {
              ZoneId estZoneId = ZoneId.of("America/New_York");
              var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));

              var daysIndex = tmr;

              switch (this.interval){
                  case DAILY_INTERVAL -> {
                      daysIndex = tmr.plusDays(d).with(
                              LocalTime.of(this.getMilitaryClock(), 0));
                  }
                  case WEEKLY_INTERVAL -> {
                      var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                      daysIndex = tmrr.plusWeeks(d).with(
                              LocalTime.of(this.getMilitaryClock(), 0));
                  }

                  case MONTHLY_INTERVAL -> {
                      var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                      daysIndex = tmrr.plusMonths(d).with(
                              LocalTime.of(this.getMilitaryClock(), 0));
                  }
              }
              int finalD = d + 1;
              ZonedDateTime finalDaysIndex = daysIndex;

              var res = this.client.tournaments().createArena(addparam -> addparam.clock(
                              this.getClockTime(), this.getClockIncrement())
                      .berserkable(isZerk)
                      .name(this.LEAGUE_NAME.replaceFirst("-", "")).description(this.LEAGUE_DES)
                      .conditionTeam(DOJO_TEAM)
                      .minutes(duration)
                      .rated(true)
                      .startTime(finalDaysIndex));

              if(res instanceof Fail<Arena> fail){
                  return fail.message();
              }


              System.out.println(res);

              addIds.append("https://lichess.org/tournament/").append(res.get().id())
                      .append("\n");
              DojoScoreboard
                      .createTournament("https://lichess.org/tournament/" + res.get().id());

              if (res.isPresent()) {

                  DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                          res.get().id());
                  Document document = new Document("Name", entry.getTournamentName())
                          .append("Id", entry.getLichessTournamentId());
                  this.tournamentCollection.insertOne(document);

              }else{
                  return res.toString();
              }


          }

          return addIds.toString();
      }else{
          StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

          for (int d = 0; d < this.TournamentCount; d++) {
              ZoneId estZoneId = ZoneId.of("America/New_York");
              var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));

              var daysIndex = tmr;

              switch (this.interval){
                  case DAILY_INTERVAL -> {
                      daysIndex = tmr.plusDays(d).with(
                              LocalTime.of(this.getMilitaryClock(), 0));
                  }
                  case WEEKLY_INTERVAL -> {
                      var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                      daysIndex = tmrr.plusWeeks(d).with(
                              LocalTime.of(this.getMilitaryClock(), 0));
                  }

                  case MONTHLY_INTERVAL -> {
                      var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                      daysIndex = tmrr.plusMonths(d).with(
                              LocalTime.of(this.getMilitaryClock(), 0));
                  }
              }
              int finalD = d + 1;
              ZonedDateTime finalDaysIndex = daysIndex;

              var res = this.client.tournaments().createArena(addparam -> addparam.clock(
                              this.getClockTime(), this.getClockIncrement())
                      .berserkable(isZerk)
                      .name(this.getName_Pattern(finalD)).description(this.LEAGUE_DES)
                      .conditionTeam(DOJO_TEAM)
                      .minutes(duration)
                      .rated(true)
                      .startTime(finalDaysIndex));

              if(res instanceof Fail<Arena> fail){
                  return fail.message();
              }


              System.out.println(res);

              addIds.append("https://lichess.org/tournament/").append(res.get().id())
                      .append("\n");
              DojoScoreboard
                      .createTournament("https://lichess.org/tournament/" + res.get().id());

              if (res.isPresent()) {

                  DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                          res.get().id());
                  Document document = new Document("Name", entry.getTournamentName())
                          .append("Id", entry.getLichessTournamentId());
                  this.tournamentCollection.insertOne(document);

              }else{
                  return res.toString();
              }


          }

          return addIds.toString();
      }
    }


    public String createSwissWithMaxAndFENParameter(){

        if(this.TournamentCount != splitFENs(this.fen).size() || this.TournamentCount != splitFENs(this.LEAGUE_NAME).size() || splitFENs(this.fen).size() != splitFENs(this.LEAGUE_NAME).size()) {
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < this.TournamentCount; d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));

                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> {
                        daysIndex = tmr.plusDays(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                    case WEEKLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                }

                int finalD = d + 1;
                ZonedDateTime finalDaysIndex = daysIndex;

                    var res = this.client.tournaments().createSwiss(DOJO_TEAM,
                            params -> params
                                    .clock(this.getClockTime() * 60, this.getClockIncrement())
                                    .name(this.getName_Pattern(finalD)).description(
                                            this.LEAGUE_DES)
                                    .startsAt(finalDaysIndex)
                                    .nbRounds(this.Swiss_rounds)
                                    .position(splitFENs(this.fen).get(0))
                                    .rated(true)
                                    .conditionMaxRating(this.MaxRating)
                                    .roundInterval(this.Swiss_round_interval));

                if(res instanceof Fail<Swiss> fail){
                    return fail.message();
                }


                System.out.println(res);

                    addIds.append("https://lichess.org/swiss/").append(res.get().id()).append("\n");
                    DojoScoreboard.createTournament("https://lichess.org/swiss/" + res.get().id());

                    if (res.isPresent()) {

                        DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                                res.get().id());
                        Document document = new Document("Name", entry.getTournamentName())
                                .append("Id", entry.getLichessTournamentId());
                        this.tournamentCollection.insertOne(document);

                    }else{
                        return res.toString();
                    }


            }

            return addIds.toString();

        }else{

            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < this.TournamentCount; d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> {
                        daysIndex = tmr.plusDays(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                    case WEEKLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                }

                int finalD = d + 1;
                int finalD1 = d;
                ZonedDateTime finalDaysIndex = daysIndex;

                    var res = this.client.tournaments().createSwiss(DOJO_TEAM,
                            params -> params
                                    .clock(this.getClockTime() * 60, this.getClockIncrement())
                                    .name(splitFENs(this.LEAGUE_NAME).get(finalD1)).description(
                                            this.LEAGUE_DES)
                                    .startsAt(finalDaysIndex)
                                    .nbRounds(this.Swiss_rounds)
                                    .position(splitFENs(this.fen).get(finalD1))
                                    .rated(true)
                                    .conditionMaxRating(this.MaxRating)
                                    .roundInterval(this.Swiss_round_interval));

                if(res instanceof Fail<Swiss> fail){
                    return fail.message();
                }


                System.out.println(res);

                    addIds.append("https://lichess.org/swiss/").append(res.get().id()).append("\n");
                    DojoScoreboard.createTournament("https://lichess.org/swiss/" + res.get().id());

                    if (res.isPresent()) {

                        DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                                res.get().id());
                        Document document = new Document("Name", entry.getTournamentName())
                                .append("Id", entry.getLichessTournamentId());
                        this.tournamentCollection.insertOne(document);

                    }else{
                        return res.toString();
                    }


            }

            return addIds.toString();

        }
    }



    public String createSwissWithFENWithoutMax(){
        if(this.TournamentCount != splitFENs(this.fen).size() || this.TournamentCount != splitFENs(this.LEAGUE_NAME).size() || splitFENs(this.fen).size() != splitFENs(this.LEAGUE_NAME).size()) {
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < this.TournamentCount; d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> {
                        daysIndex = tmr.plusDays(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                    case WEEKLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                }

                int finalD = d + 1;
                ZonedDateTime finalDaysIndex = daysIndex;

                    var res = this.client.tournaments().createSwiss(DOJO_TEAM,
                            params -> params
                                    .clock(this.getClockTime() * 60, this.getClockIncrement())
                                    .name(this.getName_Pattern(finalD)).description(
                                            this.LEAGUE_DES)
                                    .startsAt(finalDaysIndex)
                                    .nbRounds(this.Swiss_rounds)
                                    .position(splitFENs(this.fen).get(0))
                                    .rated(true)
                                    .roundInterval(this.Swiss_round_interval));

                if(res instanceof Fail<Swiss> fail){
                    return fail.message();
                }


                System.out.println(res);

                    addIds.append("https://lichess.org/swiss/").append(res.get().id()).append("\n");
                    DojoScoreboard.createTournament("https://lichess.org/swiss/" + res.get().id());

                    if (res.isPresent()) {

                        DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                                res.get().id());
                        Document document = new Document("Name", entry.getTournamentName())
                                .append("Id", entry.getLichessTournamentId());
                        this.tournamentCollection.insertOne(document);

                    }else{
                        return res.toString();
                    }

            }

            return addIds.toString();

        }else{

            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < this.TournamentCount; d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> {
                        daysIndex = tmr.plusDays(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                    case WEEKLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }
                }

                int finalD = d + 1;
                int finalD1 = d;
                ZonedDateTime finalDaysIndex = daysIndex;

                    var res = this.client.tournaments().createSwiss(DOJO_TEAM,
                            params -> params
                                    .clock(this.getClockTime() * 60, this.getClockIncrement())
                                    .name(splitFENs(this.LEAGUE_NAME).get(finalD1)).description(
                                            this.LEAGUE_DES)
                                    .startsAt(finalDaysIndex)
                                    .nbRounds(this.Swiss_rounds)
                                    .position(splitFENs(this.fen).get(finalD1))
                                    .rated(true)
                                    .roundInterval(this.Swiss_round_interval));

                if(res instanceof Fail<Swiss> fail){
                    return fail.message();
                }


                System.out.println(res);

                    addIds.append("https://lichess.org/swiss/").append(res.get().id()).append("\n");
                    DojoScoreboard.createTournament("https://lichess.org/swiss/" + res.get().id());

                    if (res.isPresent()) {

                        DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                                res.get().id());
                        Document document = new Document("Name", entry.getTournamentName())
                                .append("Id", entry.getLichessTournamentId());
                        this.tournamentCollection.insertOne(document);

                    }else{
                        return res.toString();
                    }

            }

            return addIds.toString();

        }

    }


    public String createSwissWithMAXWithoutFEN(){
       if(this.LEAGUE_NAME.startsWith("-")){
           StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

           for (int d = 0; d < this.TournamentCount; d++) {
               ZoneId estZoneId = ZoneId.of("America/New_York");
               var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
               var daysIndex = tmr;

               switch (this.interval){
                   case DAILY_INTERVAL -> {
                       daysIndex = tmr.plusDays(d).with(
                               LocalTime.of(this.getMilitaryClock(), 0));
                   }
                   case WEEKLY_INTERVAL -> {
                       var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                       daysIndex = tmrr.plusWeeks(d).with(
                               LocalTime.of(this.getMilitaryClock(), 0));
                   }

                   case MONTHLY_INTERVAL -> {
                       var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                       daysIndex = tmrr.plusMonths(d).with(
                               LocalTime.of(this.getMilitaryClock(), 0));
                   }
               }

               int finalD = d + 1;
               ZonedDateTime finalDaysIndex = daysIndex;

               var res = this.client.tournaments().createSwiss(DOJO_TEAM,
                       params -> params
                               .clock(this.getClockTime() * 60, this.getClockIncrement())
                               .name(this.LEAGUE_NAME.replaceFirst("-", "")).description(
                                       this.LEAGUE_DES)
                               .startsAt(finalDaysIndex)
                               .nbRounds(this.Swiss_rounds)
                               .rated(true)
                               .conditionMaxRating(this.MaxRating)
                               .roundInterval(this.Swiss_round_interval));

               if(res instanceof Fail<Swiss> fail){
                   return fail.message();
               }


               System.out.println(res);

               addIds.append("https://lichess.org/swiss/").append(res.get().id()).append("\n");
               DojoScoreboard.createTournament("https://lichess.org/swiss/" + res.get().id());

               if (res.isPresent()) {

                   DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                           res.get().id());
                   Document document = new Document("Name", entry.getTournamentName())
                           .append("Id", entry.getLichessTournamentId());
                   this.tournamentCollection.insertOne(document);

               }else{
                   return res.toString();
               }


           }
           return addIds.toString();
       }else {
           StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

           for (int d = 0; d < this.TournamentCount; d++) {
               ZoneId estZoneId = ZoneId.of("America/New_York");
               var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
               var daysIndex = tmr;

               switch (this.interval){
                   case DAILY_INTERVAL -> {
                       daysIndex = tmr.plusDays(d).with(
                               LocalTime.of(this.getMilitaryClock(), 0));
                   }
                   case WEEKLY_INTERVAL -> {
                       var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                       daysIndex = tmrr.plusWeeks(d).with(
                               LocalTime.of(this.getMilitaryClock(), 0));
                   }

                   case MONTHLY_INTERVAL -> {
                       var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                       daysIndex = tmrr.plusMonths(d).with(
                               LocalTime.of(this.getMilitaryClock(), 0));
                   }
               }

               int finalD = d + 1;
               ZonedDateTime finalDaysIndex = daysIndex;

               var res = this.client.tournaments().createSwiss(DOJO_TEAM,
                       params -> params
                               .clock(this.getClockTime() * 60, this.getClockIncrement())
                               .name(this.getName_Pattern(finalD)).description(
                                       this.LEAGUE_DES)
                               .startsAt(finalDaysIndex)
                               .nbRounds(this.Swiss_rounds)
                               .rated(true)
                               .conditionMaxRating(this.MaxRating)
                               .roundInterval(this.Swiss_round_interval));

               if(res instanceof Fail<Swiss> fail){
                   return fail.message();
               }


               System.out.println(res);

               addIds.append("https://lichess.org/swiss/").append(res.get().id()).append("\n");
               DojoScoreboard.createTournament("https://lichess.org/swiss/" + res.get().id());

               if (res.isPresent()) {

                   DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                           res.get().id());
                   Document document = new Document("Name", entry.getTournamentName())
                           .append("Id", entry.getLichessTournamentId());
                   this.tournamentCollection.insertOne(document);

               }else{
                   return res.toString();
               }


           }
           return addIds.toString();
       }
    }


    public String createSwissWithoutMaxAndFEN(){
        if(this.LEAGUE_NAME.startsWith("-")){
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < this.TournamentCount; d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> daysIndex = tmr.plusDays(d).with(
                            LocalTime.of(this.getMilitaryClock(), 0));
                    case WEEKLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));

                    }
                }

                int finalD = d + 1;
                ZonedDateTime finalDaysIndex = daysIndex;

                var res = this.client.tournaments().createSwiss(DOJO_TEAM,
                        params -> params
                                .clock(this.getClockTime() * 60, this.getClockIncrement())
                                .name(this.LEAGUE_NAME.replaceFirst("-","")).description(
                                        this.LEAGUE_DES)
                                .startsAt(finalDaysIndex)
                                .nbRounds(this.Swiss_rounds)
                                .rated(true)
                                .roundInterval(this.Swiss_round_interval));

                if(res instanceof Fail<Swiss> fail){
                    return fail.message();
                }


                System.out.println(res);

                addIds.append("https://lichess.org/swiss/").append(res.get().id()).append("\n");
                DojoScoreboard.createTournament("https://lichess.org/swiss/" + res.get().id());

                if (res.isPresent()) {

                    DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                            res.get().id());
                    Document document = new Document("Name", entry.getTournamentName())
                            .append("Id", entry.getLichessTournamentId());
                    this.tournamentCollection.insertOne(document);
                }else{
                    return res.toString();
                }


            }
            return addIds.toString();
        }else{
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < this.TournamentCount; d++) {
                ZoneId estZoneId = ZoneId.of("America/New_York");
                var tmr =  ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                var daysIndex = tmr;

                switch (this.interval){
                    case DAILY_INTERVAL -> daysIndex = tmr.plusDays(d).with(
                            LocalTime.of(this.getMilitaryClock(), 0));
                    case WEEKLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusWeeks(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));
                    }

                    case MONTHLY_INTERVAL -> {
                        var tmrr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(this.dayOfWeekOrMonth));
                        daysIndex = tmrr.plusMonths(d).with(
                                LocalTime.of(this.getMilitaryClock(), 0));

                    }
                }

                int finalD = d + 1;
                ZonedDateTime finalDaysIndex = daysIndex;

                var res = this.client.tournaments().createSwiss(DOJO_TEAM,
                        params -> params
                                .clock(this.getClockTime() * 60, this.getClockIncrement())
                                .name(this.getName_Pattern(finalD)).description(
                                        this.LEAGUE_DES)
                                .startsAt(finalDaysIndex)
                                .nbRounds(this.Swiss_rounds)
                                .rated(true)
                                .roundInterval(this.Swiss_round_interval));

                if(res instanceof Fail<Swiss> fail){
                    return fail.message();
                }


                System.out.println(res);

                addIds.append("https://lichess.org/swiss/").append(res.get().id()).append("\n");
                DojoScoreboard.createTournament("https://lichess.org/swiss/" + res.get().id());

                if (res.isPresent()) {

                    DbTournamentEntry entry = new DbTournamentEntry(this.getName_Pattern(d + 1),
                            res.get().id());
                    Document document = new Document("Name", entry.getTournamentName())
                            .append("Id", entry.getLichessTournamentId());
                    this.tournamentCollection.insertOne(document);
                }else{
                    return res.toString();
                }


            }
            return addIds.toString();
        }
    }

}