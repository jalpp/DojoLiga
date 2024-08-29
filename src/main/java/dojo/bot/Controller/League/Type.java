package dojo.bot.Controller.League;

/**
 * The enum Type.
 */
public enum Type {

    /**
     * Swiss type.
     */
    SWISS,
    /**
     * Arena type.
     */
    ARENA,
    /**
     * Swiss grand prix type.
     */
    SWISS_GRAND_PRIX,
    /**
     * Arena grand prix type.
     */
    ARENA_GRAND_PRIX,
    /**
     * Comb grand prix type.
     */
    COMB_GRAND_PRIX,

    /**
     * Sparring type.
     */
    SPARRING,

    /**
     * Sparring endgame type.
     */
    SPARRING_ENDGAME;


    /**
     * Returns the String value
     *
     * @return string
     */
    public String fetchLeaderboard(){
        switch (this){
            case ARENA -> { return "ARENA";}
            case SWISS -> {return "SWISS";}
            case COMB_GRAND_PRIX -> {return "GRAND_PRIX";}
        }

        return null;
    }
    @Override
    public String toString(){
        switch (this){
            case ARENA -> {
                return "_score";
            }
            case SWISS -> {
                return "_score_swiss";
            }

            case ARENA_GRAND_PRIX -> {
                return "_score_gp";
            }

            case SWISS_GRAND_PRIX -> {
                return "_score_swiss_gp";
            }

            case COMB_GRAND_PRIX -> {
                return "_comb_total_gp";
            }

            case SPARRING -> {
                return "sp_score";
            }

            case SPARRING_ENDGAME -> {
                return "eg_score";
            }
        }


        return "unknown";
    }


    /**
     * Get name string.
     *
     * @return the string
     */
    public String getName(){
        switch (this){
            case ARENA -> {
                return "Arena Total";
            }

            case SWISS -> {
                return "Swiss Total";
            }

            case COMB_GRAND_PRIX -> {
                return "Grand Prix";
            }

            case SPARRING -> {
                return "Middlegame Sparring Total";
            }

            case SPARRING_ENDGAME -> {
                return "Endgame Sparring Total";
            }
        }

        return "Error";
    }



}
