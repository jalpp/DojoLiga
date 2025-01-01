package dojo.bot.Controller.User;

import chariot.Client;
import com.mongodb.client.MongoCollection;
import dojo.bot.Controller.Discord.AntiSpam;
import dojo.bot.Controller.Discord.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.Document;

import java.awt.*;

public class UserProfileManager {



    /**
     * Discord Action to look up Lichess user profile based on verification object
     *
     * @param passport   Verification object
     * @param event      Slash command event
     * @param collection collection of players
     * @param client     Lichess java client
     * @param helper     Helper object
     */

    public void lookUpProfile(Verification passport, SlashCommandInteractionEvent event,
                              MongoCollection<Document> collection, Client client, Helper helper, AntiSpam Slow_down_buddy) {
        if (!Slow_down_buddy.checkSpam(event)) {
            if (!passport.userPresentNormal(collection, event.getUser().getId())) {
                event.reply("You have not verified your Lichess account!").setEphemeral(true).queue();
            } else {

                String name = passport.getReletatedLichessName(event.getUser().getId(), collection);

                if (!client.users().byId(name).isPresent()) {
                    event.reply("User not present in Lichess database, please try again with proper username").setEphemeral(true).queue();
                } else {
                    event.reply("generating " + name + "'s profile...").setEphemeral(true).queue();
                    Profile profile = new Profile(client, name);
                    EmbedBuilder profileBuilder = new EmbedBuilder();
                    profileBuilder.setThumbnail(Helper.DOJO_LOGO);
                    profileBuilder.setDescription(profile.getUserProfile());
                    profileBuilder.setColor(Color.BLUE);
                    event.getChannel().sendMessageEmbeds(profileBuilder.build())
                            .addActionRow(Button.link("https://lichess.org/@/" + name, "View On Lichess")).queue();
                }
            }
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }

    }


    /**
     * Lookup chesscom profile for given user
     * @param passport Verification passport
     * @param event Discord trigger event
     * @param collection Collection of players
     * @param Slow_down_buddy Antispam checker
     */

    public void lookupProfileChessCom(Verification passport, SlashCommandInteractionEvent event, MongoCollection<Document> collection, AntiSpam Slow_down_buddy){
        if(!Slow_down_buddy.checkSpam(event)){
            if(!passport.userPresentNormalChesscom(collection, event.getUser().getId())){
                event.reply("You have not verified your Chess.com account!").setEphemeral(true).queue();
            }else{
                String name = passport.getReletatedChessName(event.getUser().getId(), collection);
                CCProfile profile = new CCProfile(name);
                event.deferReply().queue();
                event.getHook().sendMessageEmbeds(profile.getCCProfile().build()).queue();
            }
        }else{
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }
    }














}
