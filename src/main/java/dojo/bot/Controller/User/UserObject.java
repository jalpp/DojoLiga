package dojo.bot.Controller.User;


import chariot.Client;
import net.dv8tion.jda.api.EmbedBuilder;

/**
 * The type User object.
 */
public class UserObject {

    private Client client;
    private EmbedBuilder embedBuilder;
    private String userID;


    /**
     * Instantiates a new User object.
     *
     * @param client      the client
     * @param userParsing the user parsing
     */
    public UserObject(Client client, String userParsing){
        this.client = client;
        this.userID = userParsing.toLowerCase().trim();
    }


    /**
     * Get client client.
     *
     * @return the client
     */
    public Client getClient(){
        return this.client;
    }

    /**
     * Get user id string.
     *
     * @return the string
     */
    public String getUserID(){
        return this.userID;
    }




}
