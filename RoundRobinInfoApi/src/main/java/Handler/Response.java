package Handler;


import java.util.ArrayList;
import java.util.List;

/**
 * The type Response.
 */
public class Response {
    private String info;
    private String message;
    private String tournamentname;
    private String desc;
    private ArrayList<ArrayList<String>> pairs;
    private List<ArrayList<String>> crosstable;
    private List<String> leaderboard;
    private List<String> players;
    private List<String> gameSub;
    private List<Double> scores;

    /**
     * Gets scores.
     *
     * @return the scores
     */
    public List<Double> getScores() {
        return scores;
    }

    /**
     * Sets scores.
     *
     * @param scores the scores
     */
    public void setScores(List<Double> scores) {
        this.scores = scores;
    }

    /**
     * Gets game sub.
     *
     * @return the game sub
     */
    public List<String> getGameSub() {
        return gameSub;
    }

    /**
     * Sets game sub.
     *
     * @param gameSub the game sub
     */
    public void setGameSub(List<String> gameSub) {
        this.gameSub = gameSub;
    }

    /**
     * Gets players.
     *
     * @return the players
     */
    public List<String> getPlayers() {
        return players;
    }

    /**
     * Sets players.
     *
     * @param players the players
     */
    public void setPlayers(List<String> players) {
        this.players = players;
    }

    private String crosstableString;
    private int statusCode;

    /**
     * Gets status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets status code.
     *
     * @param statusCode the status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets leaderboard.
     *
     * @return the leaderboard
     */
    public List<String> getLeaderboard() {
        return leaderboard;
    }

    /**
     * Sets leaderboard.
     *
     * @param leaderboard the leaderboard
     */
    public void setLeaderboard(List<String> leaderboard) {
        this.leaderboard = leaderboard;
    }

    /**
     * Gets crosstable string.
     *
     * @return the crosstable string
     */
    public String getCrosstableString() {
        return crosstableString;
    }

    /**
     * Sets crosstable string.
     *
     * @param crosstableString the crosstable string
     */
    public void setCrosstableString(String crosstableString) {
        this.crosstableString = crosstableString;
    }

    /**
     * Gets crosstable.
     *
     * @return the crosstable
     */
    public List<ArrayList<String>> getCrosstable() {
        return crosstable;
    }

    /**
     * Sets crosstable.
     *
     * @param crosstable the crosstable
     */
    public void setCrosstable(List<ArrayList<String>> crosstable) {
        this.crosstable = crosstable;
    }

    /**
     * Gets tournamentname.
     *
     * @return the tournamentname
     */
    public String getTournamentname() {
        return tournamentname;
    }

    /**
     * Sets tournamentname.
     *
     * @param tournamentname the tournamentname
     */
    public void setTournamentname(String tournamentname) {
        this.tournamentname = tournamentname;
    }

    /**
     * Sets pairs.
     *
     * @param pairs the pairs
     */
    public void setPairs(ArrayList<ArrayList<String>> pairs) {
        this.pairs = pairs;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Gets pairs.
     *
     * @return the pairs
     */
    public ArrayList<ArrayList<String>> getPairs() {
        return pairs;
    }


    /**
     * Gets info.
     *
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets info.
     *
     * @param info1 the info 1
     */
    public void setInfo(String info1) {
        this.info = info1;
    }

    @Override
    public String toString() {
        return "Response{" +
                "info='" + info + '\'' +
                ", message='" + message + '\'' +
                ", tournamentname='" + tournamentname + '\'' +
                ", desc='" + desc + '\'' +
                ", pairs=" + pairs +
                ", crosstable=" + crosstable +
                ", leaderboard='" + leaderboard + '\'' +
                ", crosstableString='" + crosstableString + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}