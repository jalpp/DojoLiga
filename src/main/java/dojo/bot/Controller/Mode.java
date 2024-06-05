package dojo.bot.Controller;

/**
 * Mode enum represents Leaderboard mode Open or U1800
 */
public enum Mode {

    OPEN,
    UNDER1800;


    @Override
    public String toString() {
        return switch (this) {
            case OPEN -> "open";
            case UNDER1800 -> "under1800";
        };
    }


    public static Mode fromString(String mode) {
        return switch (mode.toLowerCase()) {
            case "open" -> OPEN;
            case "under1800" -> UNDER1800;
            default -> null;
        };
    }

}
