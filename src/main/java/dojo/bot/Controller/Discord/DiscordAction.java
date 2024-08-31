package dojo.bot.Controller.Discord;

import chariot.Client;
import com.mongodb.client.MongoCollection;
import dojo.bot.Controller.Database.Injection;
import dojo.bot.Controller.Database.SearchQuery;
import dojo.bot.Controller.League.FutureTournamentViewerManager;
import dojo.bot.Controller.Standing.StandingReactManager;
import dojo.bot.Controller.TicketSystem.TicketManager;
import dojo.bot.Controller.User.UserLeagueActionManager;
import dojo.bot.Controller.User.UserProfileManager;
import dojo.bot.Controller.User.Verification;
import dojo.bot.Controller.CalculateScores.AutomaticComputeManager;
import dojo.bot.Controller.CalculateScores.ComputeScores;
import dojo.bot.Controller.CalculateScores.ComputeScorescc;
import dojo.bot.Controller.League.ConfigLeagueManager;
import dojo.bot.Controller.Standing.ComputeStandings;
import dojo.bot.Controller.Winner.ComputeLigaWinner;
import dojo.bot.Controller.Winner.WinnerPolicy;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.bson.Document;

import java.io.IOException;

import static dojo.bot.Controller.Discord.DiscordAdmin.isDiscordAdmin;

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
    private final ComputeLigaWinner LigaWinnerManager = new ComputeLigaWinner();
    private final UserProfileManager profileManager = new UserProfileManager();
    private final UserLeagueActionManager playerLeagueActionManager = new UserLeagueActionManager();
    private final FutureTournamentViewerManager tournamentViewerManager = new FutureTournamentViewerManager();
    private final AutomaticComputeManager automaticComputeManager = new AutomaticComputeManager();
    private final Injection injectionManager = new Injection();

    /**
     * Instantiates a new Discord action.
     */
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
        if (!Slow_down_buddy.checkSpam(event)) {
            standingReactManager.StandingsReact(event, standings, collection);
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
            configLeagueManager.configLeagueArena(event, arenaLeagueCollection);
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
            configLeagueManager.configLeagueSwiss(event, swissLeagueCollection);
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
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public void startComputingScores(SlashCommandInteractionEvent event, ComputeScores compute,
                                     MongoCollection<Document> arenaLeagueCollection, MongoCollection<Document> swissLeagueCollection) throws ChessComPubApiException, IOException {
        automaticComputeManager.startComputingScores(event, compute, arenaLeagueCollection, swissLeagueCollection, Slow_down_buddy);
    }

    /**
     * Discord action to start user verification flow
     *
     * @param event      Slash command event
     * @param passport   Verification object
     * @param collection collection of players
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public void startVerificationProcess(SlashCommandInteractionEvent event, Verification passport,
                                         MongoCollection<Document> collection) throws ChessComPubApiException, IOException {
        if (Slow_down_buddy.checkSpam(event)) {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after a 1 min!").setEphemeral(true).queue();
        } else {
            vertificationManager.startVerificationProcessLichess(event, passport, collection);
        }
    }


    /**
     * Start verification process chess com.
     *
     * @param event      the event
     * @param passport   the passport
     * @param collection the collection
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public void startVerificationProcessChessCom(SlashCommandInteractionEvent event, Verification passport,
                                                 MongoCollection<Document> collection) throws ChessComPubApiException, IOException {
        if (Slow_down_buddy.checkSpam(event)) {
            event.reply("Slow Down buddy, go watch ChessDojo and run the command after a 1 min!").setEphemeral(true).queue();
        } else {
            vertificationManager.startVerificationProcessChessCom(event, passport, collection);
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
        if (!Slow_down_buddy.checkSpam(event)) {
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
        profileManager.lookUpProfile(passport, event, collection, client, helper, Slow_down_buddy);
    }

    /**
     * Lookup chesscom profile for given user
     *
     * @param passport   Verification passport
     * @param event      Discord trigger event
     * @param collection Collection of players
     */
    public void lookupProfileChessCom(Verification passport, SlashCommandInteractionEvent event, MongoCollection<Document> collection) {
        profileManager.lookupProfileChessCom(passport, event, collection, Slow_down_buddy);
    }

    /**
     * Gets standings for Lichess URL
     *
     * @param event  Slash command event
     * @param client Lichess java client
     */
    public void getStandingsForURL(SlashCommandInteractionEvent event, Client client) {
        standingReactManager.getStandingsForURL(event, client, Slow_down_buddy);
    }

    /**
     * handle sending pairings for the Lichess url
     *
     * @param event Discord trigger event
     */
    public void getPairingsReact(SlashCommandInteractionEvent event) {
        standingReactManager.getPairingsReact(event, Slow_down_buddy);
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
        playerLeagueActionManager.getRankReact(event, compute, collection, Slow_down_buddy);
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
        playerLeagueActionManager.getScoreReact(event, compute, collection, Slow_down_buddy);
    }

    /**
     * Sends 3 leaderboards into #general chat at 1:00 PM everyday
     *
     * @param jda        JDA object
     * @param channelId  Channel ID
     * @param collection collection of players
     */
    public void sendStandingEmbeds(JDA jda, String channelId, MongoCollection<Document> collection) {
        standingReactManager.sendStandingEmbeds(jda, channelId, collection);
    }

    /**
     * Sends top 10 for blitz, rapid, classical ratings
     *
     * @param event      Slash command event
     * @param collection collection of players
     * @param cc         the cc
     * @param standings  Standings object
     */
    public void sendTop10(SlashCommandInteractionEvent event, MongoCollection<Document> collection,
                          MongoCollection<Document> cc,
                          ComputeStandings standings) {
        playerLeagueActionManager.sendTop10(event, collection, cc, standings, Slow_down_buddy);
    }

    /**
     * Send league help.
     *
     * @param event  the event
     * @param helper the helper
     */
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
        tournamentViewerManager.next(event);
    }

    /**
     * Generates a list of the next few upcoming arenas and swiss tournaments in
     * response to bot messages.
     *
     * @param event The message that kicked off the next() request.
     */
    public void next(MessageReceivedEvent event) {
        tournamentViewerManager.next(event);
    }


    /**
     * Inject tournament into league
     *
     * @param event Discord trigger event
     * @param arena Arena collection
     * @param swiss Swiss collection
     */
    public void inject(SlashCommandInteractionEvent event, MongoCollection<Document> arena,
                       MongoCollection<Document> swiss) {
        injectionManager.inject(event, arena, swiss);

    }


    /**
     * Automatically Chesscom computes scores for any finished tournaments that haven't
     * already been computed.
     *
     * @param event     The message that kicked off the                  automaticComputeScores() event.
     * @param computecc The ComputeScores object to use when calculating                  player scores.
     * @param arenacc   The collection of Arena tournaments in the database.
     * @param swisscc   The collection of Swiss tournaments in the database.
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public void automaticCCComputeScores(MessageReceivedEvent event, ComputeScorescc computecc, MongoCollection<Document> arenacc, MongoCollection<Document> swisscc) throws ChessComPubApiException, IOException {
        automaticComputeManager.automaticCCComputeScores(event, computecc, arenacc, swisscc);
    }


    /**
     * Automatically computes scores for any finished tournaments that haven't
     * already been computed.
     *
     * @param event           The message that kicked off the                        automaticComputeScores() event.
     * @param compute         The ComputeScores object to use when calculating                        player scores.
     * @param arenaCollection The collection of Arena tournaments in the database.
     * @param swissCollection The collection of Swiss tournaments in the database.
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public void automaticComputeScores(MessageReceivedEvent event, ComputeScores compute,
                                       MongoCollection<Document> arenaCollection, MongoCollection<Document> swissCollection) throws ChessComPubApiException, IOException {
        automaticComputeManager.automaticComputeScores(event, compute, arenaCollection, swissCollection);
    }


    /**
     * Computes the liga winner for given policy
     *
     * @param event  Discord trigger event
     * @param policy winner policy
     */
    public void computeLigaWinners(SlashCommandInteractionEvent event, WinnerPolicy policy) {
        LigaWinnerManager.computeLigaWinners(event, policy);
    }


    /**
     * creates a ticket service in Discord via Discord event
     *
     * @param event Discord trigger event
     */
    public void createEntryTicket(SlashCommandInteractionEvent event) {
        ticketSystem.createEntryTicket(event);
    }

    /**
     * takes care of Ticket Form logic
     *
     * @param event           Discord trigger event
     * @param senseiChannelId Sensei channel
     * @param techChannelID   Tech channel
     * @param tpchannel       Training program channel
     * @param tacchannel      feedback channel
     */
    public void ticketFormSystem(ModalInteractionEvent event, String senseiChannelId, String techChannelID, String tpchannel, String tacchannel) {
        ticketSystem.ticketFormSystem(event, senseiChannelId, techChannelID, tpchannel, tacchannel);

    }

    /**
     * takes care of sending the Ticket form
     *
     * @param event Discord trigger event
     */
    public void sentTheForms(ButtonInteraction event) {
        ticketSystem.sentTheForms(event);
    }


    /**
     * Compute chess dojo lichess liga scores.
     *
     * @param event   the event
     * @param arena   the arena
     * @param swiss   the swiss
     * @param compute the compute
     */
    public void computeChessDojoLichessLigaScores(MessageReceivedEvent event, MongoCollection<Document> arena, MongoCollection<Document> swiss, ComputeScores compute) {
        automaticComputeManager.computeChessDojoLichessLigaScores(event, arena, swiss, compute);
    }


    /**
     * Send liga message.
     *
     * @param event the event
     */
    public void sendLigaMessage(MessageReceivedEvent event) {
        configLeagueManager.sendLigaMessage(event);
    }


    /**
     * Unlinke user belt.
     *
     * @param event the event
     */
    public void unlinkeUserBelt(SlashCommandInteractionEvent event) {
        playerLeagueActionManager.unlinkeUserBelt(event);
    }


    /**
     * Perform search.
     *
     * @param event the event
     */
    public void performSearch(SlashCommandInteractionEvent event) {
        SearchQueryManager queryManager = new SearchQueryManager();
        queryManager.renderSearchResults(event, new SearchQuery());
    }


}