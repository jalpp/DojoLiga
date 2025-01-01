package dojo.bot.Controller.League;

import chariot.Client;
import chariot.model.Swiss;
import chariot.model.Tournament;
import dojo.bot.Controller.Discord.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Future Tournament Viewer
 * @author Jack Stenglein
 */

public class FutureTournamentViewerManager {

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
        arenaEmbed.setDescription(arenaResult.toString().isEmpty() ? "No upcoming arena tournaments!" : arenaResult.toString());
        arenaEmbed.setFooter("All times are in your local timezone");

        EmbedBuilder swissEmbed = new EmbedBuilder();
        swissEmbed.setTitle("Upcoming Swiss");
        swissEmbed.setColor(Color.BLUE);
        swissEmbed.setThumbnail(Helper.DOJO_LOGO);
        swissEmbed.setDescription(swissResult.toString().isEmpty() ? "No upcoming swiss tournaments!" : swissResult.toString());
        swissEmbed.setFooter("All times are in your local timezone");

        ArrayList<EmbedBuilder> result = new ArrayList<>();
        result.add(arenaEmbed);
        result.add(swissEmbed);
        return result;
    }






}
