package dojo.bot.Controller.Database;

import com.mongodb.client.MongoCollection;
import dojo.bot.Controller.DojoScoreboard.DojoScoreboard;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;

import static dojo.bot.Controller.CalculateScores.ScoresUtil.isTournamentIDresent;
import static dojo.bot.Controller.Discord.DiscordAdmin.isDiscordAdmin;

/**
 * The type Injection.
 */
public class Injection {


    /**
     * Inject.
     *
     * @param event the event
     * @param arena the arena
     * @param swiss the swiss
     */
    public void inject(SlashCommandInteractionEvent event, MongoCollection<Document> arena,
                       MongoCollection<Document> swiss) {
        if (isDiscordAdmin(event)) {

            String url = event.getOption("url").getAsString();
            event.reply("Connecting..").queue();

            if(url.contains("https://lichess.org/tournament/")){
                InjectLichessTournament(event, arena, "tournament/", url);
            }else if(url.contains("https://lichess.org/swiss/")){
                InjectLichessTournament(event, swiss, "swiss/", url);
            }else if(url.contains("https://www.chess.com/tournament/live/arena/")){
                InjectChesscomTournament(event, arena, "arena/", "arenacc", url);
            }else if(url.contains("https://www.chess.com/tournament/live/")){
                InjectChesscomTournament(event, swiss, "live/", "swisscc", url);
            }else{
                event.getChannel().sendMessage("error! invalid URL").queue();
            }

        } else {
            event.reply("Error! Sorry you are not an admin!").setEphemeral(true).queue();
        }

    }


    /**
     * Inject lichess tournament.
     *
     * @param event      the event
     * @param collection the collection
     * @param regex      the regex
     * @param url        the url
     */
    public void InjectLichessTournament(SlashCommandInteractionEvent event, MongoCollection<Document> collection, String regex, String url){
        System.out.println("test1");
        String[] spliturl = url.split(regex);
        String touryID = spliturl[1];

        DbTournamentEntry entry = new DbTournamentEntry(touryID, touryID);
        Document document = new Document("Name", entry.getTournamentName())
                .append("Id", entry.getLichessTournamentId());
        collection.insertOne(document);
        DojoScoreboard.createTournament(url);
        event.getChannel().sendMessage("Success! Injected URL " + url + " In the database and the site!")
                .queue();

    }

    /**
     * Inject liga tournament.
     *
     * @param event      the event
     * @param collection the collection
     * @param regex      the regex
     * @param url        the url
     */
    public void InjectLigaTournament(MessageReceivedEvent event, MongoCollection<Document> collection, String regex, String url){
        System.out.println("test1");
        String[] spliturl = url.split(regex);
        String touryID = spliturl[1];
        if (isTournamentIDresent(touryID, MongoConnect.getComputedId())) {
            event.getChannel().sendMessage("This Tournament is already computed and Injected! Not injecting to save resources if you want to brute force use /inject ;)").queue();
            return;
        }
        DbTournamentEntry entry = new DbTournamentEntry(touryID, touryID);
        Document document = new Document("Name", entry.getTournamentName())
                .append("Id", entry.getLichessTournamentId());
        collection.insertOne(document);
        DojoScoreboard.createTournament(url);
        event.getChannel().sendMessage("Success! Injected URL " + url + " In the database and the site!")
                .queue();

    }

    /**
     * Inject chesscom tournament.
     *
     * @param event      the event
     * @param collection the collection
     * @param regex      the regex
     * @param target     the target
     * @param url        the url
     */
    public void InjectChesscomTournament(SlashCommandInteractionEvent event, MongoCollection<Document> collection, String regex, String target, String url){
       System.out.println("test2");
       String[] spliturl = url.split(regex);
       String touryID = spliturl[1];
       DbTournamentEntry entry = new DbTournamentEntry(touryID, touryID);
       Document document = new Document("Name", entry.getTournamentName())
               .append("Id", entry.getLichessTournamentId())
               .append(target, entry.getLichessTournamentId());
       collection.insertOne(document);
       DojoScoreboard.createTournament(url);
       event.getChannel().sendMessage("Success! Injected URL " + url + " In the database and the site!")
               .queue();
   }





}
