package dojo.bot.Controller.League;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.time.DayOfWeek;
import java.util.Arrays;

/**
 * The type Swiss league.
 */
public class SwissLeague extends League implements TournamentManager {


    private final SwissLeagueManager swissLeagueManager = new SwissLeagueManager();

    /**
     * Instantiates a new Swiss league.
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
    public SwissLeague(String LEAGUE_NAME, String LEAGUE_DES, int tournamentCount, Type leagueType, Interval interval, MongoCollection<Document> registry, int time_zone_start, int clockTime, int clockIncrement, int duration, String fen, int maxRating, DayOfWeek dayOfWeek) {
        super(LEAGUE_NAME, LEAGUE_DES, tournamentCount, leagueType, interval, registry, time_zone_start, clockTime, clockIncrement, duration, fen, maxRating, dayOfWeek);
    }

    /**
     * Instantiates a new Swiss league.
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
    public SwissLeague(String LEAGUE_NAME, String LEAGUE_DES, int tournamentCount, Type leagueType, Interval interval, MongoCollection<Document> registry, int time_zone_start, int clockTime, int clockIncrement, int swiss_rounds, int swiss_round_interval, String fen, int maxRating, DayOfWeek dayOfWeek) {
        super(LEAGUE_NAME, LEAGUE_DES, tournamentCount, leagueType, interval, registry, time_zone_start, clockTime, clockIncrement, swiss_rounds, swiss_round_interval, fen, maxRating, dayOfWeek);
    }


    /**
     *
     * Creates a swoss league
     *
     * @return
     */

    @Override
    public String createTournament() {

        int[] timesInMinutes = {
                1, 2, 3, 4, 5, 6, 7, 8, 10, 15, 20, 25, 30, 40,
                50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180
        };

        int[] maxRating = {2200, 2100, 2000, 1900, 1800, 1700, 1600, 1500, 1400, 1300, 1200, 1100, 1000, 900, 800, 0};

        if (Arrays.stream(maxRating).noneMatch(n -> n == getMaxRating())) {
            return "Invalid Player Max rating! please provide proper max rating value, max rating value must be \n " + Arrays.toString(maxRating);
        }

        if (Arrays.stream(timesInMinutes).noneMatch(n -> n == getClockTime())) {
            return "Invalid Clock Time! Please provide proper clock time, clock must be \n " + Arrays.toString(timesInMinutes);
        }

        if ((getClockIncrement() < 0 || getClockIncrement() > 120)) {
            return "Invalid Clock Increment! Clock increment must be within [0, 120]";
        }

        if (getTournamentCount() <= 0 || getTournamentCount() >= 13) {
            return "Invalid Request, Swiss tournament count must be 1 to 12 tournament count (1 to 12 days)";
        }


        if (!this.getFen()
                .equalsIgnoreCase("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")) {
            if (this.getMaxRating() != 0) {
                return createSwissWithMaxAndFENParameter();
            } else {
                return createSwissWithFENWithoutMax();
            }

        } else {
            if (getMaxRating() != 0) {
                return createSwissWithMAXWithoutFEN();
            } else {
                return createSwissWithoutMaxAndFEN();
            }

        }


    }


    /**
     * Create swiss with max and fen parameter string.
     *
     * @return the string
     */
    public String createSwissWithMaxAndFENParameter() {

        if (getTournamentCount() != splitFENs(getFen()).size() || getTournamentCount() != splitFENs(getLEAGUE_NAME()).size() || splitFENs(getFen()).size() != splitFENs(getLEAGUE_NAME()).size()) {
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < getTournamentCount(); d++) {
                int finalD = d + 1;

                swissLeagueManager.manageSwissLeagueCreation(
                        getMaxRating(), getDOJO_TEAM(), getName_Pattern(finalD),
                        getName_Pattern(d + 1), splitFENs(getFen()).getFirst(),
                        true, getSwiss_round_interval(), getLeagueSpanTime(d),
                        getClockTime(), getClockIncrement(), getLEAGUE_DES(),
                        getTournamentCollection(), getSwiss_rounds(), addIds
                );

            }

            return addIds.toString();

        } else {

            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < getTournamentCount(); d++) {

                int finalD1 = d;

                swissLeagueManager.manageSwissLeagueCreation(
                        getMaxRating(), getDOJO_TEAM(), splitFENs(getLEAGUE_NAME()).get(finalD1),
                        getName_Pattern(d + 1), splitFENs(getFen()).get(finalD1),
                        true, getSwiss_round_interval(), getLeagueSpanTime(d),
                        getClockTime(), getClockIncrement(), getLEAGUE_DES(),
                        getTournamentCollection(), getSwiss_rounds(), addIds
                );

            }

