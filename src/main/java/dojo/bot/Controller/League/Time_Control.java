package dojo.bot.Controller.League;

/**
 * Enum Time control for both Chess.com and Lichess
 */
public enum Time_Control {

    CLASSICAL,
    BLITZ,
    RAPID,

    MIX,

    MIX_ENDGAME;

    /**
     * Returns a Time_Control matching the provided String or null if
     * the String does not match.
     *
     * @param timeControl The String version of the Time_Control.
     * @return A matching Time_Control object.
     */
    public static Time_Control fromString(String timeControl) {
        switch (timeControl.toLowerCase()) {
            case "classical":
                return CLASSICAL;

            case "blitz":
                return BLITZ;

            case "rapid":
                return RAPID;
        }
        return null;
    }

    /**
     * Returns the String value of time control and overrides the toString method
     * @return
     *
     * returns the String value of the time control
     *
     */

    @Override
    public String toString() {
        switch (this) {
            case CLASSICAL:
                return "classical";

            case BLITZ:
                return "blitz";

            case RAPID:
                return "rapid";

            case MIX:
                return "sparring";

            case MIX_ENDGAME:
                return "eg";
        }

        return "unknown";
    }


    public String getTitle() {
        switch (this) {
            case CLASSICAL -> {
                return "Classical";
            }

            case RAPID -> {
                return "Rapid";
            }

            case BLITZ -> {
                return "Blitz";
            }
        }

        return "unknown";
    }


    public String get_winner_field(){
        switch (this){
            case CLASSICAL, RAPID, BLITZ -> {
                return this + "_score_gp";
            }

        }

        return "error!";
    }






}