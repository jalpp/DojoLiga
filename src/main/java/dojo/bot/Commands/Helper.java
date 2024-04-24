package dojo.bot.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.Map;

/**
 * Class to show helpful commands
 */
public class Helper {

    public static final String DOJO_LOGO = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRDEcmvHJKxeF0L1dmlpickvkGNpTWPcNSGPV_c-ZiL6T8MsIxey-61J3VkehDCIi6tN5s&usqp=CAU";

    public static final Map<String, String> BELT_COLOURS = Map.of(
            "Black", "<:blackbelt:1215758642259693568>",
            "White", "<:whitebelt:1215765558570647643>",
            "Red", "<:redbelt:1215758588107034704>",
            "Purple", "<:purplebelt:1215758696240648265>",
            "Blue", "<:bluebelt:1215758720945098862>",
            "Green", "<:greenbelt:1215758751705993337>",
            "Orange", "<:orangebelt:1215758777597296712>",
            "Yellow", "<:yellowbelt:1215758672186183690>"
    );



    public Helper(){

    }

    /**
     * get the helper JDA EmbedBuilder to view all the commands
     * @return
     *
     * A JDA embedBuilder that shows all bot commands
     */



    public EmbedBuilder getHelper(){

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(DOJO_LOGO);
        embedBuilder.setDescription("**/help** \n View DojoLigaBot commands information \n\n"
                        + "**/leagueregister** \n Request Bot to provide ChessDojo team URL  \n\n **/verify** \n Follow bots steps to verify your Lichess.org account \n\n " +
                        "**/standing** \n View standings for club tournament Lichess URL\n\n" +
                        "**/profile** \n View your verified Lichess.org profile \n\n"+
                        "**/update** \n Update your ChessDojo belt for latest Lichess Live rating\n\n" +
                        "**/score** \n View your scores across all leagues \n\n" +
                        "**/rank**\n View your ranks across all leagues \n\n" +
                        "**/pairings**\n Request Lichess.org tournament pairings from URL\n\n" +
                        "**/leagueconfigarena**\n Create an arena league in ChessDojo club **[Perms: Admin]**\n\n" +
                        "**/leagueconfigswiss**\n Create an swiss league in ChessDojo club **[Perms: Admin]**\n\n" +
                        "**/standingshelp**\n Get helpful information on how leaderboard works\n\n" +
                        "**/computescores** \n enter tournament URL to compute the results one by one **[Perms: Admin]** \n\n" +
                        "**/stream** \n View ChessDojo Twitch URL \n\n" +
                "**/displaystandings**\n Display standings for various categories **[Perms: Admin]**\n\n" +
                "**/top10** \n View top 10 players for blitz, classical, rapid ratings \n\n" +
                "**unlink** \n\n unlink your Dojo Discord belt"

        );
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setTitle("DojoLigaBot Commands");

        return embedBuilder;

    }
    public void getStandingsHelp(SlashCommandInteractionEvent event){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setFooter("for information about commands run /help");
        embedBuilder.setColor(Color.green);
        embedBuilder.setTitle("League Standing Help");
        embedBuilder.setDescription("""
                **League Structure:**
                
                **Blitz Arena - Total Points**
                Total points scored per individual in all league arenas.
                
                **Blitz Swiss - Total Points**
                Total points scored per individual in all league swisses. 
                Follows (Lichess swiss points + Lichess swiss tie-breaks) rounded up.
                
                **Blitz Arena GP**
                Total 1-10 finishes in blitz arenas (10 pts for 1st, 9 pts for 2nd, etc.)
                
                **Blitz Swiss GP**
                Total 1-10 finishes in blitz swisses
                
                **Blitz Overall GP**
                Combined 1-10 finishes in blitz arenas and swisses

                **Tie-breaks:**\s
                 Dojo Liga Bot uses Lichess arena/swiss tie-break system to calculate player tie-breaks\s

                **Arena Tie-breaks**\s
                 Lichess uses tournament performance to calculate arena tie-breaks\s
                [Read more here](https://lichess.org/tournament/help)\s

                **Swiss Tie-breaks**\s
                 Lichess uses FIDE Swiss tie-break rules\s
                 [Read more here](https://lichess.org/swiss)
                 
                **Arena Bye** 
                There are no Bye in arena tournaments, players who have paused do not score any remaining points
                
                **Swiss Bye**
                Dojo Liga Bot will calculate byes in Swiss scores, more info can be found here
                [Read more here](https://lichess.org/swiss)
                
    
                 """);

        embedBuilder.setThumbnail(DOJO_LOGO);
        event.replyEmbeds(embedBuilder.build()).queue();
    }


    public void getLeagueHelp(SlashCommandInteractionEvent event){

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setFooter("for information about commands run /help");
        embedBuilder.setColor(Color.green);
        embedBuilder.setTitle("League Config Help");
        embedBuilder.setDescription("""
                
                **This are valid input values Lichess considers as valid, 
                any other value will break the bot so if you get stuck
                pull this card by running /leaguehelp**
                
                **League Config Arena Parameters Valid Inputs**
                
                **Time Start for Swiss/Arena**
                24 hour clock {0 to 23}
                
                **Tournament Count for Swiss/Arena:**
                {1 to 12)
               
                **Arena Name**
                Arena name must be 2 to 30 char long give it short and simple name \n
                Bot will add No. 1, 2 if tournament count > 1
                
                **Arena Clock Time Control Inputs:** 
                {2,3,4,5,6,7,8,10,15,20,25,30,40,50,60}
                
                **Arena Clock Time Control Increment Inputs:**
                {0,1,2,3,4,5,6,7,10,15,20,25,30,40,50,60}
                
                **Arena Single Tournament Duration Inputs (how long tournament runs)**:
                {20,25, 30, 35, 40, 45, 50, 55, 60, 70, 80, 90 ,100, 110, 120, 150, 180, 210,240, 270, 300, 330, 360, 420, 480, 540, 600, 720}
                
                **Arena Max Rating Inputs (U1400, U1500.. etc)** 
                Note: 0 means open category no rating limits
                {2200, 2100, 2000 ,1900, 1800 ,1700, 1600, 1500, 1400, 1300, 1200, 1100 ,1000, 900, 800, 0}
                
                **League Config Swiss Parameters Valid Input**
                
                **Swiss Name**
                Swiss name must be 2 to 30 char long give it short and simple name \n
                Bot will add No. 1, 2 if tournament count > 1
                
                **Swiss Clock Time Control Inputs:**
                {1, 2, 3, 4, 5, 6, 7, 8, 10, 15, 20, 25, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180}
                
                **Swiss Clock Time Increment Inputs:**                
                {0 to 120}
                
                **Swiss Number Of Rounds**
                {3 to 100}
                
                **Swiss Round Interval:**
                {1, 2, 3, 5, 10, 15, 20, 30, 45, 60, 1440, 2880, 10080}
                
                **Swiss Max Rating Inputs (U1400, U1500.. etc) 
                Note: 0 means open category no rating limits**
                {2200, 2100, 2000 ,1900, 1800 ,1700, 1600, 1500, 1400, 1300, 1200, 1100 ,1000, 900, 800, 0}
                   
             
                """);
        embedBuilder.setThumbnail(DOJO_LOGO);
        event.replyEmbeds(embedBuilder.build()).queue();
    }


    public void startHelper(SlashCommandInteractionEvent event){
        event.replyEmbeds(this.getHelper().build()).addActionRow(Button.link("https://www.chessdojo.club/", "Website")).queue();
    }











}