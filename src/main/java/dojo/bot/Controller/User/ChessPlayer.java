package dojo.bot.Controller.User;

/**
 * The type Chess player.
 */
public class ChessPlayer {

    private String Lichessname;

    private String chesscomname;
    private String DiscordId;

    private int rating;

    private int score;

    private String username;

    private int BLITZ_SCORE;

    private int RAPID_SCORE;

    private int CLASSICAL_SCORE;


    /**
     * Instantiates a new Chess player.
     *
     * @param name      the name
     * @param discordId the discord id
     */
    public ChessPlayer(String name, String discordId){
        this.DiscordId = discordId;
        this.chesscomname = name;
    }


    /**
     * Instantiates a new Chess player.
     *
     * @param lichessname                         the lichessname
     * @param discordId                           the discord id
     * @param blitz                               the blitz
     * @param rapid                               the rapid
     * @param classical                           the classical
     * @param blitz_arena_gp                      the blitz arena gp
     * @param rapid_arena_gp                      the rapid arena gp
     * @param classical_arena_gp                  the classical arena gp
     * @param blitz_swiss                         the blitz swiss
     * @param rapid_swiss                         the rapid swiss
     * @param classical_swiss                     the classical swiss
     * @param blitz_swiss_gp                      the blitz swiss gp
     * @param classical_swiss_gp                  the classical swiss gp
     * @param rapid_swiss_gp                      the rapid swiss gp
     * @param comb_blitz_arena_swiss_score        the comb blitz arena swiss score
     * @param comb_rapid_arena_swiss_score        the comb rapid arena swiss score
     * @param comb_classical_arena_swiss_score    the comb classical arena swiss score
     * @param comb_blitz_arena_swiss_gp_score     the comb blitz arena swiss gp score
     * @param comb_rapid_arena_swiss_gp_score     the comb rapid arena swiss gp score
     * @param comb_classical_arena_swiss_gp_score the comb classical arena swiss gp score
     * @param blitz_rating                        the blitz rating
     * @param rapid_rating                        the rapid rating
     * @param classical_rating                    the classical rating
     */
    public ChessPlayer(String lichessname, String discordId, int blitz, int rapid, int classical,
    int blitz_arena_gp, int rapid_arena_gp, int classical_arena_gp, int blitz_swiss, int rapid_swiss, int classical_swiss
    , int blitz_swiss_gp, int classical_swiss_gp, int rapid_swiss_gp, int comb_blitz_arena_swiss_score, int comb_rapid_arena_swiss_score, int
                       comb_classical_arena_swiss_score, int comb_blitz_arena_swiss_gp_score, int comb_rapid_arena_swiss_gp_score, int comb_classical_arena_swiss_gp_score,
                       int blitz_rating, int rapid_rating, int classical_rating){
        this.Lichessname = lichessname;
        this.DiscordId = discordId;
        this.BLITZ_SCORE = blitz;
        this.RAPID_SCORE = rapid;
        this.CLASSICAL_SCORE = classical;
    }


    /**
     * Instantiates a new Chess player.
     *
     * @param username the username
     * @param rating   the rating
     * @param score    the score
     */
    public ChessPlayer(String username, int rating, int score){
        this.username = username;
        this.rating = rating;
        this.score = score;
    }

    /**
     * Gets rating.
     *
     * @return the rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets rating.
     *
     * @param rating the rating
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Gets score.
     *
     * @return the score
     */
    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "ChessPlayer{" +
                "rating=" + rating +
                ", score=" + score +
                ", username='" + username + '\'' +
                '}';
    }


    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * Gets blitz score.
     *
     * @return the blitz score
     */
    public int getBLITZ_SCORE() {
        return BLITZ_SCORE;
    }


    /**
     * Gets rapid score.
     *
     * @return the rapid score
     */
    public int getRAPID_SCORE() {
        return RAPID_SCORE;
    }


    /**
     * Gets classical score.
     *
     * @return the classical score
     */
    public int getCLASSICAL_SCORE() {
        return CLASSICAL_SCORE;
    }


    /**
     * Gets discord id.
     *
     * @return the discord id
     */
    public String getDiscordId() {
        return DiscordId;
    }


    /**
     * Gets lichessname.
     *
     * @return the lichessname
     */
    public String getLichessname() {
        return Lichessname;
    }


    /**
     * Gets chesscomname.
     *
     * @return the chesscomname
     */
    public String getChesscomname() {
        return chesscomname;
    }


}
