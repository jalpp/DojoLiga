package dojo.bot.Model;

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

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getLichessTournamentId() {
        return LichessTournamentId;
    }

    public void setLichessTournamentId(String lichessTournamentId) {
        LichessTournamentId = lichessTournamentId;
    }
}