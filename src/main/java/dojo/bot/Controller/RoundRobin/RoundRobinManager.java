package dojo.bot.Controller.RoundRobin;

import com.mongodb.client.MongoCollection;
import dojo.bot.Controller.Database.MongoConnect;
import dojo.bot.Controller.Discord.DiscordAdmin;
import dojo.bot.Controller.Discord.Helper;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bson.Document;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * The type Round robin manager.
 */
public class RoundRobinManager {

    private final RoundRobinDbActions actions = new RoundRobinDbActions();

    /**
     * Config round robin tournament.
     *
     * @param event        the event
     * @param RRcollection the r rcollection
     */
    public void configRoundRobinTournament(SlashCommandInteractionEvent event, MongoCollection<Document> RRcollection){

        try{
            if(DiscordAdmin.isDiscordAdmin(event)) {
                String name = event.getOption("rrname").getAsString();
                String desc = event.getOption("rrdesc").getAsString();
                boolean automode = event.getOption("rrautomode").getAsBoolean();
                int startCohort = event.getOption("start-cohort").getAsInt();
                int endCohort = event.getOption("end-cohort").getAsInt();

                CohortRange cohortRange = CohortRange.findCohortRange(startCohort, endCohort);

                if (cohortRange != null) {
                    String tournamentID = actions.createNewRoundRobinTournament(
                            name,
                            desc,
                            cohortRange,
                            automode,
                            RRcollection);
                    event.reply("Successfully Created **" + name + "** " + "Round Robin tournament! " + "The ID of the tournament is: " + "**" + tournamentID +  "**" + "\n Please note, the registration for the tournament is closed, " +
                            "when you are ready please open the tournament to players via **/opentournament <tournament ID>** \n" +
                            "to generate the pairings please run **/generatepairing** <ID>, to display the pairings to players please run **/displaypairings** <ID>, to view Round Robin commands run **/helprr**").queue();
                } else {
                    event.reply("Invalid start and end cohort, please enter right cohort values").queue();
                }
            }else{
                event.reply("Sorry you are not an admin!").queue();
            }

        }catch (Exception e){

            event.reply("Internal error!").queue();

        }



    }


    /**
     * Player register.
     *
     * @param event              the event
     * @param RRplayerCollection the r rplayer collection
     * @param RRcollection       the r rcollection
     */
    public void playerRegister(SlashCommandInteractionEvent event, MongoCollection<Document> RRplayerCollection, MongoCollection<Document> RRcollection){

        String DiscordID = event.getUser().getId();
        String DiscordName = event.getUser().getName();

        if(actions.alreadyRegisteredInTournament(RRcollection, DiscordName)){
            event.reply("You have already registered in a tournament!").queue();
        }

        boolean addSuccess = actions.addPlayerToDB(DiscordID,DiscordName, RRplayerCollection);

        if(!addSuccess){
            event.reply("Verification Error! Please verify either of your Chess.com/Lichess.org account! To verify Lichess account " +
                    "run /**verify**, for Chess.com run **/verifychesscom**, for more info please run /help").setEphemeral(true).queue();
        }else{
           try{
               CohortRange userCohortRole = CohortRange.getDiscordRoleCohort(Objects.requireNonNull(event.getMember()));
               String eligibleID = actions.getPlayersEligibleTournamentID(DiscordID, RRplayerCollection, RRcollection, userCohortRole);

               actions.addPlayerToRunningTournament(DiscordName, RRcollection, eligibleID);
               event.reply("Registration Successful! You be notified when the Round Robin starts!").queue();

           }catch (RoundRobinException | ChessComPubApiException | IOException e){
               event.reply(e.getMessage()).queue();
           }
        }

    }

    /**
     * Player withdraw.
     *
     * @param event        the event
     * @param RRcollection the r rcollection
     */
    public void playerWithdraw(SlashCommandInteractionEvent event, MongoCollection<Document> RRcollection){

        try{
            String DiscordName = event.getUser().getName();
            Document getTournamentId = actions.getRegisteredPlayerTournamentID(MongoConnect.getRRplayercollection(), RRcollection, DiscordName, Platform.DISCORD);

            String tournamentId = getTournamentId.getString("tournamentId");
            actions.removePlayerToRunningTournament(DiscordName, RRcollection, tournamentId);
            event.reply("Successfully removed the player: " + DiscordName).queue();



        } catch (Exception e) {
            event.reply(e.getMessage()).queue();
        }

    }