            return addIds.toString();

        }
    }


    /**
     * Create swiss with fen without max string.
     *
     * @return the string
     */
    public String createSwissWithFENWithoutMax() {
        if (getTournamentCount() != splitFENs(getFen()).size() || getTournamentCount() != splitFENs(getLEAGUE_NAME()).size() || splitFENs(getFen()).size() != splitFENs(getLEAGUE_NAME()).size()) {
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < getTournamentCount(); d++) {

                int finalD = d + 1;

                swissLeagueManager.manageSwissLeagueCreation(
                        null, getDOJO_TEAM(), getName_Pattern(finalD),
                        getName_Pattern(d + 1), splitFENs(getFen()).getFirst(),
                        true, getSwiss_round_interval(), getLeagueSpanTime(d),
                        getClockTime(), getClockIncrement(), getLEAGUE_DES(),
                        getTournamentCollection(), getSwiss_rounds(), addIds
                );

            }

            return addIds.toString();

        } else {

            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < getTournamentCount(); d++) {

                int finalD1 = d;


                swissLeagueManager.manageSwissLeagueCreation(
                        null, getDOJO_TEAM(), splitFENs(getLEAGUE_NAME()).get(finalD1),
                        getName_Pattern(d + 1), splitFENs(getFen()).get(finalD1),
                        true, getSwiss_round_interval(), getLeagueSpanTime(d),
                        getClockTime(), getClockIncrement(), getLEAGUE_DES(),
                        getTournamentCollection(), getSwiss_rounds(), addIds
                );

            }

            return addIds.toString();

        }

    }


    /**
     * Create swiss with max without fen string.
     *
     * @return the string
     */
    public String createSwissWithMAXWithoutFEN() {
        StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");
        if (getLEAGUE_NAME().startsWith("-")) {

            for (int d = 0; d < getTournamentCount(); d++) {

                swissLeagueManager.manageSwissLeagueCreation(
                        getMaxRating(), getDOJO_TEAM(), getLEAGUE_NAME().replaceFirst("-", ""),
                        getName_Pattern(d + 1), null,
                        true, getSwiss_round_interval(), getLeagueSpanTime(d),
                        getClockTime(), getClockIncrement(), getLEAGUE_DES(),
                        getTournamentCollection(), getSwiss_rounds(), addIds
                );

            }
        } else {

            for (int d = 0; d < getTournamentCount(); d++) {
                int finalD = d + 1;

                swissLeagueManager.manageSwissLeagueCreation(
                        getMaxRating(), getDOJO_TEAM(), this.getName_Pattern(finalD),
                        getName_Pattern(d + 1), null,
                        true, getSwiss_round_interval(), getLeagueSpanTime(d),
                        getClockTime(), getClockIncrement(), getLEAGUE_DES(),
                        getTournamentCollection(), getSwiss_rounds(), addIds
                );

            }
        }
        return addIds.toString();
    }


    /**
     * Create swiss without max and fen string.
     *
     * @return the string
     */
    public String createSwissWithoutMaxAndFEN() {
        StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");
        if (getLEAGUE_NAME().startsWith("-")) {

            for (int d = 0; d < getTournamentCount(); d++) {

                swissLeagueManager.manageSwissLeagueCreation(
                        null, getDOJO_TEAM(), getLEAGUE_NAME().replaceFirst("-", ""),
                        getName_Pattern(d + 1), null,
                        true, getSwiss_round_interval(), getLeagueSpanTime(d),
                        getClockTime(), getClockIncrement(), getLEAGUE_DES(),
                        getTournamentCollection(), getSwiss_rounds(), addIds
                );

            }
        } else {

            for (int d = 0; d < getTournamentCount(); d++) {

                int finalD = d + 1;


                swissLeagueManager.manageSwissLeagueCreation(
                        null, getDOJO_TEAM(), this.getName_Pattern(finalD),
                        getName_Pattern(d + 1), null,
                        true, getSwiss_round_interval(), getLeagueSpanTime(d),
                        getClockTime(), getClockIncrement(), getLEAGUE_DES(),
                        getTournamentCollection(), getSwiss_rounds(), addIds
                );


            }
        }
        return addIds.toString();
    }


}







