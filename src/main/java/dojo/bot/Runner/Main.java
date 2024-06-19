package dojo.bot.Runner;

import chariot.Client;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dojo.bot.Controller.Discord.Helper;
import dojo.bot.Controller.User.Verification;
import dojo.bot.Controller.CalculateScores.ComputeScores;
import dojo.bot.Controller.CalculateScores.ComputeScorescc;
import dojo.bot.Controller.Discord.DiscordAction;
import dojo.bot.Controller.Discord.ManageRoles;
import dojo.bot.Controller.Standing.ComputeStandings;
import dojo.bot.Controller.Winner.WinnerPolicy;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class Main extends ListenerAdapter {

    private static JDA jda;
    public static MongoCollection<Document> collection;

    public final static boolean IS_BETA = true;


    public static MongoCollection<Document> chesscomplayers;

    public static MongoCollection<Document> computedId;

    public static MongoCollection<Document> u1800Playerswiss;

    public static MongoCollection<Document> u1800swissCollection;

    public DiscordAction DiscordReactor = new DiscordAction();

    private static MongoCollection<Document> swissLeagueCollection;
    public static MongoCollection<Document> arenaLeagueCollection;

    private static final Client client = chariot.Client.basic();
    public static final String botToken = System.getenv("Lichess_bot_token");



    public static void main(String[] args) {

        String beta = System.getenv("Beta_token");
        String prod = System.getenv("Prod_token");

        JDABuilder jdaBuilder = JDABuilder.createDefault(IS_BETA ? beta: prod).enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS);
        jdaBuilder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);

        jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        jdaBuilder.setActivity(Activity.playing("V7.4.8 Date: April 24 2024 Author: Noobmaster "));

        jdaBuilder.addEventListeners(new Main());

        try {
            jda = jdaBuilder.build();

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(Commands.slash("leagueregister", "Request joining ChessDojo league URL"));
        commands.addCommands(Commands.slash("ticket", "Post ticket information"));
        commands.addCommands(Commands.slash("leaguehelp", "View League Config inputs help"));
        commands.addCommands(Commands.slash("computescores", "Compute Arena and Swiss scores for specific Arena/Swiss URL")
                .addOption(OptionType.STRING, "arena-url", "Input Lichess arena URL which is in the league", true));
        commands.addCommands(Commands.slash("pairings", "View pairings for the time command is run!")
                .addOption(OptionType.STRING, "request-url", "Input Lichess arena/swiss URL.", true));
        commands.addCommands(Commands.slash("standing", "View Lichess Arena/Swiss standings")
                .addOption(OptionType.STRING, "tournament-url", "Input Lichess Arena/Swiss URL", true));
        commands.addCommands(Commands.slash("leagueconfigarena", "Create an arena league")
                .addOption(OptionType.STRING, "league-name", "Enter league name ", true)
                .addOption(OptionType.STRING, "league-desc", "Enter league short description", true)
                        .addOption(OptionType.STRING, "arena-fen", "Provide fen for league arenas", true)
                .addOption(OptionType.INTEGER, "tournament-count", "Enter number of tournaments within a league Daily (1 to 12), Weekly (1 to 12), Monthly (1 to 12)", true)
                        .addOption(OptionType.INTEGER, "max-rating", "Enter max rating for players to join, 1400 for U1400 (800 to 2200),for open tournaments enter 0", true)
                        .addOption(OptionType.INTEGER, "time-start", "Enter time start in 24 hour clock (0 to 23)", true)
                        .addOption(OptionType.INTEGER, "clock-time", "Enter tournament clock time control (2 to 60)", true)
                        .addOption(OptionType.INTEGER, "clock-increment", "Enter tournament clock increment (0 to 60)", true)
                        .addOption(OptionType.INTEGER, "duration", "Enter tournament duration in mins (20 to 720)", true)
                .addOptions(new OptionData(OptionType.STRING, "interval", "Select Interval", true).addChoice("Daily", "daily")
                        .addChoice("Weekly", "weekly")
                        .addChoice("Monthly", "monthly"))
                .addOptions(new OptionData(OptionType.STRING, "day-of-week", "Select Day of week for (weekly/monthly) League", true)
                        .addChoice("Monday", "mon")
                        .addChoice("Tuesday", "tue")
                        .addChoice("Wednesday", "wed")
                        .addChoice("Thursday", "th")
                        .addChoice("Friday", "fri")
                        .addChoice("Saturday", "sat")
                        .addChoice("Sunday", "sun")));
        commands.addCommands(Commands.slash("help", "view commands for DojoLigaBot"));
        commands.addCommands(Commands.slash("profile", "View Lichess Profile stats"));
        commands.addCommands(Commands.slash("verify", "Verify your Lichess account").addOption(OptionType.STRING, "lichess-username", "your Lichess account username", true));
        commands.addCommands(Commands.slash("displaystandings", "View Overall League Standings").addOptions(new OptionData(OptionType.STRING, "time-control", "Select Time Control", true)
                        .addChoice("Blitz", "blitz-tc")
                        .addChoice("Rapid", "rapid-tc")
                        .addChoice("Classical", "classical-tc")
                        .addChoice("Sparring", "sp-tc"))
                .addOptions(new OptionData(OptionType.STRING, "point-type", "Select Point Type", true)
                        .addChoice("Arena Total Points", "arena-total-points")
                        .addChoice("Swiss Total Points", "swiss-total-points")
                        .addChoice("Combined Grand Prix Points", "comb-gp-points")
                        .addChoice("Middlegame Sparring Points", "sp-points")
                        .addChoice("Endgame Sparring Points", "eg-points")));
        commands.addCommands(Commands.slash("score", "View your scores across all leagues"));
        commands.addCommands(Commands.slash("rank", "View your ranks across all leagues"));
        commands.addCommands(Commands.slash("leagueconfigswiss", "Create an arena league")
                .addOption(OptionType.STRING, "league-name-s", "Enter league name ", true)
                .addOption(OptionType.STRING, "league-desc-s", "Enter league short description", true)
                        .addOption(OptionType.STRING, "swiss-fen", "Provide fen for league swiss", true)
                        .addOption(OptionType.INTEGER, "max-rating-swiss", "Enter max rating for players to join, 1400 for U1400 (800 to 2200),for open tournaments enter 0", true)
                .addOption(OptionType.INTEGER, "tournament-count-s", "Enter number of tournaments within a league Daily (1 to 12), Weekly (1 to 12), Monthly (1 to 12)", true)
                .addOption(OptionType.INTEGER, "time-start-s", "Enter time start in 24 hour clock (0 to 23)", true)
                .addOption(OptionType.INTEGER, "clock-time-s", "Enter tournament clock time control (1 to 180)", true)
                .addOption(OptionType.INTEGER, "clock-increment-s", "Enter tournament clock increment (1 to 120)", true)
                .addOption(OptionType.INTEGER, "nb-rounds", "Enter swiss number of rounds (3 to 100)", true)
                        .addOption(OptionType.INTEGER, "round-interval", "Enter round duration in mins (1 to 10080)", true)
                .addOptions(new OptionData(OptionType.STRING, "interval-swiss", "Select Interval", true).addChoice("Daily", "daily-swiss")
                        .addChoice("Weekly", "weekly-swiss")
                        .addChoice("Monthly", "monthly-swiss"))
                .addOptions(new OptionData(OptionType.STRING, "day-of-week-swiss", "Select Day of week for (weekly/monthly) League", true)
                        .addChoice("Monday", "mon")
                        .addChoice("Tuesday", "tue")
                        .addChoice("Wednesday", "wed")
                        .addChoice("Thursday", "th")
                        .addChoice("Friday", "fri")
                        .addChoice("Saturday", "sat")
                        .addChoice("Sunday", "sun")));
        commands.addCommands(Commands.slash("search", "search a user's Lichess/Chesscom/Discord ids")
                .addOptions(new OptionData(OptionType.STRING, "query-mode", "Select query mode", true).addChoice("Lichess.org", "limode")
                        .addChoice("Chess.com", "ccmode")
                        .addChoice("Discord ID", "dimode")).addOption(OptionType.STRING, "value-search", "enter value", true));
        commands.addCommands(Commands.slash("standingshelp", "View Dojo League Standings info"));
        commands.addCommands(Commands.slash("stream", "View ChessDojo stream"));
        commands.addCommands(Commands.slash("update", "Update your Belts for your live Lichess.org and Chess.com account"));
        commands.addCommands(Commands.slash("top10", "View top10 players' ratings across Rapid, Blitz, Classical time control")
                        .addOptions(new OptionData(OptionType.STRING, "select-site", "Select Site", true)
                                .addChoice("Chess.com", "cc")
                                .addChoice("Lichess.org", "li"))
                .addOptions(new OptionData(OptionType.STRING, "select-tc", "Select Time Control", true)
                        .addChoice("Blitz", "btc")
                        .addChoice("Rapid", "rtc")
                        .addChoice("Classical", "ctc")));
       commands.addCommands(Commands.slash("chesscomprofile", "View Chess.com profile for given user"));
        commands.addCommands(Commands.slash("verifychesscom", "Verify your Chess.com account").addOption(OptionType.STRING, "chesscom-username", "your Chess.com account username", true));
       commands.addCommands(Commands.slash("next", "View Future Chess dojo liga tournaments"));
       commands.addCommands(Commands.slash("inject", "Admins inject tournament URL").addOption(OptionType.STRING, "url", "add arena/swiss url", true));
       commands.addCommands(Commands.slash("winners", "Admin compute possible winners").addOptions(new OptionData(OptionType.STRING, "time-picker", "Pick time control", true).addChoice("Blitz", "bz").addChoice(
               "Rapid",
               "ra"
       ).addChoice(
               "Classical",
               "cla"
       )).addOptions(
               new OptionData(
                       OptionType.STRING,
                       "type-picker",
                       "Pick point type",
                       true
               ).addChoice(
                       "Grand Prix",
                       "gp"
               ).addChoice(
                       "Arena",
                       "ar"
               ).addChoice(
                       "Swiss",
                       "sw"
               )
       ).addOptions(
               new OptionData(
                       OptionType.STRING,
                       "year-picker",
                       "Pick the year",
                       true
               ).addChoice("2024",
                       "2024")
                       .addChoice("2023", "2023")
       ).addOptions(new OptionData(
               OptionType.STRING,
               "month-picker",
               "Pick the Month",
               true
               ).addChoice("Jan", "jan")
                       .addChoice("Feb", "feb")
                       .addChoice("March", "marc")
                       .addChoice("April", "apr")
                       .addChoice("May", "may")
                       .addChoice("June", "june")
                       .addChoice("July", "july")
                       .addChoice("Aug", "aug")
                       .addChoice("Sep", "sep")
                       .addChoice("Oct", "oct")
                       .addChoice("Nov", "nov")
                       .addChoice("Dec", "dec")
               ));

        commands.addCommands(Commands.slash("unlink", "unlink your Dojo Belt"));
        commands.queue();





        String connectionString = System.getenv("connection_string");

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        MongoClient mongoClient = MongoClients.create(settings);

        MongoDatabase database = mongoClient.getDatabase(System.getenv("database_name"));

        collection =  database.getCollection(System.getenv("player_coll"));
        chesscomplayers = database.getCollection(System.getenv("chesscom_coll"));
        swissLeagueCollection = database.getCollection(System.getenv("swiss_coll"));
        arenaLeagueCollection = database.getCollection(System.getenv("arena_coll"));
        computedId = database.getCollection(System.getenv("comp_coll"));
        u1800Playerswiss = database.getCollection("u1800_coll");
        u1800swissCollection = database.getCollection("u1800_coll");



    }


    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if(!IS_BETA) {
            DiscordReactor.sendStandingEmbeds(jda, "1169386870849929246", collection);
        }
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        Verification passport = new Verification();
        Helper helper = new Helper();
        ComputeScores compute = new ComputeScores();
        ComputeStandings standings = new ComputeStandings();
        ManageRoles manageRoles = new ManageRoles();
        WinnerPolicy policy = new WinnerPolicy();

        switch (event.getName()){

            case "leaguehelp" -> DiscordReactor.sendLeagueHelp(event, helper);

            case "search" -> DiscordReactor.performSearch(event);
            
            case "update" -> {
                try {
                    manageRoles.startUpdatingRoles(event, passport);
                } catch (ChessComPubApiException e) {
                    event.reply("Internal bot error! Please contact Noobmaster!").queue();
                } catch (IOException e) {
                    event.reply("Error!").queue();
                }
            }


            case "stream" -> event.reply("https://www.twitch.tv/chessdojo").queue();

            case "standingshelp" -> helper.getStandingsHelp(event);

            case "rank" -> DiscordReactor.getRankReact(event, compute, collection);

            case "score" -> DiscordReactor.getScoreReact(event, compute, collection);

            case "displaystandings" -> DiscordReactor.StandingsReact(event,standings, collection);

            case "computescores" -> {
                try {
                    DiscordReactor.startComputingScores(event, compute, arenaLeagueCollection, swissLeagueCollection);
                } catch (ChessComPubApiException e) {
                    event.reply("Chess.com error!").queue();
                } catch (IOException e) {
                    event.reply("Internal Error!").queue();
                }
            }

            case "leagueconfigarena" -> DiscordReactor.configLeagueArena(event, arenaLeagueCollection);

            case "leagueconfigswiss" -> DiscordReactor.configLeagueSwiss(event, swissLeagueCollection);

            case "leagueregister" -> DiscordReactor.leagueRegister(event, passport, collection);

            case "verify"-> {
                try {
                    DiscordReactor.startVerificationProcess(event, passport, collection);
                } catch (ChessComPubApiException e) {
                    event.reply("Internal bot error! Please contact Noobmaster!").queue();
                } catch (IOException e) {
                    event.reply("Error!").queue();
                }
            }

            case "verifychesscom" -> {
                try {
                    DiscordReactor.startVerificationProcessChessCom(event, passport, chesscomplayers);
                } catch (ChessComPubApiException | IOException e) {
                    event.reply("Invalid Chess.com username! Please try again!").queue();
                }
            }

            case "help" -> helper.startHelper(event);

            case "profile" -> DiscordReactor.lookUpProfile(passport, event, collection, client, helper);

            case "standing" -> DiscordReactor.getStandingsForURL(event, client);

            case "pairings" -> DiscordReactor.getPairingsReact(event);

            case "top10" -> DiscordReactor.sendTop10(event, collection, chesscomplayers, standings);

            case "next" -> DiscordReactor.next(event);

            case "inject" -> DiscordReactor.inject(event, arenaLeagueCollection, swissLeagueCollection);

            case "chesscomprofile" -> DiscordReactor.lookupProfileChessCom(passport, event, chesscomplayers);

            case "ticket" -> DiscordReactor.createEntryTicket(event);

            case "winners" -> DiscordReactor.computeLigaWinners(event, policy);

            case "unlink" -> DiscordReactor.unlinkeUserBelt(event);

        }
    }


    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        DiscordReactor.sentTheForms(event);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(!IS_BETA) {
            DiscordReactor.ticketFormSystem(event, Keys.PROD_CHANNEL_ID_SENSEI, Keys.PROD_CHANNEL_ID_TECH, Keys.PROD_CHANNEL_ID_TP);
        }else{
            DiscordReactor.ticketFormSystem(event, Keys.BETA_CHANNEL_ID_SENSEI, Keys.BETA_CHANNEL_ID_TECH, Keys.BETA_CHANNEL_ID_TP);

        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        ComputeScores compute = new ComputeScores();
        ComputeScorescc computeScorescc = new ComputeScorescc();

        switch (event.getMessage().getContentRaw()){
            case "/next" -> DiscordReactor.next(event);

            case "/startcomputinglichess" -> {
                try {
                    DiscordReactor.automaticComputeScores(event, compute, arenaLeagueCollection, swissLeagueCollection);
                } catch (ChessComPubApiException | IOException e) {
                    event.getChannel().sendMessage("Internal Error!").queue();
                }
            }

            case "/startcomputingchesscom" -> {
                try {
                    DiscordReactor.automaticCCComputeScores(event, computeScorescc, arenaLeagueCollection, swissLeagueCollection);
                } catch (ChessComPubApiException | IOException e) {
                    event.getChannel().sendMessage("Internal Error!").queue();
                }
            }

            case "/sendligareminder" -> DiscordReactor.sendLigaMessage(event);

            case "/ligacompute" -> DiscordReactor.computeChessDojoLichessLigaScores(event,arenaLeagueCollection, swissLeagueCollection, compute);
        }
    }
}