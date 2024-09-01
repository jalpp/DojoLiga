package dojo.bot.Controller.RoundRobin;

/**
 * The enum Platform.
 */
public enum Platform {


    /**
     * Lichess platform.
     */
    LICHESS,
    /**
     * Chesscom platform.
     */
    CHESSCOM,
    /**
     * Discord platform.
     */
    DISCORD;


    /**
     * From url platform.
     *
     * @param url the url
     * @return the platform
     */
    public static Platform fromURL(String url){
        if(url.contains("https://lichess.org/")){
            return LICHESS;
        }else if(url.contains("https://www.chess.com/game/live/")){
            return CHESSCOM;
        }

        return null;
    }






}
