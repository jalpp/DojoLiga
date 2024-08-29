package dojo.bot.Controller.Discord;

import chariot.Client;
import chariot.ClientAuth;
import dojo.bot.Runner.Main;

import java.util.Random;

/**
 * Class that represents Lichess team Messenger
 */
public class Messenger {

    private final ClientAuth client = Client.auth(Main.botToken);
    private static final Client clientbasic = Client.basic();

    private final String[] messages = {
            "Hey all Dojo members! \n\n Lichess Liga starts soon, come by and play for the Dojo and aim for first!",
            "Hey all!\n" +
                    "\n" +
                    "Liga battle will start soon, come by and play for ChessDojo! ",
            "Hello hello Dojoers! Guess what we have Lichess liga starting soon! Come by and play for us!",
            "Bonjour! this is your reminder that Lichess liga will start soon, please come by and play for Dojo and aim for first place!",
            "Hi! get ready Dojoers, in no time Lichess liga will start, its time to break the ice, and aim for 1st place!!"

    };


    /**
     * Instantiates a new Messenger.
     */
    public Messenger(){

    }


    /**
     * Send message.
     */
    public void sendMessage(){
        String DOJO_TEAM = "chessdojo";
        client.teams().messageTeam(DOJO_TEAM, getMessage());
    }


    /**
     * Get message string.
     *
     * @return the string
     */
    public String getMessage(){
        return messages[new Random().nextInt(messages.length)] + "\n\n " + getTeamUrl();
    }

    /**
     * Get team url string.
     *
     * @return the string
     */
    public String getTeamUrl(){
        return "https://lichess.org/team/chessdojo";
    }





}
