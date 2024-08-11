package dojo.bot.Controller.League;

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
        return switch (timeControl.toLowerCase()) {
            case "classical" -> CLASSICAL;
            case "blitz" -> BLITZ;
            case "rapid" -> RAPID;
            default -> null;
        };
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
        return switch (this) {
            case CLASSICAL -> "classical";
            case BLITZ -> "blitz";
            case RAPID -> "rapid";
            case MIX -> "sparring";
            case MIX_ENDGAME -> "eg";
        };

    }

    /**
     * returns the title of the time control
     * @return
     */
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


}