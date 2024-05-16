package dojo.bot.Controller;

import chariot.Client;
import chariot.model.Swiss;
import chariot.model.Tournament;
import com.mongodb.client.MongoCollection;
import dojo.bot.Commands.Helper;
import dojo.bot.Commands.Profile;
import dojo.bot.Commands.Verification;
import dojo.bot.Model.DbTournamentEntry;
import dojo.bot.Runner.Main;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.bson.Document;

import java.awt.*;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.*;

import static dojo.bot.Controller.DiscordAdmin.isDiscordAdmin;
import static dojo.bot.Controller.DiscordAdmin.isDiscordAdminMessage;

/**
 * An Object that provides methods for DojoSwissManager to act upon Discord
 * events related to creating Leagues, checking admins, etc
 */

public class DiscordAction {

    private final AntiSpam Slow_down_buddy = new AntiSpam(60000, 5);
    private final TicketManager ticketSystem = new TicketManager();
    private final StandingReactManager standingReactManager = new StandingReactManager();
    private final ConfigLeagueManager configLeagueManager = new ConfigLeagueManager();
    private final VertificationManager vertificationManager = new VertificationManager();

    public DiscordAction() {

    }

    /**
     * Discord Action to send standings in Discord
     *
     * @param event      SlashCommand event
     * @param standings  compute standings object
     * @param collection collection of players
     */

    public void StandingsReact(SlashCommandInteractionEvent event, ComputeStandings standings,
                               MongoCollection<Document> collection) {
        if(!Slow_down_buddy.checkSpam(event)) {
            standingReactManager.StandingsReact(event,standings, collection);
        } else {
            event.reply("Slow Down Admin ;) Try again in 1 min").setEphemeral(true).queue();
        }

    }

    /**
     * Configs an arena league
     *
     * @param event                 Slash command event
     * @param arenaLeagueCollection collection of arenas
     */

    public void configLeagueArena(SlashCommandInteractionEvent event, MongoCollection<Document> arenaLeagueCollection) {
        if (!Slow_down_buddy.checkSpam(event)) {
            configLeagueManager.configLeagueArena(event,arenaLeagueCollection);
        } else {
            event.reply("Slow down Admin ;) Try again in 1 min").setEphemeral(true).queue();
        }
    }

    /**
     * Configs a swiss league
     *
     * @param event                 Slash command event
     * @param swissLeagueCollection collection of swiss tournaments
     */

    public void configLeagueSwiss(SlashCommandInteractionEvent event, MongoCollection<Document> swissLeagueCollection) {

        if (!Slow_down_buddy.checkSpam(event)) {
           configLeagueManager.configLeagueSwiss(event,swissLeagueCollection);
        } else {
            event.reply("Slow down Admin ;) Try again in 1 min").setEphemeral(true).queue();
        }
    }

    /**
     * Discord Action to start computing scores
     *
     * @param event                 Slash command event
     * @param compute               Compute scores object
     * @param arenaLeagueCollection collection of arena ids
     * @param swissLeagueCollection collection of swiss ids
     */

    public void startComputingScores(SlashCommandInteractionEvent event, ComputeScores compute,
                                     MongoCollection<Document> arenaLeagueCollection, MongoCollection<Document> swissLeagueCollection) throws ChessComPubApiException, IOException {
        if (!Slow_down_buddy.checkSpam(event)) {
            if (isDiscordAdmin(event)) {
                String urlarena = event.getOption("arena-url").getAsString();

                event.reply("Computing.. Please wait for 5 mins").queue();

                event.getChannel()
                        .sendMessage(
                                compute.calculatePlayerScores(urlarena, arenaLeagueCollection, swissLeagueCollection))
                        .queue();
            } else {
                event.reply("Sorry! You are not an admin!").queue();
            }
        } else {
            event.reply("Slow down Admin ;) Try again in 1 min").setEphemeral(true).queue();
        }
    }

    /**
     * Discord action to start user verification flow
     *
     * @param event      Slash command event
     * @param passport   Verification object
     * @param collection collection of players
     */

    public void startVerificationProcess(SlashCommandInteractionEvent event, Verification passport,
                                         MongoCollection<Document> collection) throws ChessComPubApiException, IOException {
        if (Slow_down_buddy.checkSpam(event)) {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after a 1 min!").setEphemeral(true).queue();
        }else{
           vertificationManager.startVerificationProcessLichess(event,passport,collection);
        }
    }


    public void startVerificationProcessChessCom(SlashCommandInteractionEvent event, Verification passport,
                                         MongoCollection<Document> collection) throws ChessComPubApiException, IOException {
        if (Slow_down_buddy.checkSpam(event)) {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after a 1 min!").setEphemeral(true).queue();
        }else{
           vertificationManager.startVerificationProcessChessCom(event,passport,collection);
        }
    }

