package dojo.bot.Controller.League;

import chariot.Client;
import chariot.ClientAuth;
import chariot.model.Arena;
import chariot.model.Fail;
import com.mongodb.client.MongoCollection;
import dojo.bot.Controller.DojoScoreboard.DojoScoreboard;
import dojo.bot.Controller.Database.DbTournamentEntry;
import dojo.bot.Runner.Main;
import org.bson.Document;

import java.time.ZonedDateTime;

public class ArenaLeagueManager {

    private final ClientAuth client = Client.auth(Main.botToken);

    /**
     * Manager that handles actually creating the tournament via Lichess API client
     *
     * @param MaxRating            max rating for tournament
     * @param DOJO_TEAM            the team where is created
     * @param isZerk               can players zerk
     * @param League_NAME          the leaague name
     * @param LEAGUE_NEXT          the name stored in the MongoDB collection
     * @param fen                  the chess starting position
     * @param isRated              are players rating effected
     * @param duration             the tournament duration
     * @param finalDaysIndex       how long in future the final days the league last
     * @param clockTime            the games clock time
     * @param clockIncrement       the game clock increments
     * @param LEAGUE_DES           the league small descriptions
     * @param addIds               Stringbuilder to collect info
     * @param tournamentCollection the effected tournament MongoDB collection
     */

    public void manageArenaLeagueCreation(Integer MaxRating, String DOJO_TEAM, Boolean isZerk, String League_NAME, String LEAGUE_NEXT, String fen, Boolean isRated, Integer duration, ZonedDateTime finalDaysIndex, Integer clockTime, Integer clockIncrement, String LEAGUE_DES, StringBuilder addIds, MongoCollection<Document> tournamentCollection) {

        var res = this.client.tournaments().createArena(ArenaLeagueConsumer.leagueConsumerInvoker(
                MaxRating, DOJO_TEAM, isZerk, League_NAME, fen, isRated, duration, finalDaysIndex, clockTime, clockIncrement, LEAGUE_DES
        ));

        if (res instanceof Fail<Arena> fail) {
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
