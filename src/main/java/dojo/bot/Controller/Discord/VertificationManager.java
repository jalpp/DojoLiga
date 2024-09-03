package dojo.bot.Controller.Discord;

import com.mongodb.client.MongoCollection;
import dojo.bot.Controller.Database.MongoConnect;
import dojo.bot.Controller.User.CCProfile;
import dojo.bot.Controller.User.ChessPlayer;
import dojo.bot.Controller.User.Verification;
import dojo.bot.Runner.Keys;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bson.Document;

import java.io.IOException;

public class VertificationManager {


    /**
     * Discord action to start user verification flow
     *
     * @param event      Slash command event
     * @param passport   Verification object
     * @param collection collection of players
     */


    public void startVerificationProcessLichess(SlashCommandInteractionEvent event, Verification passport,
                                                MongoCollection<Document> collection) throws ChessComPubApiException, IOException {
        ManageRoles manageRoles = new ManageRoles();
        String name = event.getOption("lichess-username").getAsString().toLowerCase().trim();
        if (passport.userPresent(collection, event.getUser().getId(), name)) {
            String cc = passport.getReletatedChessName(event.getUser().getId(), MongoConnect.getChesscomplayers());

            if (manageRoles.calculateChesscomRoleIndex(event, passport, cc) == -1 && manageRoles.calculateLichessRoleIndex(event, passport, name) == -1) {
                event.reply("Error! Lichess.org Rapid and Classical ratings are ? and Chess.com account not linked! " +
                        "I can't give belts. Please play more games or link chess.com account").setEphemeral(true).queue();
                return;
            }

            if (event.getGuild().getId().equalsIgnoreCase(Keys.MAIN_GUILD)) {
                event.reply(
                                "You have already been verified, Join our ChessDojo Lichess team if you have not already [**Join**](https://lichess.org/team/chessdojo). you can also run **/profile** to view your linked Lichess profile!")
                        .setEphemeral(true).queue();

                manageRoles.assignTheHighestRole(passport, event, false);
            } else {
                event.reply("You have successfully connected your Lichess account for round robins").queue();
            }
        } else {
            ChessPlayer player = new ChessPlayer(name, event.getUser().getId(), 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0,
                    0, 0, 0,
                    0, 0, 0, 0,
                    0, 0);
            Document document = new Document("Lichessname", player.getLichessname())
                    .append("Discordid", player.getDiscordId())
                    .append("blitz_score", player.getBLITZ_SCORE()) // blitz_score // rapid_score // blitz_score_gp // rapid_score_gp
                    .append("rapid_score", player.getRAPID_SCORE()) // blitz_score_swiss 0.0 rapid_score_swiss 0.0
                    .append("classical_score", player.getCLASSICAL_SCORE()) // blitz_score_swiss_gp // rapid_score_swiss_gp
                    .append("blitz_rating", 0).append("classical_rating", 0) // blitz_comb_total // rapid_comb_total
                    .append("rapid_rating", 0).append("blitz_score_gp", 0)  // blitz_comb_total_gp rapid_comb_total_gp
                    .append("rapid_score_gp", 0).append("classical_score_gp", 0) // sp_score // eg_score //sp_rating // eg_rating
                    .append("blitz_score_swiss", 0.0).append("rapid_score_swiss", 0.0)
                    .append("classical_score_swiss", 0.0).append("blitz_score_swiss_gp", 0)
                    .append("rapid_score_swiss_gp", 0).append("classical_score_swiss_gp", 0)
                    .append("blitz_comb_total", 0).append("blitz_comb_total_gp", 0)
                    .append("rapid_comb_total", 0).append("rapid_comb_total_gp", 0)
                    .append("classical_comb_total", 0).append("classical_comb_total_gp", 0).append("sp_score", 0.0)
                    .append("sparring_rating", 0).append("eg_score", 0.0)
                    .append("eg_rating", 0);
            collection.insertOne(document);
            passport.verificationStatus(name, event.getUser().getId(), event);

        }

    }


    public void startVerificationProcessChessCom(SlashCommandInteractionEvent event, Verification passport,
                                                 MongoCollection<Document> collection) throws ChessComPubApiException, IOException {
        ManageRoles manageRoles = new ManageRoles();

        String name = event.getOption("chesscom-username").getAsString().toLowerCase().trim();
        if (passport.userPresentChesscom(collection, event.getUser().getId(), name)) {
            event.reply("You are already verified!").setEphemeral(true).queue();

            String li = passport.getReletatedLichessName(event.getUser().getId(), MongoConnect.getChesscomplayers());
            String cc = passport.getReletatedChessName(event.getUser().getId(), MongoConnect.getChesscomplayers());

            if (manageRoles.calculateChesscomRoleIndex(event, passport, cc) == -1 && manageRoles.calculateLichessRoleIndex(event, passport, li) == -1) {
                event.reply("Error! Lichess.org Rapid and Classical ratings are ? and Chess.com account not linked! " +
                        "I can't give belts. Please play more games or link chess.com account").setEphemeral(true).queue();
                return;
            }

            if (event.getGuild().getId().equalsIgnoreCase(Keys.MAIN_GUILD)) {
                manageRoles.assignTheHighestRole(passport, event, false);
            } else {
                event.reply("You have successfully connected your Chess.com account for round robins").queue();
            }

        } else {
            ChessPlayer player = new ChessPlayer(name, event.getUser().getId());
            CCProfile profile = new CCProfile(name);
            Document document = new Document("Chesscomname", player.getChesscomname())
                    .append("Discordid", player.getDiscordId())
                    .append("blitz_score", 0)
                    .append("rapid_score", 0)
                    .append("blitz_rating", profile.getBlitzRating())
                    .append("rapid_rating", profile.getRapidRating()).append("blitz_score_gp", 0)
                    .append("rapid_score_gp", 0)
                    .append("blitz_score_swiss", 0.0).append("rapid_score_swiss", 0.0)
                    .append("blitz_score_swiss_gp", 0)
                    .append("rapid_score_swiss_gp", 0)
                    .append("blitz_comb_total", 0).append("blitz_comb_total_gp", 0)
                    .append("rapid_comb_total", 0).append("rapid_comb_total_gp", 0)
                    .append("sp_score", 0.0)
                    .append("sparring_rating", 0).append("eg_score", 0.0)
                    .append("eg_rating", 0);

            collection.insertOne(document);
            passport.verificationStatusChesscom(name, event.getUser().getId(), event);

        }
    }


}

