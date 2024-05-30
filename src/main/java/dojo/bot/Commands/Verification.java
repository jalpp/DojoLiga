package dojo.bot.Commands;

import chariot.Client;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import dojo.bot.Controller.ManageRoles;
import io.github.sornerol.chess.pubapi.client.PlayerClient;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.Document;

import java.io.IOException;

public class Verification {

    private final Client client = Client.basic();

    private final PlayerClient playerClient = new PlayerClient();


    public Verification(){

    }


    public void verificationStatus(String Lichessname, String DiscordId, SlashCommandInteractionEvent event){
        String checkDiscordId =  client.users().byId(Lichessname).get().profile().location().orElse("");
        ManageRoles manageRoles = new ManageRoles();

        if(checkDiscordId.equalsIgnoreCase(DiscordId)){
            event.reply("You have been verified for Lichess!").setEphemeral(true).queue();
        }else{
            event.reply("Verification failed please add your Discord ID " + DiscordId + " in your Lichess profile location field. Note: After setting the value \n" +
                    "please run /verify again, and you can remove the id from location if you want. \n" +
                    "**Steps**\n 1) Login in your Lichess account \n 2) Go to 'Profile', then hit edit profile button \n 3) Go to 'Location' field and update it to ID given \n 4) Hit save and run **/verify** again ")
                    .addActionRow(Button.link("https://lichess.org/login", "Login")).setEphemeral(true).queue();
        }

    }


    public boolean userPresent(MongoCollection<Document> collection, String discordId, String LichessName){

        Document queryname = new Document("Lichessname", LichessName);
        Document res = collection.find(queryname).first();
        if(collection.countDocuments(queryname) > 0 && res.getString("Discordid").equalsIgnoreCase("null") ){
            Document update = new Document("$set", new Document("Discordid", discordId));
            collection.findOneAndUpdate(queryname, update);
            return true;
        }else {
            Document query = new Document("Discordid", discordId);
            FindIterable<Document> result = collection.find(query);
            return result.iterator().hasNext();
        }
    }

    public boolean userPresentNormal(MongoCollection<Document> collection, String discordId){
            Document query = new Document("Discordid", discordId);
            FindIterable<Document> result = collection.find(query);
            return result.iterator().hasNext();
    }


    public String getReletatedLichessName(String DiscordId, MongoCollection<Document> collection){

        Document query = new Document("Discordid", DiscordId);

        Document result = collection.find(query).first();

        if(result != null){

            return result.getString("Lichessname");

        }else{
            return "Something went wrong!";
        }

    }



    public void verificationStatusChesscom(String ccname, String DiscordId, SlashCommandInteractionEvent event) throws ChessComPubApiException, IOException {
        String checkDiscordId =  playerClient.getPlayerByUsername(ccname).getLocation();
        //ManageRoles manageRoles = new ManageRoles();

        if( checkDiscordId != null && checkDiscordId.equalsIgnoreCase(DiscordId)){
            event.reply("You have been verified for Chess.com!").setEphemeral(true).queue();
        }else{
            event.reply("Verification failed please add your Discord ID " + DiscordId + " in your Chess.com profile location field. Note: After setting the value \n" +
                            "please run /verifychesscom again, and you can remove the id from location if you want. \n" +
                            "**Steps**\n 1) Login in your Chess.com account \n 2) Go to 'Profile', then hit edit profile \n 3) Go to 'Location' field and update it to ID given \n 4) hit save and run **/verifychesscom** again ")
                    .addActionRow(Button.link("https://www.chess.com/", "Login")).setEphemeral(true).queue();
        }

    }


    public boolean userPresentChesscom(MongoCollection<Document> collection, String discordId, String ccName){

        Document queryname = new Document("Chesscomname", ccName);
        Document res = collection.find(queryname).first();

            Document query = new Document("Discordid", discordId);
            FindIterable<Document> result = collection.find(query);
            return result.iterator().hasNext();
        
    }

    public boolean userPresentNormalChesscom(MongoCollection<Document> collection, String discordId){
        Document query = new Document("Discordid", discordId);
        FindIterable<Document> result = collection.find(query);
        return result.iterator().hasNext();
    }


    public String getReletatedChessName(String DiscordId, MongoCollection<Document> collection){

        Document query = new Document("Discordid", DiscordId);

        Document result = collection.find(query).first();

        if(result != null){

            return result.getString("Chesscomname");

        }else{
            return "Something went wrong!";
        }

    }





}
