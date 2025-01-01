package dojo.bot.Controller.User;

import dojo.bot.Controller.League.Time_Control;
import io.github.sornerol.chess.pubapi.client.PlayerClient;
import io.github.sornerol.chess.pubapi.domain.player.stats.PlayerStats;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.IOException;

/**
 * The type Cc profile.
 */
public class CCProfile {

    private final String username;
    private EmbedBuilder embedBuilder;
    private  PlayerClient playerClient;


    /**
     * Instantiates a new Cc profile.
     *
     * @param username the username
     */
    public CCProfile(String username){
        this.username = username.toLowerCase().trim();
    }


    /**
     * Gets rapid rating.
     *
     * @return the rapid rating
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public int getRapidRating() throws ChessComPubApiException, IOException {
        this.playerClient = new PlayerClient();
        PlayerStats player = playerClient.getStatsForPlayer(this.username);

       if(player.getChessRapid() != null){
           return player.getChessRapid().getLast().getRating();
       }

       return 0;
    }

    /**
     * Gets blitz rating.
     *
     * @return the blitz rating
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public int getBlitzRating() throws ChessComPubApiException, IOException {
        this.playerClient = new PlayerClient();
        PlayerStats player = playerClient.getStatsForPlayer(this.username);

        if(player.getChessBlitz() != null){
            return player.getChessBlitz().getLast().getRating();
        }

        return 0;
    }


    /**
     * Gets rating based on time control.
     *
     * @param control the control
     * @return the rating based on time control
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public int getRatingBasedOnTimeControl(Time_Control control) throws ChessComPubApiException, IOException {
        switch (control){
            case BLITZ -> {
                return getBlitzRating();
            }

            case RAPID -> {
                return getRapidRating();
            }

        }

        return 0;
    }


    /**
     * Get cc profile embed builder.
     *
     * @return the embed builder
     */
    public EmbedBuilder getCCProfile(){


        try {
            this.playerClient = new PlayerClient();
            this.embedBuilder = new EmbedBuilder();
            String proSay = "";


            PlayerStats player = playerClient.getStatsForPlayer(this.username);

            System.out.println(player);

            String pfp = playerClient.getPlayerByUsername(this.username).getAvatarUrl();
            String bullet = (player.getChessBullet() == null) ? "0" : String.valueOf(player.getChessBullet().getLast().getRating().intValue());
            String rapid = (player.getChessRapid() == null) ? "0" : String.valueOf(player.getChessRapid().getLast().getRating().intValue());
            String blitz = (player.getChessBlitz() == null) ? "0" : String.valueOf(player.getChessBlitz().getLast().getRating().intValue());
            String rush = (player.getPuzzleRush().getBest() == null) ? "0" : String.valueOf(player.getPuzzleRush().getBest().getScore().intValue());
            String tactics = (player.getTactics().getLowest() == null) ? "0" : String.valueOf(player.getTactics().getHighest().getRating().intValue());
                proSay += " ** \uD83D\uDE85 Bullet**: " +
                          bullet +
                        "\n **\uD83D\uDC07 Rapid:**  " +
                        rapid +
                        "\n ** \uD83D\uDD25 Blitz:** " +
                        blitz +
                        "\n **\uD83E\uDDE9 Puzzle Rush:** " +
                        rush +
                        "\n **\uD83D\uDE80 Tactics Rating** " +
                        tactics
                ;


            this.embedBuilder.setThumbnail(pfp).setTitle(username + "'s Chess.com Profile").setDescription(proSay).setColor(Color.green);




        } catch (IOException e) {
            return this.embedBuilder.setDescription("error! Please provide a valid username!").setColor(Color.red);
        } catch (ChessComPubApiException e) {
            return this.embedBuilder.setDescription("Error!");
        }

        return this.embedBuilder;

    }






}
