package dojo.bot.Controller.Discord;

import dojo.bot.Controller.Database.MongoConnect;
import dojo.bot.Controller.Database.SearchQuery;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


/**
 * The type Search query manager.
 */
public class SearchQueryManager {


    /**
     * Instantiates a new Search query manager.
     */
    public SearchQueryManager(){

    }


    /**
     * Render search results.
     *
     * @param event the event
     * @param query the query
     */
    public void renderSearchResults(SlashCommandInteractionEvent event, SearchQuery query){

       if(DiscordAdmin.isDiscordAdmin(event)){
           event.deferReply(true).queue();
           String mode = event.getOptionsByName("query-mode").get(0).getAsString();
           String searchVal = event.getOption("value-search").getAsString();
           switch (mode){
               case "limode" -> {
                   event.getHook().sendMessage(query.searchUserByLichessUser(searchVal, MongoConnect.getLichessplayers())).queue();
               }
               case "ccmode" -> {
                   event.getHook().sendMessage(query.searchUserByChessComUser(searchVal, MongoConnect.getChesscomplayers())).queue();
               }
               case "dimode" -> {
                   event.getHook().sendMessage(query.searchUserByDiscordID(searchVal, MongoConnect.getLichessplayers())).queue();
               }
           }
       } else{
           event.reply("Your not an admin!").queue();
       }

    }




}
