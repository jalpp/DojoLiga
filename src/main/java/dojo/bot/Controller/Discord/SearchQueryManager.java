package dojo.bot.Controller.Discord;

import dojo.bot.Controller.Database.SearchQuery;
import dojo.bot.Runner.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class SearchQueryManager {


    public SearchQueryManager() {

    }


    public void renderSearchResults(SlashCommandInteractionEvent event, SearchQuery query) {

        if (DiscordAdmin.isDiscordAdmin(event)) {
            event.deferReply(true).queue();
            String mode = event.getOptionsByName("query-mode").get(0).getAsString();
            String searchVal = event.getOption("value-search").getAsString();
            switch (mode) {
                case "limode" -> {
                    event.getHook().sendMessage(query.searchUserByLichessUser(searchVal, Main.collection)).queue();
                }
                case "ccmode" -> {
                    event.getHook().sendMessage(query.searchUserByChessComUser(searchVal, Main.chesscomplayers)).queue();
                }
                case "dimode" -> {
                    event.getHook().sendMessage(query.searchUserByDiscordID(searchVal, Main.collection)).queue();
                }
            }
        } else {
            event.reply("Your not an admin!").queue();
        }

    }


}
