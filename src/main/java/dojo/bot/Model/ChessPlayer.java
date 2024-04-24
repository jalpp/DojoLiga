package dojo.bot.Model;

public class ChessPlayer {

    private String Lichessname;

    private String chesscomname;
    private String DiscordId;

    private int rating;

    private int score;

    private String username;


    // Blitz, classical, rapid total arena points
    private int BLITZ_SCORE;

    private int RAPID_SCORE;

    private int CLASSICAL_SCORE;

    //GP for blitz, rapid, classical arena points
    private int BLITZ_SCORE_GP_ARENA;

    private int RAPID_SCORE_GP_ARENA;

    private int CLASSICAL_SCORE_GP_ARENA;


    // Blitz, rapid, classical swiss total points

    private int BLITZ_SWISS_SCORE;

    private int RAPID_SWISS_SCORE;

    private int CLASSICAL_SWISS_SCORE;

    // GP swiss for blitz, rapid, classical points

    private int BLITZ_SCORE_GP_SWISS;


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

    public void setScore(int score) {
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getBLITZ_SCORE_GP_ARENA() {
        return BLITZ_SCORE_GP_ARENA;
    }

    public void setBLITZ_SCORE_GP_ARENA(int BLITZ_SCORE_GP_ARENA) {
        this.BLITZ_SCORE_GP_ARENA = BLITZ_SCORE_GP_ARENA;
    }

    public int getRAPID_SCORE_GP_ARENA() {
        return RAPID_SCORE_GP_ARENA;
    }

    public void setRAPID_SCORE_GP_ARENA(int RAPID_SCORE_GP_ARENA) {
        this.RAPID_SCORE_GP_ARENA = RAPID_SCORE_GP_ARENA;
    }

    public int getCLASSICAL_SCORE_GP_ARENA() {
        return CLASSICAL_SCORE_GP_ARENA;
    }

    public void setCLASSICAL_SCORE_GP_ARENA(int CLASSICAL_SCORE_GP_ARENA) {
        this.CLASSICAL_SCORE_GP_ARENA = CLASSICAL_SCORE_GP_ARENA;
    }

    public int getBLITZ_SWISS_SCORE() {
        return BLITZ_SWISS_SCORE;
    }

    public void setBLITZ_SWISS_SCORE(int BLITZ_SWISS_SCORE) {
        this.BLITZ_SWISS_SCORE = BLITZ_SWISS_SCORE;
    }

    public int getRAPID_SWISS_SCORE() {
        return RAPID_SWISS_SCORE;
    }

    public void setRAPID_SWISS_SCORE(int RAPID_SWISS_SCORE) {
        this.RAPID_SWISS_SCORE = RAPID_SWISS_SCORE;
    }

    public int getCLASSICAL_SWISS_SCORE() {
        return CLASSICAL_SWISS_SCORE;
    }

    public void setCLASSICAL_SWISS_SCORE(int CLASSICAL_SWISS_SCORE) {
        this.CLASSICAL_SWISS_SCORE = CLASSICAL_SWISS_SCORE;
    }

    public int getBLITZ_SCORE_GP_SWISS() {
        return BLITZ_SCORE_GP_SWISS;
    }

    public void setBLITZ_SCORE_GP_SWISS(int BLITZ_SCORE_GP_SWISS) {
        this.BLITZ_SCORE_GP_SWISS = BLITZ_SCORE_GP_SWISS;
    }

    public int getRAPID_SCORE_GP_SWISS() {
        return RAPID_SCORE_GP_SWISS;
    }

    public void setRAPID_SCORE_GP_SWISS(int RAPID_SCORE_GP_SWISS) {
        this.RAPID_SCORE_GP_SWISS = RAPID_SCORE_GP_SWISS;
    }

    public int getCLASSICAL_SCORE_GP_SWISS() {
        return CLASSICAL_SCORE_GP_SWISS;
    }

    public void setCLASSICAL_SCORE_GP_SWISS(int CLASSICAL_SCORE_GP_SWISS) {
        this.CLASSICAL_SCORE_GP_SWISS = CLASSICAL_SCORE_GP_SWISS;
    }

    private int RAPID_SCORE_GP_SWISS;

    private int CLASSICAL_SCORE_GP_SWISS;



    public int getBLITZ_SCORE() {
        return BLITZ_SCORE;
    }

    public void setBLITZ_SCORE(int BLITZ_SCORE) {
        this.BLITZ_SCORE = BLITZ_SCORE;
    }

    public int getRAPID_SCORE() {
        return RAPID_SCORE;
    }

    public void setRAPID_SCORE(int RAPID_SCORE) {
        this.RAPID_SCORE = RAPID_SCORE;
    }

    public int getCLASSICAL_SCORE() {
        return CLASSICAL_SCORE;
    }

    public void setCLASSICAL_SCORE(int CLASSICAL_SCORE) {
        this.CLASSICAL_SCORE = CLASSICAL_SCORE;

    }





    public String getDiscordId() {
        return DiscordId;
    }

    public void setDiscordId(String discordId) {
        DiscordId = discordId;
    }

    public String getLichessname() {
        return Lichessname;
    }

    public void setLichessname(String lichessname) {
        Lichessname = lichessname;
    }

   // get combined arena and swiss total scores

    public int getCombinedBlitzTotalScore(){
        return BLITZ_SCORE + BLITZ_SWISS_SCORE;
    }


    public int getCombinedClassicalTotalScore(){
        return CLASSICAL_SCORE + CLASSICAL_SWISS_SCORE;
    }


    public int getCombinedRapidTotalScore(){
        return RAPID_SCORE + RAPID_SWISS_SCORE;
    }

    // get combined arena GP and swiss GP scores


    public int getCombinedBlitzGPTotalScore(){
        return BLITZ_SCORE_GP_ARENA + BLITZ_SCORE_GP_SWISS;
    }


    public int getCombinedRapidGPTotalScore(){
        return RAPID_SCORE_GP_ARENA + RAPID_SCORE_GP_SWISS;
    }


    public int getCombinedClassicalGPTotalScore(){
        return CLASSICAL_SCORE_GP_ARENA + CLASSICAL_SCORE_GP_SWISS;
    }


    public String getChesscomname() {
        return chesscomname;
    }

    public void setChesscomname(String chesscomname) {
        this.chesscomname = chesscomname;
    }
}
