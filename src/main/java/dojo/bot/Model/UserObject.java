package dojo.bot.Model;


import chariot.Client;
import net.dv8tion.jda.api.EmbedBuilder;

/**
 * Class that represents a User
 */
public class UserObject {

    private Client client;
    private EmbedBuilder embedBuilder;
    private String userID;


    public UserObject(Client client, String userParsing){
        this.client = client;
        this.userID = userParsing.toLowerCase().trim();
    }


    public Client getClient(){
        return this.client;
    }

    public String getUserID(){
        return this.userID;
    }




}
