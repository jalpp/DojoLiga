package dojo.bot.Controller;

import com.mongodb.client.MongoCollection;
import dojo.bot.Model.DbTournamentEntry;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bson.Document;

import java.util.Objects;

import static dojo.bot.Controller.DiscordAdmin.isDiscordAdmin;

public class Injection {


    public void inject(SlashCommandInteractionEvent event, MongoCollection<Document> arena,
                       MongoCollection<Document> swiss) {
        if (isDiscordAdmin(event)) {

            String url = Objects.requireNonNull(event.getOption("url")).getAsString();
            event.reply("Connecting..").queue();


            switch (url) {
                case "https://lichess.org/tournament/" -> InjectLichessTournament(event, arena, "tournament/", url);
                case "https://lichess.org/swiss/" -> InjectLichessTournament(event, swiss, "swiss/", url);
                case "https://www.chess.com/tournament/live/arena/" -> InjectChesscomTournament(event, arena, "arena/", "arenacc", url);
                case "https://www.chess.com/tournament/live/" -> InjectChesscomTournament(event, swiss, "live/", "swisscc", url);
                default -> event.reply("error! invalid URL").queue();

            }

        } else {
            event.reply("Error! Sorry you are not an admin!").setEphemeral(true).queue();
        }

    }



    private void InjectLichessTournament(SlashCommandInteractionEvent event, MongoCollection<Document> collection, String regex, String url){
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

   private void InjectChesscomTournament(SlashCommandInteractionEvent event, MongoCollection<Document> collection, String regex, String target, String url){
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