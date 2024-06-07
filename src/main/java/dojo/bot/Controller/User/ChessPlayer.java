package dojo.bot.Controller.User;

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


    public ChessPlayer(String name, String discordId){
        this.DiscordId = discordId;
        this.chesscomname = name;
    }


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


    public ChessPlayer(String username, int rating, int score){
        this.username = username;
        this.rating = rating;
        this.score = score;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

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


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public int getBLITZ_SCORE() {
        return BLITZ_SCORE;
    }


    public int getRAPID_SCORE() {
        return RAPID_SCORE;
    }


    public int getCLASSICAL_SCORE() {
        return CLASSICAL_SCORE;
    }


    public String getDiscordId() {
        return DiscordId;
    }


    public String getLichessname() {
        return Lichessname;
    }


    public String getChesscomname() {
        return chesscomname;
    }


}
