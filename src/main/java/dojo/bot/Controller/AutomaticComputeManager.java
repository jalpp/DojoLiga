package dojo.bot.Controller;

import chariot.Client;
import chariot.model.Swiss;
import chariot.model.Tournament;
import com.mongodb.client.MongoCollection;
import dojo.bot.Runner.Main;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dojo.bot.Controller.DiscordAdmin.isDiscordAdmin;
import static dojo.bot.Controller.ScoresUtil.isTournamentIDresent;
import static dojo.bot.Controller.ScoresUtil.tournamentPresent;

public class AutomaticComputeManager {





    /**
     * Discord Action to start computing scores
     *
     * @param event                 Slash command event
     * @param compute               Compute scores object
     * @param arenaLeagueCollection collection of arena ids
     * @param swissLeagueCollection collection of swiss ids
     */

    public void startComputingScores(SlashCommandInteractionEvent event, ComputeScores compute,
                                     MongoCollection<Document> arenaLeagueCollection, MongoCollection<Document> swissLeagueCollection, AntiSpam Slow_down_buddy) throws ChessComPubApiException, IOException {
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
     * Automatically Chess.com computes scores for any finished tournaments that haven't
     * already been computed.
     *
     * @param event           The message that kicked off the
     *                        automaticComputeScores() event.
     * @param computecc         The ComputeScores object to use when calculating
     *                        player scores.
     * @param arenacc The collection of Arena tournaments in the database.
     * @param swisscc The collection of Swiss tournaments in the database.
     */


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
     * Automatically Chesscom computes scores for any finished tournaments that haven't
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
            if (tournamentPresent(arenaCollection, tournament.id())) {
                System.out.println("Arena not in current League: https://lichess.org/tournament/" + tournament.id());
                continue;
            }
            if (isTournamentIDresent(tournament.id(), Main.computedId)) {
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
            if (tournamentPresent(swissCollection, swiss.id())) {
                System.out.println("Swiss not in current League: https://lichess.org/swiss/" + swiss.id());
                continue;
            }
            if (isTournamentIDresent(swiss.id(), Main.computedId)) {
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

    /**
     *  compute scores Lichess blitz bundesliga and Mega Team battle scores for Dojo players
     * @param event Discord trigger event
     * @param arena arena collection
     * @param swiss swiss collection
     * @param compute compute manager
     */

    public void computeChessDojoLichessLigaScores(MessageReceivedEvent event, MongoCollection<Document> arena, MongoCollection<Document> swiss, ComputeScores compute){
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));

        List<Tournament> list = new ArrayList<>(Client.basic().teams().arenaByTeamId("chessdojo", 50).stream().filter(tournament -> tournament.fullName().contains("Lichess Liga")).filter(tournament -> tournament.fullName().contains("Lichess Mega")).filter(tournament -> tournament.finishesTime().getMonthValue() == now.getMonthValue()).toList());

        if(list.isEmpty()){
            event.getChannel().sendMessage("No new completed Lichess Dojo Liga tournaments found. Scores cannot be computed.").queue();
            return;
        }
        event.getChannel().sendMessage("Computing Lichess Dojo Liga tournaments of size " + list.size() + "  Please wait!").queue();

        for(Tournament t: list){
            String res = compute.calculateLichessLigaScores("https://lichess.org/tournament/" + t.id(), arena);
            event.getChannel().sendMessage(res).queue();
        }
    }



    
}
