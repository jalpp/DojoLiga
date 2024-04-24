package dojo.bot.Controller;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Work in progress optimized injection code class
 */

public class Injection {





    public Injection(){

    }


    public boolean isValidInjectionURl(String url){
        return url.equalsIgnoreCase("https://lichess.org/tournament/")
                || url.equalsIgnoreCase("https://lichess.org/swiss/")
                || url.equalsIgnoreCase("https://www.chess.com/tournament/live/arena/")
                || url.equalsIgnoreCase("https://www.chess.com/tournament/live/");
    }



    public void InjectTournamentLichess(String url, MongoCollection<Document> arena,
                                 MongoCollection<Document> swiss){


        if(isValidInjectionURl(url)){

        }

    }










}