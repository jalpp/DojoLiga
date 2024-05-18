package dojo.bot.Model;


/**
 * Represents the tournament entry Document 
 */


public class DbTournamentEntry {


    private String tournamentName;
    private String LichessTournamentId;


    public DbTournamentEntry(String tournamentName, String lichessTournamentId) {
        this.tournamentName = tournamentName;
        LichessTournamentId = lichessTournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public String getLichessTournamentId() {
        return LichessTournamentId;
    }

}
