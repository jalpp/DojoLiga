package dojo.bot.Controller.Discord;

import chariot.Client;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import dojo.bot.Controller.User.Profile;
import dojo.bot.Controller.User.Verification;
import dojo.bot.Controller.User.CCProfile;
import dojo.bot.Runner.Main;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.Document;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


/**
 * A object that takes care of managing user roles based on player ratings
 */
public class ManageRoles {


    private final Client client = Client.basic();


    /**
     * Instantiates a new Manage roles.
     */
    public ManageRoles() {

    }


    /**
     * Update roles chesscom.
     *
     * @param DiscordId the discord id
     * @param event     the event
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public void updateRolesChesscom(String DiscordId, SlashCommandInteractionEvent event) throws ChessComPubApiException, IOException {
        Verification verification = new Verification();
        if (verification.userPresentNormalChesscom(Main.chesscomplayers, DiscordId)) {
            String playerName = verification.getReletatedChessName(DiscordId, Main.chesscomplayers);
            removePreviousRoles(event.getGuild(), Objects.requireNonNull(event.getMember()));
            giveRolesBasedOnChessComRapidRating(playerName.toLowerCase(), event, true);
        } else {
            event.reply("You have not verified your Chess.com account! Please run **/verifychesscom**").setEphemeral(true).queue();
        }
    }


    /**
     * Updates the role for given Discord Id
     *
     * @param DiscordId user Discord Id
     * @param event     slashCommand event
     */
    public void updateRoles(String DiscordId, SlashCommandInteractionEvent event) {
        Verification verification = new Verification();
        if (verification.userPresentNormal(Main.collection, DiscordId)) {
            String playerName = verification.getReletatedLichessName(DiscordId, Main.collection);
            removePreviousRoles(event.getGuild(), event.getMember());
            assignRolesBasedOnRating(playerName.toLowerCase(), event, true);
        } else {
            event.reply("You have not verified your Lichess account! Please run **/verify**").queue();
        }
    }


    /**
     * Update live ratings.
     *
     * @param username         the username
     * @param collection       the collection
     * @param blitz_rating     the blitz rating
     * @param classical_rating the classical rating
     * @param rapid_rating     the rapid rating
     */
    public void updateLiveRatings(String username, MongoCollection<Document> collection, int blitz_rating, int classical_rating, int rapid_rating) {
        updatePlayer(username, blitz_rating, "blitz_rating", collection);
        updatePlayer(username, rapid_rating, "rapid_rating", collection);
        updatePlayer(username, classical_rating, "classical_rating", collection);


    }


    private void updatePlayer(String playerName, int value, String fieldName,
                              MongoCollection<Document> collection) {
        Document query = new Document("Lichessname", playerName);

        if (collection.countDocuments() <= 0) {
            System.out.println("Player not found: " + playerName);
            return;
        }

        collection.updateOne(query, Updates.set(fieldName, value));
        System.out.println("Value updated for player: " + playerName + " and field: " + fieldName);
    }

    private void updatePlayerCC(String playerName, int value, String fieldName,
                                MongoCollection<Document> collection) {
        Document query = new Document("Chesscomname", playerName);

        if (collection.countDocuments() <= 0) {
            System.out.println("Player not found: " + playerName);
            return;
        }

        collection.updateOne(query, Updates.set(fieldName, value));
        System.out.println("Value updated for player: " + playerName + " and field: " + fieldName);
    }


    /**
     * Give roles based on chess com rapid rating.
     *
     * @param ccName the cc name
     * @param event  the event
     * @param update the update
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public void giveRolesBasedOnChessComRapidRating(String ccName, SlashCommandInteractionEvent event, boolean update) throws ChessComPubApiException, IOException {
        CCProfile profile = new CCProfile(ccName);
        int rapid_rating = profile.getRapidRating();
        int blitz_rating = profile.getBlitzRating();

        updatePlayerCC(ccName, rapid_rating, "rapid_rating", Main.chesscomplayers);
        updatePlayerCC(ccName, blitz_rating, "blitz_rating", Main.chesscomplayers);

        if (rapid_rating < 1000) {
            calculateRoles("White", event, update, "a Chess.com Rapid rating of " + rapid_rating);
        } else if (rapid_rating < 1200 && rapid_rating >= 1000) {
            calculateRoles("Yellow", event, update, "a Chess.com Rapid rating of " + rapid_rating);
        } else if (rapid_rating < 1400 && rapid_rating >= 1200) {
            calculateRoles("Orange", event, update, "a Chess.com Rapid rating of " + rapid_rating);
        } else if (rapid_rating < 1600 && rapid_rating >= 1400) {
            calculateRoles("Green", event, update, "a Chess.com Rapid rating of " + rapid_rating);
        } else if (rapid_rating < 1800 && rapid_rating >= 1600) {
            calculateRoles("Blue", event, update, "a Chess.com Rapid rating of " + rapid_rating);
        } else if (rapid_rating < 2100 && rapid_rating >= 1800) {
            calculateRoles("Purple", event, update, "a Chess.com Rapid rating of " + rapid_rating);
        } else if (rapid_rating < 2400 && rapid_rating >= 2100) {
            calculateRoles("Red", event, update, "a Chess.com Rapid rating of " + rapid_rating);
        } else if (rapid_rating < 3300 && rapid_rating >= 2400) {
            calculateRoles("Black", event, update, "a Chess.com Rapid rating of " + rapid_rating);
        }
    }


    /**
     * Calculate chesscom role index int.
     *
     * @param event the event
     * @param pass  the pass
     * @param cc    the cc
     * @return the int
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public int calculateChesscomRoleIndex(SlashCommandInteractionEvent event, Verification pass, String cc) throws ChessComPubApiException, IOException {
        if (pass.userPresentNormalChesscom(Main.chesscomplayers, event.getUser().getId())) {
            CCProfile profile = new CCProfile(cc);
            int rapid_rating = profile.getRapidRating();


            if (rapid_rating < 1000) {
                return 0;
                //calculateRoles("White", event, update, "a Chess.com Rapid rating of " + rapid_rating);
            } else if (rapid_rating < 1200) {
                return 1;
                //calculateRoles("Yellow", event, update, "a Chess.com Rapid rating of " + rapid_rating);
            } else if (rapid_rating < 1400) {
                return 2;
                //calculateRoles("Orange", event, update, "a Chess.com Rapid rating of " + rapid_rating);
            } else if (rapid_rating < 1600) {
                return 3;
                //calculateRoles("Green", event, update, "a Chess.com Rapid rating of " + rapid_rating);
            } else if (rapid_rating < 1800) {
                return 4;
                //calculateRoles("Blue", event, update, "a Chess.com Rapid rating of " + rapid_rating);
            } else if (rapid_rating < 2100) {
                return 5;
                //calculateRoles("Purple", event, update, "a Chess.com Rapid rating of " + rapid_rating);
            } else if (rapid_rating < 2400) {
                return 6;
                //calculateRoles("Red", event, update, "a Chess.com Rapid rating of " + rapid_rating);
            } else if (rapid_rating < 3300) {
                return 7;
                //calculateRoles("Black", event, update, "a Chess.com Rapid rating of " + rapid_rating);
            }
        }

        return -1;
    }


    // W Y O G B P R Bl
    // 0  1 2 3 4 5 6 7

    /**
     * Calculate lichess role index int.
     *
     * @param event the event
     * @param pass  the pass
     * @param li    the li
     * @return the int
     */
    public int calculateLichessRoleIndex(SlashCommandInteractionEvent event, Verification pass, String li) {
        if (pass.userPresentNormal(Main.collection, event.getUser().getId())) {
            Profile profile = new Profile(client, li);

            int rapid_rating = profile.getSingleRapidRating();
            int cla_rating = profile.getSingleClassicalRating();


            if (rapid_rating == -1 && cla_rating == -1) {
                return -1;
            }


            if (rapid_rating > cla_rating) {
                if (rapid_rating <= 1199) {
                    return 0;
                    //calculateRoles("White", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(1200, 1399, rapid_rating)) {
                    return 1;
                    //calculateRoles("Yellow", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(1400, 1599, rapid_rating)) {
                    return 2;
                    //calculateRoles("Orange", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(1600, 1799, rapid_rating)) {
                    return 3;
                    //calculateRoles("Green", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(1800, 1999, rapid_rating)) {
                    return 4;
                    //calculateRoles("Blue", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(2000, 2199, rapid_rating)) {
                    return 5;
                    //calculateRoles("Purple", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(2200, 2499, rapid_rating)) {
                    return 6;
                    //calculateRoles("Red", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(2500, 4000, rapid_rating)) {
                    return 7;
                    //calculateRoles("Black", event, update, "a Lichess Rapid rating of " + rapid_rating);
                }
            } else {
                if (cla_rating <= 1199) {
                    return 0;
                    //calculateRoles("White", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(1200, 1399, cla_rating)) {
                    return 1;
                    //calculateRoles("Yellow", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(1400, 1599, cla_rating)) {
                    return 2;
                    //calculateRoles("Orange", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(1600, 1799, cla_rating)) {
                    return 3;
                    //calculateRoles("Green", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(1800, 1999, cla_rating)) {
                    return 4;
                    //calculateRoles("Blue", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(2000, 2099, cla_rating)) {
                    return 5;
                    //calculateRoles("Purple", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(2100, 2199, cla_rating)) {
                    return 6;
                    //calculateRoles("Red", event, update, "a Lichess Rapid rating of " + rapid_rating);
                } else if (isInBeltRange(2200, 4000, cla_rating)) {
                    return 7;
                    //calculateRoles("Black", event, update, "a Lichess Rapid rating of " + rapid_rating);
                }

            }


        }

        return -1;

    }


    /**
     * Assign roles based on rating of a given user
     *
     * @param LichessUsername PlayerName
     * @param event           SlashcommandEvent
     * @param update          true or false if its updating or not
     */
    public void assignRolesBasedOnRating(String LichessUsername, SlashCommandInteractionEvent event, boolean update) {

        Profile profile = new Profile(client, LichessUsername.toLowerCase());
        int rapid_rating = profile.getSingleRapidRating();
        int cla_rating = profile.getSingleClassicalRating();
        int blz_rating = profile.getSingleBlitzRating();


        if (rapid_rating == -1 && cla_rating == -1) {
            event.getChannel().sendMessage("Your rapid and classical rating is still provisional, please try again after getting an established rating (20 games)").queue();
            return;
        }

        updateLiveRatings(LichessUsername, Main.collection, blz_rating, cla_rating, rapid_rating);


        if (rapid_rating > cla_rating) {
            if (rapid_rating <= 1199) {
                calculateRoles("White", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(1200, 1399, rapid_rating)) {
                calculateRoles("Yellow", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(1400, 1599, rapid_rating)) {
                calculateRoles("Orange", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(1600, 1799, rapid_rating)) {
                calculateRoles("Green", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(1800, 1999, rapid_rating)) {
                calculateRoles("Blue", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(2000, 2199, rapid_rating)) {
                calculateRoles("Purple", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(2200, 2499, rapid_rating)) {
                calculateRoles("Red", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(2500, 4000, rapid_rating)) {
                calculateRoles("Black", event, update, "a Lichess Rapid rating of " + rapid_rating);
            }
        } else {
            if (cla_rating <= 1199) {
                calculateRoles("White", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(1200, 1399, cla_rating)) {
                calculateRoles("Yellow", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(1400, 1599, cla_rating)) {
                calculateRoles("Orange", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(1600, 1799, cla_rating)) {
                calculateRoles("Green", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(1800, 1999, cla_rating)) {
                calculateRoles("Blue", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(2000, 2099, cla_rating)) {
                calculateRoles("Purple", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(2100, 2199, cla_rating)) {
                calculateRoles("Red", event, update, "a Lichess Rapid rating of " + rapid_rating);
            } else if (isInBeltRange(2200, 4000, cla_rating)) {
                calculateRoles("Black", event, update, "a Lichess Rapid rating of " + rapid_rating);
            }

        }

    }


    /**
     * Calculate roles for a given belt name
     *
     * @param beltName the belt name
     * @param event    SlashCommandEvent
     * @param update   true or false if its updating or not
     * @param reason   the reason
     */
    public void calculateRoles(String beltName, SlashCommandInteractionEvent event, boolean update, String reason) {

        if (!update) {
            User user = event.getUser();
            Role role = Objects.requireNonNull(event.getGuild()).getRolesByName(beltName + " Belt", true).get(0);
            event.getGuild().addRoleToMember(user, role).queue();
            event.getChannel().sendMessage("Verification successful! " + "Based on " + reason + ", " + event.getUser().getAsMention() + " has earned the " + beltName + " Belt! \uD83E\uDD4B ").addActionRow(Button.link("https://www.chessdojo.club/", "Success")).queue(msg -> msg.addReaction(Emoji.fromFormatted(Helper.BELT_COLOURS.get(beltName))).queue());
        } else {
            User user = event.getUser();
            Role role = Objects.requireNonNull(event.getGuild()).getRolesByName(beltName + " Belt", true).get(0);
            event.getGuild().addRoleToMember(user, role).queue();
            event.getChannel().sendMessage("Based on " + reason + ", " + event.getUser().getAsMention() + " has earned the " + beltName + " Belt! \uD83E\uDD4B ").addActionRow(Button.link("https://www.chessdojo.club/", "Success")).queue(msg -> msg.addReaction(Emoji.fromFormatted(Helper.BELT_COLOURS.get(beltName))).queue());
        }
    }

    /**
     * Remove the previous role of a user which is a belt role
     *
     * @param guild  the Discord guild in which the bot has to remove the role
     * @param member the member for which the role will be removed for
     */
    public static void removePreviousRoles(Guild guild, Member member) {
        List<Role> roles = member.getRoles();

        for (Role role : roles) {
            String roleName = role.getName().toLowerCase();
            if (roleName.equalsIgnoreCase("White belt") || roleName.equalsIgnoreCase("Green belt") || roleName.equalsIgnoreCase("Red Belt")
                    || roleName.equalsIgnoreCase("Black belt") || roleName.equalsIgnoreCase("Purple belt")
                    || roleName.equalsIgnoreCase("Blue belt") || roleName.equalsIgnoreCase("Yellow belt")) {
                guild.removeRoleFromMember(member, role).queue();
            }
        }
    }

    /**
     * Starts the updating role process
     *
     * @param event a Slash command event for which the bot starts the process
     * @param pass  the pass
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public void startUpdatingRoles(SlashCommandInteractionEvent event, Verification pass) throws ChessComPubApiException, IOException {
        event.deferReply().setEphemeral(true).queue();
        String li = pass.getReletatedLichessName(event.getUser().getId(), Main.collection);
        String cc = pass.getReletatedChessName(event.getUser().getId(), Main.chesscomplayers);

        if (calculateChesscomRoleIndex(event, pass, cc) == -1 && calculateLichessRoleIndex(event, pass, li) == -1) {
            event.reply("Error! Lichess.org Rapid and Classical ratings are ? and Chess.com account not linked! " +
                    "I can't give belts. Please play more games or link chess.com account").setEphemeral(true).queue();
            return;
        }

        this.assignTheHighestRole(pass, event, true);

    }


    /**
     * The main assigner control logic
     *
     * @param pass   verification object
     * @param event  Slash event
     * @param update update belt or not
     * @throws ChessComPubApiException User not found exception
     * @throws IOException             the io exception
     */
    public void assignTheHighestRole(Verification pass, SlashCommandInteractionEvent event, boolean update) throws ChessComPubApiException, IOException {

        String li = pass.getReletatedLichessName(event.getUser().getId(), Main.collection);
        String cc = pass.getReletatedChessName(event.getUser().getId(), Main.chesscomplayers);


        if (calculateChesscomRoleIndex(event, pass, cc) > calculateLichessRoleIndex(event, pass, li)) {
            removePreviousRoles(event.getGuild(), Objects.requireNonNull(event.getMember()));
            giveRolesBasedOnChessComRapidRating(cc, event, update);
        } else if (calculateLichessRoleIndex(event, pass, li) > calculateChesscomRoleIndex(event, pass, cc)) {
            removePreviousRoles(event.getGuild(), Objects.requireNonNull(event.getMember()));
            assignRolesBasedOnRating(li, event, update);
        } else {
            removePreviousRoles(event.getGuild(), Objects.requireNonNull(event.getMember()));
            giveRolesBasedOnChessComRapidRating(cc, event, update);
        }


    }


    /**
     * Is in belt range boolean.
     *
     * @param lowerBound   the lower bound
     * @param upperBound   the upper bound
     * @param targetRating the target rating
     * @return the boolean
     */
    public boolean isInBeltRange(int lowerBound, int upperBound, int targetRating) {
        return (targetRating >= lowerBound && targetRating <= upperBound);
    }


}