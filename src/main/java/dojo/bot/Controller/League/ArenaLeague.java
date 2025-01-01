package dojo.bot.Controller.League;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.time.DayOfWeek;
import java.util.Arrays;

public class ArenaLeague extends League implements TournamentManager {


    private final ArenaLeagueManager leagueManager = new ArenaLeagueManager();


    public ArenaLeague(String LEAGUE_NAME, String LEAGUE_DES, int tournamentCount, Type leagueType, Interval interval, MongoCollection<Document> registry, int time_zone_start, int clockTime, int clockIncrement, int duration, String fen, int maxRating, DayOfWeek dayOfWeek) {
        super(LEAGUE_NAME, LEAGUE_DES, tournamentCount, leagueType, interval, registry, time_zone_start, clockTime, clockIncrement, duration, fen, maxRating, dayOfWeek);
    }

    public ArenaLeague(String LEAGUE_NAME, String LEAGUE_DES, int tournamentCount, Type leagueType, Interval interval, MongoCollection<Document> registry, int time_zone_start, int clockTime, int clockIncrement, int swiss_rounds, int swiss_round_interval, String fen, int maxRating, DayOfWeek dayOfWeek) {
        super(LEAGUE_NAME, LEAGUE_DES, tournamentCount, leagueType, interval, registry, time_zone_start, clockTime, clockIncrement, swiss_rounds, swiss_round_interval, fen, maxRating, dayOfWeek);
    }

    /**
     * Create an arena league from input params
     *
     * @return syccess/failed arena league creation message
     */


    @Override
    public String createTournament() {

        int[] checkTime = {2, 3, 4, 5, 6, 7, 8, 10, 15, 20, 25, 30, 40, 50, 60};
        int[] checkSec = {0, 1, 2, 3, 4, 5, 6, 7, 10, 15, 20, 25, 30, 40, 50, 60};
        int[] checkDuration = {20, 25, 30, 35, 40, 45, 50, 55, 60, 70, 80, 90, 100, 110, 120, 150, 180, 210, 240, 270, 300, 330, 360, 420, 480, 540, 600, 720};
        int[] maxRating = {2200, 2100, 2000, 1900, 1800, 1700, 1600, 1500, 1400, 1300, 1200, 1100, 1000, 900, 800, 0};

        if (Arrays.stream(maxRating).noneMatch(n -> n == getMaxRating())) {
            return "Invalid Player Max rating! please provide proper max rating value, max rating value must be \n " + Arrays.toString(maxRating);
        }

        if (Arrays.stream(checkSec).noneMatch(n -> n == getClockIncrement())) {
            return "Invalid Clock Increment in secs, please provide proper increment, increment must be \n " + Arrays.toString(checkSec);
        }

        if (Arrays.stream(checkTime).noneMatch(n -> n == getClockTime())) {
            return "Invalid Clock Time in mins, please provide proper mins, mins must be \n " + Arrays.toString(checkTime);
        }

        if (Arrays.stream(checkDuration).noneMatch(n -> n == getDuration())) {
            return "Invalid Duration, Please provide proper duration, duration must be this values \n " + Arrays.toString(checkDuration);
        }

        if (getTournamentCount() <= 0 || getTournamentCount() >= 13) {
            return "Invalid Request, Tournament count must be 1 to 12 tournament count (1 to 12 days)";
        }

        if (!getFen()
                .equalsIgnoreCase("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")) {
            if (getMaxRating() != 0) {
                switch (getLEAGUE_TIME_TYPE()) {
                    case BLITZ -> {
                        return createArenaWithMaxAndFENParameter(true);
                    }
                    case RAPID, CLASSICAL -> {
                        return createArenaWithMaxAndFENParameter(false);
                    }
                }
            } else {
                switch (getLEAGUE_TIME_TYPE()) {
                    case BLITZ -> {
                        return createArenaWithoutMaxAndWithFEN(true);
                    }
                    case RAPID, CLASSICAL -> {
                        return createArenaWithoutMaxAndWithFEN(false);
                    }
                }

            }
        }

        if (getMaxRating() != 0) {
            switch (getLEAGUE_TIME_TYPE()) {
                case BLITZ -> {
                    return createArenaWithoutFENAndWithMAX(true);
                }
                case RAPID, CLASSICAL -> {
                    return createArenaWithoutFENAndWithMAX(false);
                }
            }

        } else {
            switch (getLEAGUE_TIME_TYPE()) {
                case BLITZ -> {
                    return createArenaWithoutFENAndMAX(true);
                }
                case RAPID, CLASSICAL -> {
                    return createArenaWithoutFENAndMAX(false);
                }
            }

        }


        return "Error!";
    }

    /**
     * Creates arena with max and FEN from Discord input
     *
     * @param isZerk value to indicate if players can berzerk (cut time half)
     * @return tournament URLs to tournament admin
     */


    public String createArenaWithMaxAndFENParameter(Boolean isZerk) {

        if (getTournamentCount() != splitFENs(getFen()).size() || getTournamentCount() != splitFENs(getLEAGUE_NAME()).size() || splitFENs(getFen()).size() != splitFENs(getLEAGUE_DES()).size()) {
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < getTournamentCount(); d++) {

                int finalD = d + 1;

                leagueManager.manageArenaLeagueCreation(
                        getMaxRating(),
                        getDOJO_TEAM(),
                        isZerk,
                        getName_Pattern(finalD),
                        getName_Pattern(d + 1),
                        splitFENs(getFen()).getFirst(),
                        true,
                        getDuration(),
                        getLeagueSpanTime(d),
                        getClockTime(),
                        getClockIncrement(),
                        getLEAGUE_DES(),
                        addIds,
                        getTournamentCollection()
                );

            }

            return addIds.toString();

        }


        StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

        for (int d = 0; d < splitFENs(getFen()).size(); d++) {

            leagueManager.manageArenaLeagueCreation(
                    getMaxRating(),
                    getDOJO_TEAM(),
                    isZerk,
                    splitFENs(getLEAGUE_NAME()).get(d),
                    getName_Pattern(d + 1),
                    splitFENs(getFen()).get(d),
                    true,
                    getDuration(),
                    getLeagueSpanTime(d),
                    getClockTime(),
                    getClockIncrement(),
                    getLEAGUE_DES(),
                    addIds,
                    getTournamentCollection()
            );

        }

        return addIds.toString();
    }

    /**
     * Creates arena without max and with FEN from Discord input
     *
     * @param isZerk value to indicate if players can berzerk (cut time half)
     * @return tournament URLs to tournament admin
     */

    public String createArenaWithoutMaxAndWithFEN(Boolean isZerk) {

        if (getTournamentCount() != splitFENs(getFen()).size() || getTournamentCount() != splitFENs(getLEAGUE_NAME()).size() || splitFENs(getFen()).size() != splitFENs(getLEAGUE_NAME()).size()) {
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < getTournamentCount(); d++) {

                int finalD = d + 1;

                leagueManager.manageArenaLeagueCreation(
                        null,
                        getDOJO_TEAM(),
                        isZerk,
                        getName_Pattern(finalD),
                        getName_Pattern(d + 1),
                        splitFENs(getFen()).getFirst(),
                        true,
                        getDuration(),
                        getLeagueSpanTime(d),
                        getClockTime(),
                        getClockIncrement(),
                        getLEAGUE_DES(),
                        addIds,
                        getTournamentCollection()
                );
            }
            return addIds.toString();

        } else {
            StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");

            for (int d = 0; d < splitFENs(getFen()).size(); d++) {

                leagueManager.manageArenaLeagueCreation(
                        getMaxRating(),
                        getDOJO_TEAM(),
                        isZerk,
                        splitFENs(getLEAGUE_NAME()).get(d),
                        getName_Pattern(d + 1),
                        splitFENs(getFen()).get(d),
                        true,
                        getDuration(),
                        getLeagueSpanTime(d),
                        getClockTime(),
                        getClockIncrement(),
                        getLEAGUE_DES(),
                        addIds,
                        getTournamentCollection()
                );

            }


            return addIds.toString();
        }

    }

    /**
     * Creates arena without max and FEN from Discord input
     *
     * @param isZerk value to indicate if players can berzerk (cut time half)
     * @return tournament URLs to tournament admin
     */

    public String createArenaWithoutFENAndWithMAX(Boolean isZerk) {

        StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");
        if (getLEAGUE_NAME().startsWith("-")) {

            for (int d = 0; d < getTournamentCount(); d++) {

                leagueManager.manageArenaLeagueCreation(
                        getMaxRating(),
                        getDOJO_TEAM(),
                        isZerk,
                        getLEAGUE_NAME().replaceFirst("-", ""),
                        getName_Pattern(d + 1),
                        null,
                        true,
                        getDuration(),
                        getLeagueSpanTime(d),
                        getClockTime(),
                        getClockIncrement(),
                        getLEAGUE_DES(),
                        addIds,
                        getTournamentCollection()
                );

            }

        } else {

            for (int d = 0; d < getTournamentCount(); d++) {

                int finalD = d + 1;

                leagueManager.manageArenaLeagueCreation(
                        getMaxRating(),
                        getDOJO_TEAM(),
                        isZerk,
                        this.getName_Pattern(finalD),
                        getName_Pattern(d + 1),
                        null,
                        true,
                        getDuration(),
                        getLeagueSpanTime(d),
                        getClockTime(),
                        getClockIncrement(),
                        getLEAGUE_DES(),
                        addIds,
                        getTournamentCollection()
                );
            }

        }
        return addIds.toString();
    }

    /**
     * Creates arena without max and FEN from Discord input
     *
     * @param isZerk value to indicate if players can berzerk (cut time half)
     * @return tournament URLs to tournament admin
     */

    public String createArenaWithoutFENAndMAX(Boolean isZerk) {

        StringBuilder addIds = new StringBuilder("Here are League Tournaments: \n");
        if (getLEAGUE_NAME().startsWith("-")) {

            for (int d = 0; d < getTournamentCount(); d++) {

                leagueManager.manageArenaLeagueCreation(
                        null,
                        getDOJO_TEAM(),
                        isZerk,
                        getLEAGUE_NAME().replaceFirst("-", ""),
                        getName_Pattern(d + 1),
                        null,
                        true,
                        getDuration(),
                        getLeagueSpanTime(d),
                        getClockTime(),
                        getClockIncrement(),
                        getLEAGUE_DES(),
                        addIds,
                        getTournamentCollection()
                );

            }

        } else {

            for (int d = 0; d < getTournamentCount(); d++) {

                int finalD = d + 1;

                leagueManager.manageArenaLeagueCreation(
                        null,
                        getDOJO_TEAM(),
                        isZerk,
                        this.getName_Pattern(finalD),
                        getName_Pattern(d + 1),
                        null,
                        true,
                        getDuration(),
                        getLeagueSpanTime(d),
                        getClockTime(),
                        getClockIncrement(),
                        getLEAGUE_DES(),
                        addIds,
                        getTournamentCollection()
                );

            }

        }
        return addIds.toString();
    }


}

