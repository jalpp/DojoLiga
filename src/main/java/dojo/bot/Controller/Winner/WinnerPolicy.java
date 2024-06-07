package dojo.bot.Controller.Winner;

import chariot.Client;
import dojo.bot.Controller.User.Profile;
import dojo.bot.Controller.DojoScoreboard.DojoScoreboard;
import dojo.bot.Controller.League.Time_Control;
import dojo.bot.Controller.League.Type;
import dojo.bot.Controller.User.ChessPlayer;
import com.mongodb.client.MongoCollection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * This class is responsible for computing possible winners for DojoLiga
 *
 */

public class WinnerPolicy {


    private Client client = Client.basic();


    public static List<String> getPlayerNames(MongoCollection collection, int startIndex, int endIndex){

        List<String> usernames = new ArrayList<>();

        try(MongoCursor<Document> cursor = collection.find().skip(startIndex).limit(endIndex - startIndex + 1).iterator()){

            while (cursor.hasNext()) {
                Document document = cursor.next();

                // Check if the document contains the "username" field
                if (document.containsKey("username")) {
                    // Retrieve the username and add it to the list
                    String username = document.getString("username");
                    usernames.add(username);
                }
            }

            return usernames;
        }

    }


   public String getISOtime(int month, int year){

       LocalDate date = LocalDate.of(year, month, 1); // Setting the day as 1 for simplicity

       DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

       String formattedDate = date.format(formatter);

       return formattedDate + "T23:00:00Z";
   }


    public String findWinner(Time_Control timeControl, Type type, int month, int year){
            ArrayList<ChessPlayer> top10Players = DojoScoreboard.getLeaderboard(timeControl, "monthly", getISOtime(month, year), type);
            List<String> winnersGreaterThan1800 = new ArrayList<>();
            List<String> winnerLessThan1800 = new ArrayList<>();
            List<String> isNonWinner = new ArrayList<>();
            StringBuilder builder = new StringBuilder();


        if(top10Players != null) {
            for (ChessPlayer top10Player : top10Players) {
                String username = top10Player.getUsername();
                int rating = top10Player.getRating();
                int score = top10Player.getScore();
                if (rating > 1800 && isWinner(timeControl, username) && score > 0) {
                    winnersGreaterThan1800.add(username + " (" + rating + ") " + " Score: " + score);
                } else if (rating < 1800 && isWinner(timeControl, username) && score > 0) {
                    winnerLessThan1800.add(username + " (" + rating + ") " + " Score: " + score);
                } else if (!isWinner(timeControl, username)) {
                    isNonWinner.add(username + " (" + rating + ") " + " Score: " + score);
                }

            }
            builder.append("**Possible winners I computed For Date").append(" ").append(getISOtime(month, year)).append(" ").append(":** \n\n");

            builder.append("** Category: Open \n").append(" Time Control: ").append(timeControl.getTitle())
                    .append("\n Tournament Type: ").append(type.getName()).append("**").append("\n\n");


            for (String s : winnersGreaterThan1800) {
                builder.append("✅ ").append(s).append("\n");
            }

            builder.append("\n");


            builder.append("** Category: U1800 \n ").append("Time Control: ").append(timeControl.getTitle())
                    .append(" \n Tournament Type: ").append(type.getName()).append("**").append("\n\n");


            for (String u : winnerLessThan1800) {
                builder.append("✅ ").append(u).append("\n");
            }

            builder.append("\n **Disqualified Players [Reason Cheater Or Closed Account Or Prov Rating Field] ** ").append("\n");

            if (isNonWinner.size() == 0) {
                builder.append("No Disqualified players found! Scanned ").append(top10Players.size()).append(" # of players");
            } else {
                for (String nonWinners : isNonWinner) {
                    builder.append("❌ ").append(nonWinners).append("\n");
                }
            }


            return builder.toString();

        }else{
            return "The following Date data is not collected yet, Winner policy does not apply";
        }

    }




    public boolean isWinner(Time_Control timeControl, String username){

        return !isCheater(username) && !isClosed(username) && !isProv(username, timeControl);

    }



    public boolean isCheater(String username){
       return client.users().byId(username).get().tosViolation();
    }


    public boolean isClosed(String username){
        return client.users().byId(username).get().disabled();
    }

// Not good for O(N) time makes algorithm worse
//    public boolean isInTeam(String username){
//
//        Client.basic().users().byId()
//
////       List<TeamMember> members = client.teams().usersByTeamId("chessdojo").stream().toList();
////
////       for(TeamMember member: members){
////           if(member.user().name().equalsIgnoreCase(username)){
////               return true;
////           }
////       }
//
//       return false;
//    }


    public boolean isProv(String username, Time_Control timeControl){
       switch (timeControl){
           case BLITZ -> {
               return new Profile(client, username).getSingleBlitzRating() == -1;
           }
           case RAPID -> {
               return new Profile(client, username).getSingleRapidRating() == -1;
           }
           case CLASSICAL -> {
               return new Profile(client, username).getSingleClassicalRating() == -1;
           }
       }

       return false;
    }


    // Not good algo
//    public boolean isRatingGreaterThan1800(String username, Time_Control timeControl){
//
//        if(!isProv(username, timeControl)){
//            switch (timeControl){
//
//                case BLITZ -> {
//                    return new Profile(client, username).getSingleBlitzRating() > 1800;
//                }
//
//                case RAPID -> {
//                    return new Profile(client, username).getSingleRapidRating() > 1800;
//                }
//
//                case CLASSICAL -> {
//                    return new Profile(client, username).getSingleClassicalRating() > 1800;
//                }
//
//            }
//        }
//
//        return false;
//    }
//
//
//    public int getPlayerRatingPrint(String username, Time_Control timeControl){
//
//        if(!isProv(username, timeControl)){
//
//            switch (timeControl){
//
//                case BLITZ -> {
//                    return new Profile(client, username).getSingleBlitzRating();
//                }
//
//                case RAPID -> {
//                    return new Profile(client, username).getSingleRapidRating();
//                }
//
//                case CLASSICAL -> {
//                    return new Profile(client, username).getSingleClassicalRating();
//                }
//
//
//
//            }
//
//
//        }
//
//        return 0;


    }
