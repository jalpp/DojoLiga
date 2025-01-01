package dojo.bot.Controller.User;

import chariot.Client;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import dojo.bot.Controller.Standing.Platform;
import io.github.sornerol.chess.pubapi.client.PlayerClient;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * The type Verification.
 */
public class Verification {

    private final Client client = Client.basic();

    private final PlayerClient playerClient = new PlayerClient();


    /**
     * Instantiates a new Verification.
     */
    public Verification(){

    }


    /**
     * Verifies the user's Lichess profile by checking if the provided Discord ID matches the one stored in the Lichess profile location field.
     * If the verification is successful, sends a message indicating that the user has been verified for Lichess.
     * If the verification fails, sends a message with instructions on how to add the Discord ID to the Lichess profile location field and run the /verify command again.
     *
     * @param Lichessname the Lichess username of the user to be verified
     * @param DiscordId   the Discord ID of the user to be verified
     * @param event       the event triggered by the user's interaction with the bot
     */
    public void verificationStatus(String Lichessname, String DiscordId, SlashCommandInteractionEvent event){
        boolean checkClosed = client.users().byId(Lichessname).get().disabled();
        boolean checkTOSViolation = client.users().byId(Lichessname).get().tosViolation();

        if(checkClosed || checkTOSViolation){
            event.reply("The following account is closed or has violated Lichess TOS! The following account can't be used to obtain Dojo belt").queue();
        }

        String checkDiscordId =  client.users().byId(Lichessname).get().profile().location().orElse("");

        if(checkDiscordId.equalsIgnoreCase(DiscordId)){
            event.reply("You have been verified for Lichess!").setEphemeral(true).queue();
        }else{
            event.reply("Verification failed please add your Discord ID " + DiscordId + " in your Lichess profile location field. Note: After setting the value \n" +
                    "please run /verify again, and you can remove the id from location if you want. \n" +
                    "**Steps**\n 1) Login in your Lichess account \n 2) Go to 'Profile', then hit edit profile button \n 3) Go to 'Location' field and update it to ID given \n 4) Hit save and run **/verify** again ")
                    .addActionRow(Button.link("https://lichess.org/login", "Login")).setEphemeral(true).queue();
        }

    }


    /**
     * User present boolean.
     *
     * @param collection  the collection
     * @param discordId   the discord id
     * @param LichessName the lichess name
     * @return the boolean
     */
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


    /**
     * Get discord id by lichess username string.
     *
     * @param collection  the collection
     * @param LichessUser the lichess user
     * @return the string
     */
    public String getDiscordIdByLichessUsername(MongoCollection<Document> collection, String LichessUser){
        return getGeneralSearchBasedOnParams("Lichessname", LichessUser, collection, "Discordid");
    }

    /**
     * Get discord id by chesscom username string.
     *
     * @param collection the collection
     * @param ccUser     the cc user
     * @return the string
     */
    public String getDiscordIdByChesscomUsername(MongoCollection<Document> collection, String ccUser){
        return getGeneralSearchBasedOnParams("Chesscomname", ccUser, collection, "Discordid");
    }

    /**
     * User present normal boolean.
     *
     * @param collection the collection
     * @param discordId  the discord id
     * @return the boolean
     */
    public boolean userPresentNormal(MongoCollection<Document> collection, String discordId){
            Document query = new Document("Discordid", discordId);
            FindIterable<Document> result = collection.find(query);
            return result.iterator().hasNext();
    }

    /**
     * Get general search based on params string.
     *
     * @param targetSearch the target search
     * @param targetID     the target id
     * @param collection   the collection
     * @param returnId     the return id
     * @return the string
     */
    public String getGeneralSearchBasedOnParams(String targetSearch, String targetID, MongoCollection<Document> collection, String returnId){
        Document query = new Document(targetSearch, targetID);

        Document result = collection.find(query).first();

        if(result != null){

            return result.getString(returnId);

        }else{
            return "null";
        }
    }


    /**
     * Get reletated lichess name string.
     *
     * @param DiscordId  the discord id
     * @param collection the collection
     * @return the string
     */
    public String getReletatedLichessName(String DiscordId, MongoCollection<Document> collection){

        return getGeneralSearchBasedOnParams("Discordid", DiscordId, collection, "Lichessname");

    }


    /**
     * Verification status chesscom.
     *
     * @param ccname    the ccname
     * @param DiscordId the discord id
     * @param event     the event
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public void verificationStatusChesscom(String ccname, String DiscordId, SlashCommandInteractionEvent event) throws ChessComPubApiException, IOException {
        String checkDiscordId =  playerClient.getPlayerByUsername(ccname).getLocation();

        String isNotAllowed = playerClient.getPlayerByUsername(ccname).getMembershipStatus().toString().toLowerCase();


        if(isNotAllowed.contains("closed")){
            event.reply("The following account is closed or has violated Chess.com TOS! The following account can't be used to obtain Dojo belt").queue();

        }

        if( checkDiscordId != null && checkDiscordId.equalsIgnoreCase(DiscordId)){
            event.reply("You have been verified for Chess.com!").setEphemeral(true).queue();
        }else{
            event.reply("Verification failed please add your Discord ID " + DiscordId + " in your Chess.com profile location field. Note: After setting the value \n" +
                            "please run /verifychesscom again, and you can remove the id from location if you want. \n" +
                            "**Steps**\n 1) Login in your Chess.com account \n 2) Go to 'Profile', then hit edit profile \n 3) Go to 'Location' field and update it to ID given \n 4) hit save and run **/verifychesscom** again ")
                    .addActionRow(Button.link("https://www.chess.com/", "Login")).setEphemeral(true).queue();
        }

    }


    /**
     * User present chesscom boolean.
     *
     * @param collection the collection
     * @param discordId  the discord id
     * @param ccName     the cc name
     * @return the boolean
     */
    public boolean userPresentChesscom(MongoCollection<Document> collection, String discordId, String ccName){

        Document queryname = new Document("Chesscomname", ccName);
        Document res = collection.find(queryname).first();

            Document query = new Document("Discordid", discordId);
            FindIterable<Document> result = collection.find(query);
            return result.iterator().hasNext();

    }

    /**
     * User present normal chesscom boolean.
     *
     * @param collection the collection
     * @param discordId  the discord id
     * @return the boolean
     */
    public boolean userPresentNormalChesscom(MongoCollection<Document> collection, String discordId){
        Document query = new Document("Discordid", discordId);
        FindIterable<Document> result = collection.find(query);
        return result.iterator().hasNext();
    }


    /**
     * Get reletated chess name string.
     *
     * @param DiscordId  the discord id
     * @param collection the collection
     * @return the string
     */
    public String getReletatedChessName(String DiscordId, MongoCollection<Document> collection){
        return getGeneralSearchBasedOnParams("Discordid", DiscordId, collection, "Chesscomname");

    }

    public Document getPlayerDoc (MongoCollection<Document> RRplayercollection, String DiscordId){
        Document query = new Document("Discordid", DiscordId);
        return RRplayercollection.find(query).first();
    }

    public void updatePlayerForPlatformName(MongoCollection<Document> RRplayercollection, Document playerDoc, @NotNull Platform platform, String username){
        UpdateResult result = RRplayercollection.updateOne(
                playerDoc,
                Updates.set(platform.toString(), username)
        );
    }



}