    /**
     * Generate pairings.
     *
     * @param event        the event
     * @param RRcollection the r rcollection
     */
    public void generatePairings(SlashCommandInteractionEvent event, MongoCollection<Document> RRcollection){
       try{
           if(DiscordAdmin.isDiscordAdmin(event)) {
               String tournamentID = event.getOption("rrid").getAsString();
               String internalResponse = actions.getRoundRobinPairingsInternally(RRcollection, tournamentID);
               System.out.println(tournamentID);
               event.reply("I have successfully generated the pairings").queue();
           }else{
               event.reply("Sorry you are not an admin!").queue();
           }

       }catch (RoundRobinException e){
           event.reply(e.getMessage()).queue();
       }

    }


    /**
     * Round robin helper.
     *
     * @param event the event
     */
    public void roundRobinHelper(SlashCommandInteractionEvent event){
        if(DiscordAdmin.isDiscordAdmin(event)){
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.BLUE);
            builder.setThumbnail(Helper.DOJO_LOGO);
            builder.setTitle("Dojo Round Robin Help Guide");
            builder.setDescription("""
                    
                    **/configroundrobin** <params>
                    config a new Round Robin tournament
                    
                    **/register**
                    register for upcoming round robin as a player
                    
                    **/opentournament** <ID>
                    open the tournament to players
                    
                    **/closetournament** <ID>
                    close the tournament to finished state
                    
                    **/publishtournament** <ID>
                    publish the tournament to players with tournament info
                    
                    **/generatepairings** <ID>
                    generate Round Robin pairings
                    
                    **/displaypairings** <ID>
                    display pairings to players
                    
                    **/adminaddplayer <ID> <User> <Platform>**
                    use admin rights to force push a player into the tournament and override system restrictions
                    
                    **/viewtournamentgames** <ID>
                    view tournament submitted games
                    
                    **/submitgame**
                    manually calculate the players scores admin only 
                    """);
         event.replyEmbeds(builder.build()).queue();
        }else{
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.BLUE);
            builder.setThumbnail(Helper.DOJO_LOGO);
            builder.setTitle("Dojo Round Robin Help Guide");
            builder.setDescription("""
                    
                    **/register**
                    Register for upcoming Dojo round robin tournament in your cohort
                    
                    **/withdraw**
                    Withdraw from registered tournament
                    """);
            event.replyEmbeds(builder.build()).queue();
        }
    }


    /**
     * Display pairings.
     *
     * @param event        the event
     * @param RRcollection the r rcollection
     */
    public void displayPairings(SlashCommandInteractionEvent event, MongoCollection<Document> RRcollection){
        try{
            if(DiscordAdmin.isDiscordAdmin(event)) {
                String tournamentID = event.getOption("rrdisid").getAsString();
                String internalResponse = actions.getPairingFromRunningTournament(RRcollection, tournamentID);
                event.replyEmbeds(new EmbedBuilder().setDescription(internalResponse).setThumbnail(Helper.DOJO_LOGO).setTitle("Tournament Pairing").setColor(Color.GREEN).build()).queue();
            }else {
                event.reply("Sorry you are not an admin!").queue();
            }
        }catch (RoundRobinException e){
            event.reply(e.getMessage()).queue();
        }

    }

    /**
     * Publish tournament.
     *
     * @param event        the event
     * @param RRcollection the r rcollection
     */
    public void publishTournament(SlashCommandInteractionEvent event, MongoCollection<Document> RRcollection){
        try{
            if(DiscordAdmin.isDiscordAdmin(event)) {
                String tournamentID = event.getOption("publishid").getAsString();
                RoundRobin roundRobin = actions.getRoundRobinInternally(RRcollection, tournamentID);
                EmbedBuilder builder = new EmbedBuilder();
                builder.setThumbnail(Helper.DOJO_LOGO);
                builder.setTitle(roundRobin.getTournamentName());
                builder.setDescription("**Tournament Description:** \n" + roundRobin.getDesc() + "\n\n **Time Control: ** \n" + roundRobin.getFormattedTimeControl() + "\n\n**Cohort**\n" + roundRobin.getCohortRange().getStart() + "-" + roundRobin.getCohortRange().getEnd()
                + "\n\n**Rules** \n\n" + "**1)** The time control is assigned from Dojo training program cohort requirements, please use this time control in your games \n" +
                        "**2)** You can challenge each other via Lichess/Chesscom online and submit the game afterwards via **/submitgame**" +
                        "\n **3)** Cheating is **NOT ALLOWED**, any sort of cheating will result in player being removed from tournament, and each player getting a 1 win point bye" +
                        "\n **4)** Players must verify their online accounts via **/verify** if you need any help please ping @nmp" +
                        "\n\n **Registration Process** \n In order to register players must run **/register**, further player verification might be needed" +
                        "\n\n **Game Rules** \n the pairings will be done in advance, the whites/blacks are random, and the winner gets 1 point, a draw gets 0.5 point and a lost gets 0 points");
                builder.setColor(Color.BLUE);
                event.replyEmbeds(builder.build()).queue();
            }else {
                event.reply("Sorry you are not an admin!").queue();
            }
        }catch (RoundRobinException e){
            event.reply(e.getMessage()).queue();
        }

    }


    /**
     * Open tournament.
     *
     * @param event        the event
     * @param RRcollection the r rcollection
     */
    public void openTournament(SlashCommandInteractionEvent event, MongoCollection<Document> RRcollection){
        try{
            if(DiscordAdmin.isDiscordAdmin(event)) {
                String tournamentID = event.getOption("openid").getAsString();
                actions.openTournamentToCalculation(RRcollection, tournamentID);
                event.reply("Successfully opened the tournament: " + tournamentID).queue();
            }else{
                event.reply("Sorry you are not an admin!").queue();
            }
        }catch (RoundRobinException e){
            event.reply(e.getMessage()).queue();
        }

    }

    /**
     * Close tournament.
     *
     * @param event        the event
     * @param RRcollection the r rcollection
     */
    public void closeTournament(SlashCommandInteractionEvent event, MongoCollection<Document> RRcollection){
        try{
            if(DiscordAdmin.isDiscordAdmin(event)) {
                String tournamentID = event.getOption("closeid").getAsString();
                actions.finishTournamentToPlayers(RRcollection, tournamentID);
                event.reply("Successfully closed the tournament: " + tournamentID).queue();
            }else{
                event.reply("Sorry you are not an admin!").queue();
            }
        }catch (RoundRobinException e){
            event.reply(e.getMessage()).queue();
        }

    }


    /**
     * Player submit game.
     *
     * @param event the event
     */
    public void playerSubmitGame(SlashCommandInteractionEvent event){
        playerSubmitModalTrigger(event);

    }

    /**
     * Player submit modal trigger.
     *
     * @param event the event
     */
    public void playerSubmitModalTrigger(SlashCommandInteractionEvent event){

        TextInput gameURL = TextInput.create("game-url", "Enter Game URL", TextInputStyle.SHORT)
                .setPlaceholder("input your Lichess/Chess.com game")
                .setMinLength(10)
                .setMaxLength(500)
                .setRequired(true)
                .build();

        TextInput player1 = TextInput.create("player1", "Enter White Player username", TextInputStyle.SHORT)
                .setPlaceholder("Enter White Player Lichess/Chess.com username")
                .setMinLength(2)
                .setMaxLength(500)
                .setRequired(true)
                .build();

        TextInput player2 = TextInput.create("player2", "Enter Black Player username", TextInputStyle.SHORT)
                .setPlaceholder("Enter Black Player  Lichess/Chess.com username")
                .setMinLength(2)
                .setMaxLength(500)
                .setRequired(true)
                .build();

        Modal sender = Modal.create("rr-modal", "Submit Your Round Robin game")
                .addActionRow(gameURL)
                .addActionRow(player1)
                .addActionRow(player2)
                .build();

        event.replyModal(sender).queue();


    }


    /**
     * Handle game modal.
     *
     * @param event              the event
     * @param RRplayercollection the r rplayercollection
     * @param RRcollection       the r rcollection
     */
    public void handleGameModal(ModalInteractionEvent event, MongoCollection<Document> RRplayercollection, MongoCollection<Document> RRcollection){

        try{

            if(!DiscordAdmin.isDiscordAdminModal(event)){
                event.reply("You are not an admin!").setEphemeral(true).queue();
            }

            String gameURL = event.getValue("game-url").getAsString();

            String player1 = event.getValue("player1").getAsString();

            String player2 = event.getValue("player2").getAsString();

            Platform platform = Platform.fromURL(gameURL);

            System.out.println("Platform" + platform);
            System.out.println(player1);

            if(platform == null){
                event.reply("Invalid platform URL, please provide Lichess or Chess.com game URL!").queue();
            }else{
                Document getRegisteredTournamentID = actions.getRegisteredPlayerTournamentID(RRplayercollection, RRcollection, player1.toLowerCase().trim(), platform);

                if(getRegisteredTournamentID != null){

                    event.reply("Thinking").queue();
                    String tournamentID = getRegisteredTournamentID.getString("tournamentId");

                    System.out.println(tournamentID);

                    if(actions.getGameSubmissionsFromRunningTournament(RRcollection, tournamentID).contains(gameURL)){
                        event.getChannel().sendMessage("This game has already been submitted to the system! Please play other games of rounds and submit those!").queue();
                        return;
                    }

                    switch (platform){
                        case LICHESS -> performGameCalculationStrategy(new LichessStrategy(gameURL, RRplayercollection, RRcollection, tournamentID));

                        case CHESSCOM -> performGameCalculationStrategy(new ChessComStrategy(gameURL, player1.toLowerCase().trim(), RRplayercollection, RRcollection, tournamentID));
                    }

                    actions.pushGameSubmissionForRunningTournament(RRcollection, gameURL, tournamentID);

                    event.getChannel().sendMessage("Successfully computed the scores for the game URL: " + gameURL).queue();

                }else{
                    event.reply("The player registration not found! You can not submit a game if you have not registered!").queue();
                }
            }



        }catch (RoundRobinException e){
            event.getChannel().sendMessage(e.getMessage()).queue();
            System.out.println(e.getMessage());
        }

    }


    /**
     * Perform game calculation strategy.
     *
     * @param strategy the strategy
     * @throws RoundRobinException the round robin exception
     */
    public void performGameCalculationStrategy(CalculateResultStrategy strategy) throws RoundRobinException {
        strategy.calculateGameResult();
    }


    /**
     * View submitted games.
     *
     * @param event        the event
     * @param RRcollection the r rcollection
     */
    public void viewSubmittedGames(SlashCommandInteractionEvent event, MongoCollection<Document> RRcollection){
        try{
            if(DiscordAdmin.isDiscordAdmin(event)) {
                String tournamentID = event.getOption("viewid").getAsString();
                List<String> games = actions.getGameSubmissionsFromRunningTournament(RRcollection, tournamentID);
                StringBuilder builder = new StringBuilder();
                for(String game: games){
                    builder.append(game);
                }
                event.reply("Here are the submitted games for" + tournamentID + "\n" + builder.toString()).queue();
            }else{
                event.reply("Sorry you are not an admin!").queue();
            }
        }catch (RoundRobinException e){
            event.reply(e.getMessage()).queue();
        }
    }


    /**
     * Admin force push player.
     *
     * @param event              the event
     * @param RRcollection       the r rcollection
     * @param RRplayercollection the r rplayercollection
     */
    public void adminForcePushPlayer(SlashCommandInteractionEvent event, MongoCollection<Document> RRcollection, MongoCollection<Document> RRplayercollection){
        try{
            if(DiscordAdmin.isDiscordAdmin(event)){

                String tournamentID = event.getOption("tour-id").getAsString();
                String playerusername = event.getOption("player-username").getAsString();
                String platform = event.getOptionsByName("platform").get(0).getAsString();

                switch (platform){
                    case "li-pl" -> {
                        actions.AdminForcePushPlayerToTournament(playerusername, Platform.LICHESS, tournamentID, RRplayercollection, RRcollection);
                        event.reply("Successfully added the player: " + playerusername + "In the tournament ID: " + tournamentID + " tournament!").queue();
                    }

                    case "cc-pl" -> {
                        actions.AdminForcePushPlayerToTournament(playerusername, Platform.CHESSCOM, tournamentID, RRplayercollection, RRcollection);
                        event.reply("Successfully added the player: " + playerusername + "In the tournament ID: " + tournamentID + " tournament!").queue();

                    }
                    case "dl-pl" -> {
                        actions.AdminForcePushPlayerToTournament(playerusername, Platform.DISCORD, tournamentID, RRplayercollection, RRcollection);
                        event.reply("Successfully added the player: " + playerusername + "In the tournament ID: " + tournamentID + " tournament!").queue();

                    }
                }

            }else{
                event.reply("Sorry you are not an admin!").queue();
            }
        }catch (RoundRobinException e){
            event.reply(e.getMessage()).queue();
        }
    }












}
