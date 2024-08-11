package dojo.bot.Controller.Database;

import com.mongodb.client.MongoCollection;
import dojo.bot.Controller.DojoScoreboard.DojoScoreboard;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bson.Document;

import static dojo.bot.Controller.Discord.DiscordAdmin.isDiscordAdmin;

public class Injection {


    public void inject(SlashCommandInteractionEvent event, MongoCollection<Document> arena,
                       MongoCollection<Document> swiss) {
        if (isDiscordAdmin(event)) {

            String url = event.getOption("url").getAsString();
            event.reply("Connecting..").queue();

            if (url.contains("https://lichess.org/tournament/")) {
                InjectLichessTournament(event, arena, "tournament/", url);
            } else if (url.contains("https://lichess.org/swiss/")) {
                InjectLichessTournament(event, swiss, "swiss/", url);
            } else if (url.contains("https://www.chess.com/tournament/live/arena/")) {
                InjectChesscomTournament(event, arena, "arena/", "arenacc", url);
            } else if (url.contains("https://www.chess.com/tournament/live/")) {
                InjectChesscomTournament(event, swiss, "live/", "swisscc", url);
            } else {
                event.getChannel().sendMessage("error! invalid URL").queue();
            }

        } else {
            event.reply("Error! Sorry you are not an admin!").setEphemeral(true).queue();
        }

    }


    public void InjectLichessTournament(SlashCommandInteractionEvent event, MongoCollection<Document> collection, String regex, String url) {
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

    public void InjectChesscomTournament(SlashCommandInteractionEvent event, MongoCollection<Document> collection, String regex, String target, String url) {
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
