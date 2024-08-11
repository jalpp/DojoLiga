package dojo.bot.Controller.League;

import chariot.Client;
import chariot.ClientAuth;
import chariot.model.Fail;
import chariot.model.Swiss;
import com.mongodb.client.MongoCollection;
import dojo.bot.Controller.Database.DbTournamentEntry;
import dojo.bot.Controller.DojoScoreboard.DojoScoreboard;
import dojo.bot.Runner.Main;
import org.bson.Document;

import java.time.ZonedDateTime;

public class SwissLeagueManager {


    private final ClientAuth client = Client.auth(Main.botToken);

    public void manageSwissLeagueCreation(Integer MaxRating, String DOJO_TEAM, String League_NAME, String LEAGUE_NEXT, String fen, Boolean isRated, Integer interval, ZonedDateTime finalDaysIndex, Integer clockTime, Integer clockIncrement, String LEAGUE_DES, MongoCollection<Document> tournamentCollection, Integer nbRounds, StringBuilder addIds) {


        var res = this.client.tournaments().createSwiss(DOJO_TEAM,
                SwissLeagueConsumer.leagueConsumerInvoker(MaxRating, DOJO_TEAM, nbRounds, League_NAME, fen, isRated, interval, finalDaysIndex, clockTime * 60, clockIncrement, LEAGUE_DES)
        );

        if (res instanceof Fail<Swiss> fail) {
            System.out.print(fail.message());
            addIds.append(fail.message());
        }

        System.out.println(res);

        addIds.append("https://lichess.org/swiss/").append(res.get().id()).append("\n");
        DojoScoreboard.createTournament("https://lichess.org/swiss/" + res.get().id());

        DbTournamentEntry entry = new DbTournamentEntry(LEAGUE_NEXT,
                res.get().id());
        Document document = new Document("Name", entry.getTournamentName())
                .append("Id", entry.getLichessTournamentId());

        tournamentCollection.insertOne(document);

    }


}
