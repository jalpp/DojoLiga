package dojo.bot.Controller.League;

import chariot.Client;
import chariot.ClientAuth;
import com.mongodb.client.MongoCollection;
import dojo.bot.Runner.Main;
import org.bson.Document;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

/**
 * League represents how would a Chess league look like
 */
public class League {
    private final ClientAuth client = Client.auth(Main.botToken);

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

    private final ArenaLeagueManager leagueManager = new ArenaLeagueManager();

    private final SwissLeagueManager swissLeagueManager = new SwissLeagueManager();

    /**
     * Gets clock time.
     *
     * @return the clock time
     */
    public int getClockTime() {
        return ClockTime;
    }


    /**
     * Gets clock increment.
     *
     * @return the clock increment
     */
    public int getClockIncrement() {
        return ClockIncrement;
    }


    /**
     * Instantiates a new League.
     *
     * @param LEAGUE_NAME     the league name
     * @param LEAGUE_DES      the league des
     * @param tournamentCount the tournament count
     * @param leagueType      the league type
     * @param interval        the interval
     * @param registry        the registry
     * @param time_zone_start the time zone start
     * @param clockTime       the clock time
     * @param clockIncrement  the clock increment
     * @param duration        the duration
     * @param fen             the fen
     * @param maxRating       the max rating
     * @param dayOfWeek       the day of week
     */
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

    /**
     * Instantiates a new League.
     *
     * @param LEAGUE_NAME          the league name
     * @param LEAGUE_DES           the league des
     * @param tournamentCount      the tournament count
     * @param leagueType           the league type
     * @param interval             the interval
     * @param registry             the registry
     * @param time_zone_start      the time zone start
     * @param clockTime            the clock time
     * @param clockIncrement       the clock increment
     * @param swiss_rounds         the swiss rounds
     * @param swiss_round_interval the swiss round interval
     * @param fen                  the fen
     * @param maxRating            the max rating
     * @param dayOfWeek            the day of week
     */
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

    /**
     * Sets league time type.
     */
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

    /**
     * Gets military clock.
     *
     * @return the military clock
     */
    public int getMilitaryClock() {


        if (this.time_zone_start == 0) {
            return 0;
        }

        if (this.time_zone_start < -1 || this.time_zone_start >= 24) {
            return 13;
        }

        return this.time_zone_start;
    }


    /**
     * Gets name pattern.
     *
     * @param index the index
     * @return the name pattern
     */
    public String getName_Pattern(int index) {
        return splitFENs(this.LEAGUE_NAME).getFirst() + " No. " + index;
    }

    /**
     * Gets dojo team.
     *
     * @return the dojo team
     */
    public String getDOJO_TEAM() {
        return Main.IS_BETA ? "teamtesting" : "chessdojo";
    }

    /**
     * Gets league name.
     *
     * @return the league name
     */
    public String getLEAGUE_NAME() {
        return LEAGUE_NAME;
    }

    /**
     * Gets league des.
     *
     * @return the league des
     */
    public String getLEAGUE_DES() {
        return LEAGUE_DES;
    }

    /**
     * Gets tournament count.
     *
     * @return the tournament count
     */
    public int getTournamentCount() {
        return TournamentCount;
    }

    /**
     * Gets league time type.
     *
     * @return the league time type
     */
    public Time_Control getLEAGUE_TIME_TYPE() {
        return LEAGUE_TIME_TYPE;
    }

    /**
     * Gets tournament collection.
     *
     * @return the tournament collection
     */
    public MongoCollection<Document> getTournamentCollection() {
        return tournamentCollection;
    }

    /**
     * Gets interval.
     *
     * @return the interval
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * Gets duration.
     *
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Gets swiss rounds.
     *
     * @return the swiss rounds
     */
    public int getSwiss_rounds() {
        return Swiss_rounds;
    }

    /**
     * Gets swiss round interval.
     *
     * @return the swiss round interval
     */
    public int getSwiss_round_interval() {
        return Swiss_round_interval;
    }

    /**
     * Gets fen.
     *
     * @return the fen
     */
    public String getFen() {
        return fen;
    }

    /**
     * Gets max rating.
     *
     * @return the max rating
     */
    public int getMaxRating() {
        return MaxRating;
    }

    /**
     * Gets day of week or month.
     *
     * @return the day of week or month
     */
    public DayOfWeek getDayOfWeekOrMonth() {
        return dayOfWeekOrMonth;
    }


    /**
     * Responsible for spliting the input string with comma
     *
     * @param input String with tournament names and fens
     * @return Arraylist containing each element
     */
    public static ArrayList<String> splitFENs(String input) {
        ArrayList<String> fenList = new ArrayList<>();

        String[] fens = input.split(",");

        for (String fen : fens) {
            fenList.add(fen.trim());
        }

        return fenList;
    }

    /**
     * generates the future zonedatedtime object based on current interval
     *
     * @param d the ith loop index
     * @return future time
     */
    public ZonedDateTime getLeagueSpanTime(int d) {
        ZoneId estZoneId = ZoneId.of("America/New_York");
        var tmr = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(getDayOfWeekOrMonth()));
        var daysIndex = tmr;

        switch (getInterval()) {
            case DAILY_INTERVAL -> daysIndex = tmr.plusDays(d).with(
                    LocalTime.of(this.getMilitaryClock(), 0));
            case WEEKLY_INTERVAL -> {
                var tmr1 = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(getDayOfWeekOrMonth()));
                daysIndex = tmr1.plusWeeks(d).with(
                        LocalTime.of(this.getMilitaryClock(), 0));
            }

            case MONTHLY_INTERVAL -> {
                var tmr1 = ZonedDateTime.now(estZoneId).with(TemporalAdjusters.next(getDayOfWeekOrMonth()));
                daysIndex = tmr1.plusMonths(d).with(
                        LocalTime.of(this.getMilitaryClock(), 0));
            }
        }

        return daysIndex;
    }


}