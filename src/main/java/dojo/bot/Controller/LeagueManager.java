package dojo.bot.Controller;

import chariot.Client;
import chariot.ClientAuth;
import chariot.model.Arena;
import chariot.model.Fail;
import com.mongodb.client.MongoCollection;
import dojo.bot.Model.DbTournamentEntry;
import dojo.bot.Runner.Main;
import org.bson.Document;

import java.time.ZonedDateTime;

public class LeagueManager {

    private final ClientAuth client = Client.auth(Main.botToken);

    public void manageArenaLeagueCreation(Integer MaxRating, String DOJO_TEAM, Boolean isZerk, String League_NAME, String LEAGUE_NEXT, String fen, Boolean isRated, Integer duration, ZonedDateTime finalDaysIndex, Integer clockTime, Integer clockIncrement, String LEAGUE_DES, StringBuilder addIds, MongoCollection<Document> tournamentCollection){

        var res = this.client.tournaments().createArena(LeagueConsumer.LeagueConsumerArenaInvoker(
                MaxRating, DOJO_TEAM, isZerk, League_NAME, fen, isRated, duration, finalDaysIndex, clockTime, clockIncrement, LEAGUE_DES
        ));

        if(res instanceof Fail<Arena> fail){
             System.out.println(fail.message());
             addIds.append(fail.message());
        }

        addIds.append("https://lichess.org/tournament/").append(res.get().id())
                .append("\n");
        DojoScoreboard
                .createTournament("https://lichess.org/tournament/" + res.get().id());



        DbTournamentEntry entry = new DbTournamentEntry(LEAGUE_NEXT,
                res.get().id());
        Document document = new Document("Name", entry.getTournamentName())
                .append("Id", entry.getLichessTournamentId());
        tournamentCollection.insertOne(document);


        System.out.println(res.toString());



    }



}