    /**
     * Discord action to send ChessDojo URL based on if user is verified
     *
     * @param event      Slash command event
     * @param passport   Verification object
     * @param collection collection of players
     */

    public void leagueRegister(SlashCommandInteractionEvent event, Verification passport,
                               MongoCollection<Document> collection) {
        if(!Slow_down_buddy.checkSpam(event)) {
            configLeagueManager.leagueRegister(event, passport, collection);
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }
    }

    /**
     * Discord Action to look up user profile based on verification object
     *
     * @param passport   Verification object
     * @param event      Slash command event
     * @param collection collection of players
     * @param client     Lichess java client
     * @param helper     Helper object
     */

    public void lookUpProfile(Verification passport, SlashCommandInteractionEvent event,
                              MongoCollection<Document> collection, Client client, Helper helper) {
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


    public void lookupProfileChessCom(Verification passport, SlashCommandInteractionEvent event, MongoCollection<Document> collection){
        if(!Slow_down_buddy.checkSpam(event)){
            if(!passport.userPresentNormalChesscom(collection, event.getUser().getId())){
                event.reply("You have not verified your Chess.com account!").setEphemeral(true).queue();
            }else{
                String name = passport.getReletatedChessName(event.getUser().getId(), collection);
                CCProfile profile = new CCProfile(name);
                event.replyEmbeds(profile.getCCProfile().build()).queue();
            }
        }else{
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }
    }

    /**
     * Gets standings for Lichess URL
     *
     * @param event  Slash command event
     * @param client Lichess java client
     */

    public void getStandingsForURL(SlashCommandInteractionEvent event, Client client) {
        if (!Slow_down_buddy.checkSpam(event)) {
            String targetUrl = Objects.requireNonNull(event.getOption("tournament-url")).getAsString();
            if (targetUrl.contains("https://lichess.org/tournament/")
                    || targetUrl.contains("https://lichess.org/swiss/")) {
                UserArena base = new UserArena(client, targetUrl);
                event.replyEmbeds(base.getUserArena().build()).addActionRow(Button.link(targetUrl, "View On Lichess"))
                        .queue();

            } else {
                event.reply("Error! URL must be a Lichess Swiss or Arena URL").setEphemeral(true).queue();
            }
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }
    }

    /**
     * Get pairings URL
     *
     * @param event Slash command event
     */

    public void getPairingsReact(SlashCommandInteractionEvent event) {
        if (!Slow_down_buddy.checkSpam(event)) {
            String url = Objects.requireNonNull(event.getOption("request-url")).getAsString();
            if (url.contains("https://lichess.org/tournament/") || url.contains("https://lichess.org/swiss/")) {
                event.reply("View Pairings on Lichess!").addActionRow(Button.link(url, "View Pairings")).queue();
            } else {
                event.reply("Please provide valid URL!").setEphemeral(true).queue();
            }
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }
    }

    /**
     * Discord action to fire player ranks
     *
     * @param event      Slash command event
     * @param compute    Compute scores object
     * @param collection collection of players
     */

    public void getRankReact(SlashCommandInteractionEvent event, ComputeScores compute,
                             MongoCollection<Document> collection) {
        if (!Slow_down_buddy.checkSpam(event)) {
            event.reply("generating..").setEphemeral(true).queue();
            event.getChannel().sendMessageEmbeds(compute.getPlayerRankCard(event.getUser().getId(), collection).build())
                    .queue();
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }
    }

    /**
     * Discord action to fire player scores
     *
     * @param event      Slash command event
     * @param compute    Compute scores object
     * @param collection collection of players
     */

    public void getScoreReact(SlashCommandInteractionEvent event, ComputeScores compute,
                              MongoCollection<Document> collection) {
        if (!Slow_down_buddy.checkSpam(event)) {
            event.reply("generating..").setEphemeral(true).queue();
            event.getChannel().sendMessageEmbeds(compute.getPlayerScore(event.getUser().getId(), collection).build())
                    .queue();
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").setEphemeral(true).queue();
        }
    }

    /**
     * Sends 3 leaderboards into #general chat at 1:00 PM everyday
     *
     * @param jda        JDA object
     * @param channelId  Channel ID
     * @param collection collection of players
     */

    public void sendStandingEmbeds(JDA jda, String channelId, MongoCollection<Document> collection) {

        ComputeStandings standings = new ComputeStandings();
        TextChannel channel = jda.getTextChannelById(channelId);

        if (channel != null) {
            channel.sendMessageEmbeds(standings.calculateBlitzCombTotalGPStandings(collection).build()).queue();
            channel.sendMessageEmbeds(standings.calculateRapidCombTotalGPStandings(collection).build()).queue();
            channel.sendMessageEmbeds(standings.calculateClassicalCombTotalGPStandings(collection).build()).queue();

        } else {
            System.out.println("Channel not found or bot does not have access to the channel with ID: " + channelId);
        }
    }

    /**
     * Sends top 10 for blitz, rapid, classical ratings
     *
     * @param event      Slash command event
     * @param collection collection of players
     * @param standings  Standings object
     */

    public void sendTop10(SlashCommandInteractionEvent event, MongoCollection<Document> collection,
                          MongoCollection<Document> cc,
                          ComputeStandings standings) {
        if (!Slow_down_buddy.checkSpam(event)) {
            switch (event.getOptionsByName("select-site").get(0).getAsString()) {
             case "li" -> {
                 switch (event.getOptionsByName("select-tc").get(0).getAsString()) {

                     case "btc" -> {
                         event.reply("generating..").setEphemeral(true).queue();
                         event.getChannel()
                                 .sendMessageEmbeds(standings.getTop10Leaderboard(collection, Time_Control.BLITZ, "Lichessname").build())
                                 .queue();
                     }

                     case "rtc" -> {
                         event.reply("generating..").setEphemeral(true).queue();
                         event.getChannel()
                                 .sendMessageEmbeds(standings.getTop10Leaderboard(collection, Time_Control.RAPID, "Lichessname").build())
                                 .queue();
                     }

                     case "ctc" -> {
                         event.reply("generating..").setEphemeral(true).queue();
                         event.getChannel().sendMessageEmbeds(
                                 standings.getTop10Leaderboard(collection, Time_Control.CLASSICAL, "Lichessname").build()).queue();
                     }

                 }
             }

             case "cc" -> {
                 switch (event.getOptionsByName("select-tc").get(0).getAsString()) {

                     case "btc" -> {
                         event.reply("generating..").setEphemeral(true).queue();
                         event.getChannel()
                                 .sendMessageEmbeds(standings.getTop10Leaderboard(cc, Time_Control.BLITZ, "Chesscomname").build())
                                 .queue();
                     }

                     case "rtc" -> {
                         event.reply("generating..").setEphemeral(true).queue();
                         event.getChannel()
                                 .sendMessageEmbeds(standings.getTop10Leaderboard(cc, Time_Control.RAPID, "Chesscomname").build())
                                 .queue();
                     }

                     case "ctc" -> event.reply("Chess.com rapid is equal to rapid! Please select Rapid time control!").setEphemeral(true).queue();

                 }
             }

            }
        } else {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after 1 min!").queue();
        }

    }

    public void sendLeagueHelp(SlashCommandInteractionEvent event, Helper helper) {
        if (isDiscordAdmin(event)) {
            helper.getLeagueHelp(event);
        } else {
            event.reply("You are not an admin!").queue();
        }
    }

    /**
     * Generates a list of the next few upcoming arena and swiss tournaments in
     * response to a slash command ran by admins.
     *
     * @param event The slash command that kicked off the next() request.
     */
    public void next(SlashCommandInteractionEvent event) {
        List<EmbedBuilder> embeds = next();
        event.deferReply(true).queue();
        event.getChannel().sendMessageEmbeds(embeds.get(0).build()).queue();
        event.getChannel().sendMessageEmbeds(embeds.get(1).build()).queue();
    }

    /**
     * Generates a list of the next few upcoming arenas and swiss tournaments in
     * response to bot messages.
     *
     * @param event The message that kicked off the next() request.
     */
    public void next(MessageReceivedEvent event) {
        List<EmbedBuilder> embeds = next();
        event.getChannel().sendMessageEmbeds(embeds.get(0).build()).queue();
        event.getChannel().sendMessageEmbeds(embeds.get(1).build()).queue();
    }

    /**
     * Generates a list of the next few upcoming arena and swiss tournaments, and
     * returns them in two EmbedBuilders.
     *
     * @return A list of two EmbedBuilders. The first contains the arenas, and the
     *         second contains the swisses.
     */
    private List<EmbedBuilder> next() {
        final int MAX_ARENAS = 5;
        final int MAX_SWISSES = 5;

        StringBuilder arenaResult = new StringBuilder();
        StringBuilder swissResult = new StringBuilder();

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        Client client = Client.basic();
        List<Tournament> arenaList = new ArrayList<>(client.teams()
                .arenaByTeamId("chessdojo")
                .stream()
                .filter(t -> t.startsTime().isAfter(now))
                .toList());



        List<Swiss> swissList = new ArrayList<>(client.teams()
                .swissByTeamId("chessdojo")
                .stream()
                .filter(s -> ZonedDateTime.parse(s.startsAt()).isAfter(now))
                .toList());

        Collections.reverse(arenaList);
        Collections.reverse(swissList);

        for (int i = 0; i < MAX_ARENAS && i < arenaList.size(); i++) {
            long epochTime = arenaList.get(i).startsTime().toEpochSecond();
            arenaResult.append("**").append(arenaList.get(i).fullName()).append("**")
                    .append("\n")
                    .append("<t:").append(epochTime).append(":f>")
                    .append("\n")
                    .append("[**Join**](").append("https://lichess.org/tournament/").append(arenaList.get(i).id())
                    .append(")\n");
        }

        for (int i = 0; i < MAX_SWISSES && i < swissList.size(); i++) {
            long epochTime = ZonedDateTime.parse(swissList.get(i).startsAt()).toEpochSecond();
            swissResult.append("**").append(swissList.get(i).name()).append("**")
                    .append("\n")
                    .append("<t:").append(epochTime).append(":f>")
                    .append("\n")
                    .append("[**Join**](").append("https://lichess.org/swiss/").append(swissList.get(i).id())
                    .append(")\n");
        }

        EmbedBuilder arenaEmbed = new EmbedBuilder();
        arenaEmbed.setTitle("Upcoming Arenas");
        arenaEmbed.setColor(Color.green);
        arenaEmbed.setThumbnail(Helper.DOJO_LOGO);
        arenaEmbed.setDescription(arenaResult.toString());
        arenaEmbed.setFooter("All times are in your local timezone");

        EmbedBuilder swissEmbed = new EmbedBuilder();
        swissEmbed.setTitle("Upcoming Swiss");
        swissEmbed.setColor(Color.BLUE);
        swissEmbed.setThumbnail(Helper.DOJO_LOGO);
        swissEmbed.setDescription(swissResult.toString());
        swissEmbed.setFooter("All times are in your local timezone");

        ArrayList<EmbedBuilder> result = new ArrayList<>();
        result.add(arenaEmbed);
        result.add(swissEmbed);
        return result;
    }

    public void inject(SlashCommandInteractionEvent event, MongoCollection<Document> arena,
                       MongoCollection<Document> swiss) {
        if (isDiscordAdmin(event)) {
            String url = Objects.requireNonNull(event.getOption("url")).getAsString();
            event.reply("Connecting..").queue();

            if (url.contains("https://lichess.org/tournament/")) {
                String[] spliturl = url.split("tournament/");
                String touryID = spliturl[1];
                DbTournamentEntry entry = new DbTournamentEntry(touryID, touryID);
                Document document = new Document("Name", entry.getTournamentName())
                        .append("Id", entry.getLichessTournamentId());
                arena.insertOne(document);
                DojoScoreboard.createTournament(url);
                event.getChannel().sendMessage("Success! Injected URL " + url + " In the database and the site!")
                        .queue();
            } else if (url.contains("https://lichess.org/swiss/")) {
                String[] spliturl = url.split("swiss/");
                String touryID = spliturl[1];
                DbTournamentEntry entry = new DbTournamentEntry(touryID, touryID);
                Document document = new Document("Name", entry.getTournamentName())
                        .append("Id", entry.getLichessTournamentId());
                swiss.insertOne(document);
                DojoScoreboard.createTournament(url);
                event.getChannel().sendMessage("Success! Injected URL " + url + " In the database and the site!")
                        .queue();


            } else if (url.contains("https://www.chess.com/tournament/live/arena/")){
                String[] spliturl = url.split("arena/");
                String touryID = spliturl[1];
                DbTournamentEntry entry = new DbTournamentEntry(touryID, touryID);
                Document document = new Document("Name", entry.getTournamentName())
                        .append("Id", entry.getLichessTournamentId())
                                .append("arenacc", entry.getLichessTournamentId());
                arena.insertOne(document);
                DojoScoreboard.createTournament(url);
                event.getChannel().sendMessage("Success! Injected URL " + url + " In the database and the site!")
                        .queue();
            }

            else if (url.contains("https://www.chess.com/tournament/live/")){
                String[] spliturl = url.split("live/");
                String touryID = spliturl[1];
                DbTournamentEntry entry = new DbTournamentEntry(touryID, touryID);
                Document document = new Document("Name", entry.getTournamentName())
                        .append("Id", entry.getLichessTournamentId())
                                .append("swisscc", entry.getLichessTournamentId());
                swiss.insertOne(document);
                DojoScoreboard.createTournament(url);
                event.getChannel().sendMessage("Success! Injected URL " + url + " In the database and the site!")
                        .queue();
            }else if (url.contains("https://www.chess.com/play/arena/")){
                event.getChannel().sendMessage("Error! The current chess.com arena URL is shareable URL, please inject tournament URL when the tournament is finished!").queue();
            }else if(url.contains("https://www.chess.com/play/tournament/")){
                event.getChannel().sendMessage("Error! The current chess.com swiss URL is shareable URL, please inject tournament URL when the tournament is finished!").queue();

            }
            else {
                event.getChannel().sendMessage("Error! Injection failed.. Please check the URL").queue();
            }

        } else {
            event.reply("Error! Sorry you are not an admin!").setEphemeral(true).queue();
        }

    }



    public void automaticCCComputeScores(MessageReceivedEvent event, ComputeScorescc computecc, MongoCollection<Document> arenacc, MongoCollection<Document> swisscc) throws ChessComPubApiException, IOException {

        List<String> swissids = ComputeScorescc.compareCollections(swisscc, Main.computedId, "swisscc");

        swissids.removeIf(Objects::isNull);


        List<String> arenaids = ComputeScorescc.compareCollections(arenacc, Main.computedId, "arenacc");

        arenaids.removeIf(Objects::isNull);


        if(arenaids.isEmpty() && swissids.isEmpty()){
            event.getChannel().sendMessage("No Chess.com arena and swiss tournament found to be computed").queue();
        }else if(arenaids.isEmpty()){

            event.getChannel().sendMessage("Computing Chess.com swiss tournaments of size: " + swissids.size()).queue();

            for(String id: swissids){
                String url = "https://www.chess.com/tournament/live/"+ id;
                String result = computecc.calculatePlayerScores(url, arenacc, swisscc);
                event.getChannel().sendMessage(result).queue();
            }

        }else if(swissids.isEmpty()){
            event.getChannel().sendMessage("Computing Chess.com arena tournaments of size: " + arenaids.size()).queue();

            for(String id: arenaids){
                String url = "https://www.chess.com/tournament/live/arena/"+ id;
                String result = computecc.calculatePlayerScores(url, arenacc, swisscc);
                event.getChannel().sendMessage(result).queue();
            }

        }else{

            event.getChannel().sendMessage("Computing Chess.com swiss & arena tournaments of size: " + swissids.size() + swissids.size()).queue();

            for(String id: swissids){
                String url = "https://www.chess.com/tournament/live/"+ id;
                String result = computecc.calculatePlayerScores(url, arenacc, swisscc);
                event.getChannel().sendMessage(result).queue();
            }

            for(String id: arenaids){
                String url = "https://www.chess.com/tournament/live/arena/"+ id;
                String result = computecc.calculatePlayerScores(url, arenacc, swisscc);
                event.getChannel().sendMessage(result).queue();
            }

        }



    }



    /**
     * Automatically computes scores for any finished tournaments that haven't
     * already been computed.
     *
     * @param event           The message that kicked off the
     *                        automaticComputeScores() event.
     * @param compute         The ComputeScores object to use when calculating
     *                        player scores.
     * @param arenaCollection The collection of Arena tournaments in the database.
     * @param swissCollection The collection of Swiss tournaments in the database.
     */
    public void automaticComputeScores(MessageReceivedEvent event, ComputeScores compute,
                                       MongoCollection<Document> arenaCollection, MongoCollection<Document> swissCollection) throws ChessComPubApiException, IOException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));

        List<String> tournamentURLs = new ArrayList<>();
        List<Tournament> arenaList = Client.basic().teams().arenaByTeamId("chessdojo", 100).stream().toList();
        List<Swiss> swissList = Client.basic().teams().swissByTeamId("chessdojo", 100).stream().toList();


        for (Tournament tournament : arenaList) {

            if(tournament.startsTime().getMonthValue() != now.getMonthValue() && tournament.startsTime().getYear() == tournament.startsTime().getYear() ){
                System.out.println("Arena was ignored due last month");
                continue;
            }

            if (tournament.startsTime().plusMinutes(tournament.minutes()).isAfter(now)) {
                System.out.println("Arena not finished: https://lichess.org/tournament/" + tournament.id());
                continue;
            }
            if (!ComputeScores.tournamentPresent(arenaCollection, tournament.id())) {
                System.out.println("Arena not in current League: https://lichess.org/tournament/" + tournament.id());
                continue;
            }
            if (ComputeScores.isTournamentIDresent(tournament.id(), Main.computedId)) {
                System.out.println("Arena already computed: https://lichess.org/tournament/" + tournament.id());
                continue;
            }

            tournamentURLs.add("https://lichess.org/tournament/" + tournament.id());
        }

        for (Swiss swiss : swissList) {

            if (!swiss.status().equalsIgnoreCase("finished")) {
                System.out.println("Swiss not finished: https://lichess.org/swiss/" + swiss.id());
                continue;
            }
            if (!ComputeScores.tournamentPresent(swissCollection, swiss.id())) {
                System.out.println("Swiss not in current League: https://lichess.org/swiss/" + swiss.id());
                continue;
            }
            if (ComputeScores.isTournamentIDresent(swiss.id(), Main.computedId)) {
                System.out.println("Swiss already computed: https://lichess.org/swiss/" + swiss.id());
                continue;
            }

            tournamentURLs.add("https://lichess.org/swiss/" + swiss.id());
        }

        if (tournamentURLs.isEmpty()) {
            event.getChannel().sendMessage("No new completed tournaments found. Scores cannot be computed.")
                    .queue();
            return;
        }

        event.getChannel().sendMessage(
                        "Computing scores for " + tournamentURLs.size() + " tournaments. Please wait for 10 to 15 mins...")
                .queue();

        for (String url : tournamentURLs) {
            String result = compute.calculatePlayerScores(url, arenaCollection, swissCollection);
            event.getChannel().sendMessage(result).queue();
        }
    }





    public void computeLigaWinners(SlashCommandInteractionEvent event, WinnerPolicy policy){
        if(isDiscordAdmin(event)) {
            event.reply("Computing Please wait!").queue(msg ->
            {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    int count = 0;

                    @Override
                    public void run() {
                        msg.editOriginal("Loading" + " beep beep boop boop ".repeat(count % 4)).queue();
                        count++;
                        if (count > 20) {
                            timer.cancel();
                        }
                    }
                }, 0, 1000);


            });
            switch (event.getOptionsByName("time-picker").get(0).getAsString()) {
                case "bz" -> {
                    switch (event.getOptionsByName("type-picker").get(0).getAsString()) {
                        case "gp" -> {
                            switch (event.getOptionsByName("year-picker").get(0).getAsString()){

                                case "2024" -> {
                                    switch (event.getOptionsByName("month-picker").get(0).getAsString()){

                                        case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 1, 2024)).queue();

                                        case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 2, 2024)).queue();

                                        case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 3, 2024)).queue();

                                        case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 4, 2024)).queue();

                                        case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 5, 2024)).queue();

                                        case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 6, 2024)).queue();

                                        case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 7, 2024)).queue();

                                        case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 8, 2024)).queue();

                                        case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 9, 2024)).queue();

                                        case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 10, 2024)).queue();

                                        case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 11, 2024)).queue();

                                        case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 12, 2024)).queue();


                                    }

                                }

                                case "2023" -> {
                                    switch (event.getOptionsByName("month-picker").get(0).getAsString()){
                                        case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 1, 2023)).queue();

                                        case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 2, 2023)).queue();

                                        case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 3, 2023)).queue();

                                        case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 4, 2023)).queue();

                                        case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 5, 2023)).queue();

                                        case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 6, 2023)).queue();

                                        case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 7, 2023)).queue();

                                        case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 8, 2023)).queue();

                                        case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 9, 2023)).queue();

                                        case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 10, 2023)).queue();

                                        case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 11, 2023)).queue();

                                        case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.COMB_GRAND_PRIX, 12, 2023)).queue();



                                    }

                                }

                            }
                        }

                        case "ar" -> {
                            switch (event.getOptionsByName("year-picker").get(0).getAsString()){
                                case "2024" -> {
                                    switch (event.getOptionsByName("month-picker").get(0).getAsString()){

                                        case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 1, 2024)).queue();

                                        case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 2, 2024)).queue();

                                        case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 3, 2024)).queue();

                                        case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 4, 2024)).queue();

                                        case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 5, 2024)).queue();

                                        case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 6, 2024)).queue();

                                        case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 7, 2024)).queue();

                                        case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 8, 2024)).queue();

                                        case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 9, 2024)).queue();

                                        case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 10, 2024)).queue();

                                        case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 11, 2024)).queue();

                                        case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 12, 2024)).queue();


                                    }

                                }

                                case "2023" -> {
                                    switch (event.getOptionsByName("month-picker").get(0).getAsString()){
                                        case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 1, 2023)).queue();

                                        case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 2, 2023)).queue();

                                        case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 3, 2023)).queue();

                                        case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 4, 2023)).queue();

                                        case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 5, 2023)).queue();

                                        case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 6, 2023)).queue();

                                        case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 7, 2023)).queue();

                                        case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 8, 2023)).queue();

                                        case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 9, 2023)).queue();

                                        case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 10, 2023)).queue();

                                        case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 11, 2023)).queue();

                                        case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.ARENA, 12, 2023)).queue();



                                    }

                                }
                            }

                        }
                        case "sw" -> {
                            switch (event.getOptionsByName("year-picker").get(0).getAsString()){
                                case "2024" -> {
                                    switch (event.getOptionsByName("month-picker").get(0).getAsString()){

                                        case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 1, 2024)).queue();

                                        case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 2, 2024)).queue();

                                        case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 3, 2024)).queue();

                                        case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 4, 2024)).queue();

                                        case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 5, 2024)).queue();

                                        case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 6, 2024)).queue();

                                        case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 7, 2024)).queue();

                                        case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 8, 2024)).queue();

                                        case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 9, 2024)).queue();

                                        case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 10, 2024)).queue();

                                        case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 11, 2024)).queue();

                                        case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 12, 2024)).queue();


                                    }

                                }

                                case "2023" -> {
                                    switch (event.getOptionsByName("month-picker").get(0).getAsString()){
                                        case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 1, 2023)).queue();

                                        case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 2, 2023)).queue();

                                        case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 3, 2023)).queue();

                                        case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 4, 2023)).queue();

                                        case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 5, 2023)).queue();

                                        case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 6, 2023)).queue();

                                        case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 7, 2023)).queue();

                                        case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ,Type.SWISS, 8, 2023)).queue();

                                        case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 9, 2023)).queue();

                                        case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 10, 2023)).queue();

                                        case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 11, 2023)).queue();

                                        case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.BLITZ, Type.SWISS, 12, 2023)).queue();
                                    }

                                }
                            }

                        }
                    }
                }

                case "ra" -> {

                        switch (event.getOptionsByName("type-picker").get(0).getAsString()) {
                            case "gp" -> {
                                switch (event.getOptionsByName("year-picker").get(0).getAsString()){

                                    case "2024" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){

                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 1, 2024)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 2, 2024)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 3, 2024)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 4, 2024)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 5, 2024)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 6, 2024)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 7, 2024)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 8, 2024)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 9, 2024)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 10, 2024)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 11, 2024)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 12, 2024)).queue();


                                        }

                                    }

                                    case "2023" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){
                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 1, 2023)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 2, 2023)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 3, 2023)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 4, 2023)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 5, 2023)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 6, 2023)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 7, 2023)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 8, 2023)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 9, 2023)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 10, 2023)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 11, 2023)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.COMB_GRAND_PRIX, 12, 2023)).queue();



                                        }

                                    }

                                }
                            }

                            case "ar" -> {
                                switch (event.getOptionsByName("year-picker").get(0).getAsString()){
                                    case "2024" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){

                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 1, 2024)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 2, 2024)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 3, 2024)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 4, 2024)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 5, 2024)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 6, 2024)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 7, 2024)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 8, 2024)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 9, 2024)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 10, 2024)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 11, 2024)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 12, 2024)).queue();


                                        }

                                    }

                                    case "2023" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){
                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 1, 2023)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 2, 2023)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 3, 2023)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 4, 2023)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 5, 2023)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 6, 2023)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 7, 2023)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 8, 2023)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 9, 2023)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 10, 2023)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 11, 2023)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.ARENA, 12, 2023)).queue();



                                        }

                                    }
                                }

                            }
                            case "sw" -> {
                                switch (event.getOptionsByName("year-picker").get(0).getAsString()){
                                    case "2024" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){

                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 1, 2024)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 2, 2024)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 3, 2024)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 4, 2024)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 5, 2024)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 6, 2024)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 7, 2024)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 8, 2024)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 9, 2024)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 10, 2024)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 11, 2024)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 12, 2024)).queue();


                                        }

                                    }

                                    case "2023" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){
                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 1, 2023)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 2, 2023)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 3, 2023)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 4, 2023)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 5, 2023)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 6, 2023)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 7, 2023)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID,Type.SWISS, 8, 2023)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 9, 2023)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 10, 2023)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 11, 2023)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.RAPID, Type.SWISS, 12, 2023)).queue();
                                        }

                                    }
                                }

                            }
                        }
                    }

                    case "cla" -> {

                        switch (event.getOptionsByName("type-picker").get(0).getAsString()) {
                            case "gp" -> {
                                switch (event.getOptionsByName("year-picker").get(0).getAsString()){

                                    case "2024" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){

                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 1, 2024)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 2, 2024)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 3, 2024)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 4, 2024)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 5, 2024)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 6, 2024)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 7, 2024)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 8, 2024)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 9, 2024)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 10, 2024)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 11, 2024)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 12, 2024)).queue();


                                        }

                                    }

                                    case "2023" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){
                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 1, 2023)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 2, 2023)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 3, 2023)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 4, 2023)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 5, 2023)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 6, 2023)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 7, 2023)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 8, 2023)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 9, 2023)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 10, 2023)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 11, 2023)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.COMB_GRAND_PRIX, 12, 2023)).queue();



                                        }

                                    }

                                }
                            }

                            case "ar" -> {
                                switch (event.getOptionsByName("year-picker").get(0).getAsString()){
                                    case "2024" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){

                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 1, 2024)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 2, 2024)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 3, 2024)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 4, 2024)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 5, 2024)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 6, 2024)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 7, 2024)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 8, 2024)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 9, 2024)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 10, 2024)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 11, 2024)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 12, 2024)).queue();


                                        }

                                    }

                                    case "2023" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){
                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 1, 2023)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 2, 2023)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 3, 2023)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 4, 2023)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 5, 2023)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 6, 2023)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 7, 2023)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 8, 2023)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 9, 2023)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 10, 2023)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 11, 2023)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.ARENA, 12, 2023)).queue();



                                        }

                                    }
                                }

                            }
                            case "sw" -> {
                                switch (event.getOptionsByName("year-picker").get(0).getAsString()){
                                    case "2024" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){

                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 1, 2024)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 2, 2024)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 3, 2024)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 4, 2024)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 5, 2024)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 6, 2024)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 7, 2024)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 8, 2024)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 9, 2024)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 10, 2024)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 11, 2024)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 12, 2024)).queue();


                                        }

                                    }

                                    case "2023" -> {
                                        switch (event.getOptionsByName("month-picker").get(0).getAsString()){
                                            case "jan" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 1, 2023)).queue();

                                            case "feb" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 2, 2023)).queue();

                                            case "marc" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 3, 2023)).queue();

                                            case "apr" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 4, 2023)).queue();

                                            case "may" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 5, 2023)).queue();

                                            case "june" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 6, 2023)).queue();

                                            case "july" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 7, 2023)).queue();

                                            case "aug" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL,Type.SWISS, 8, 2023)).queue();

                                            case "sep" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 9, 2023)).queue();

                                            case "oct" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 10, 2023)).queue();

                                            case "nov" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 11, 2023)).queue();

                                            case "dec" -> event.getChannel().sendMessage(policy.findWinner(Time_Control.CLASSICAL, Type.SWISS, 12, 2023)).queue();
                                        }

                                    }
                                }

                            }
                        }



                    }




            }

        }else{
            event.reply("Your not an admin!").queue();
        }
    }



    public void createEntryTicket(SlashCommandInteractionEvent event){
        ticketSystem.createEntryTicket(event);
    }



    public void ticketFormSystem(ModalInteractionEvent event, String senseiChannelId, String techChannelID, String tpchannel){
       ticketSystem.ticketFormSystem(event,senseiChannelId,techChannelID,tpchannel);

    }



    public void sentTheForms(ButtonInteraction event){
       ticketSystem.sentTheForms(event);
    }


    public void computeChessDojoLichessLigaScores(MessageReceivedEvent event, MongoCollection<Document> arena, MongoCollection<Document> swiss, ComputeScores compute){
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));

        List<Tournament> list = new ArrayList<>(Client.basic().teams().arenaByTeamId("chessdojo", 50).stream().filter(tournament -> tournament.fullName().contains("Lichess Liga")).filter(tournament -> tournament.finishesTime().getMonthValue() == now.getMonthValue()).toList());


        event.getChannel().sendMessage("Computing Lichess Dojo Liga tournaments of size " + list.size() + "  Please wait!").queue();

        for(Tournament t: list){
         String res = compute.calculateLichessLigaScores("https://lichess.org/tournament/" + t.id(), arena);
         event.getChannel().sendMessage(res).queue();
        }
    }


    public void sendLigaMessage(MessageReceivedEvent event){
              if(isDiscordAdminMessage(event)){
                  Messenger messenger = new Messenger();
                  messenger.sendMessage();
                  event.getChannel().sendMessage("I have successfully notified Lichess team! Good luck to everyone playing!").queue();
              }else{
                  event.getChannel().sendMessage("Error! Automated Message have been shut of, please call admin to send the messages!").queue();
              }


    }


    public void unlinkeUserBelt(SlashCommandInteractionEvent event){
        ManageRoles.removePreviousRoles(event.getGuild(), Objects.requireNonNull(event.getMember()));
        event.reply("Successfully unlinked belt for " + event.getMember().getUser().getAsMention()).queue();
    }

}