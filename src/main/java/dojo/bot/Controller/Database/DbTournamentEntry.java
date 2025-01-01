package dojo.bot.Controller.Database;

/**
 * The type Db tournament entry.
 */
public class DbTournamentEntry {


    private String tournamentName;
    private String LichessTournamentId;


    /**
     * Instantiates a new Db tournament entry.
     *
     * @param tournamentName      the tournament name
     * @param lichessTournamentId the lichess tournament id
     */
    public DbTournamentEntry(String tournamentName, String lichessTournamentId) {
        this.tournamentName = tournamentName;
        LichessTournamentId = lichessTournamentId;
    }

    /**
     * Gets tournament name.
     *
     * @return the tournament name
     */
    public String getTournamentName() {
        return tournamentName;
    }

    /**
     * Gets lichess tournament id.
     *
     * @return the lichess tournament id
     */
    public String getLichessTournamentId() {
        return LichessTournamentId;
    }

}
