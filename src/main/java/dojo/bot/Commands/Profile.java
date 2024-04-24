package dojo.bot.Commands;

import chariot.Client;
import chariot.model.*;
import dojo.bot.Controller.UserTrophy;
import dojo.bot.Model.UserObject;

import java.util.List;

/**
 * Class to show Lichess profile
 */
public class Profile extends UserObject {


    private String sayProfile = "";


    /**
     * Profile constructor to build up a user profile
     * @param client Lichess Java client
     * @param userParsing Lichess username
     */


    public Profile(Client client, String userParsing) {
        super(client, userParsing);
    }


    /**
     * returns the blitz rating in string version
     * @return
     *
     * String containing blitz rating value with emoji
     */


    public String getBlitzRatings(){
        One<PerformanceStatistics> userBlitz = this.getClient().users().performanceStatisticsByIdAndType(this.getUserID(), Enums.PerfType.blitz);


        String blitzRating = " \uD83D\uDD25 **Blitz**: ?";

        if(userBlitz.isPresent() && !userBlitz.get().perf().glicko().provisional()){
            return blitzRating = " \uD83D\uDD25 **Blitz**:  " + userBlitz.get().perf().glicko().rating().intValue();
        }

        return blitzRating;

    }


    /**
     * returns the rapid rating in string version
     * @return
     *
     * String containing rapid rating value witj emoji
     */


    public String getRapidRatings(){
        One<PerformanceStatistics> userRapid = this.getClient().users().performanceStatisticsByIdAndType(this.getUserID(), Enums.PerfType.rapid);

        String rapidRating = " \uD83D\uDC07 **Rapid**: ?";

        if(userRapid.isPresent() && !userRapid.get().perf().glicko().provisional()){
            return rapidRating = "\uD83D\uDC07 **Rapid**:  " + userRapid.get().perf().glicko().rating().intValue();
        }

        return rapidRating;

    }


    /**
     * returns the rating of rapid performance of the player
     * @return
     *
     * returns an String version of the  rating value
     */

    public int getSingleRapidRating(){
        One<PerformanceStatistics> userRapid = this.getClient().users().performanceStatisticsByIdAndType(this.getUserID(), Enums.PerfType.rapid);

        if(userRapid == null){
            return -1;
        }

        if(!userRapid.get().perf().glicko().provisional() && userRapid.isPresent() ){
            return userRapid.get().perf().glicko().rating().intValue();
        }

        return -1;

    }

    /**
     * returns the String version of a rating for bullet performance of the player
     * @return
     * returns an String containing the bullet rating with Emoji
     *
     */



    public String getBulletRatings(){
        One<PerformanceStatistics> userBullet = this.getClient().users().performanceStatisticsByIdAndType(this.getUserID(), Enums.PerfType.bullet);

        String bulletRating = "\uD83D\uDD2B **Bullet**: ?";

        if(userBullet.isPresent() && !userBullet .get().perf().glicko().provisional()){
            return bulletRating = "\uD83D\uDD2B **Bullet**:  " + userBullet .get().perf().glicko().rating().intValue();
        }

        return bulletRating;

    }

    /**
     * returns the String version of classical rating of the player
     * @return
     *
     * return String version of classical rating in Emoji
     */

    public String getClassicalRatings(){
        One<PerformanceStatistics> usercal = this.getClient().users().performanceStatisticsByIdAndType(this.getUserID(), Enums.PerfType.classical);

        String calRating = "\uD83D\uDC22 **Classical**: ?";

        if(usercal.isPresent() && !usercal.get().perf().glicko().provisional()){
            return calRating = "\uD83D\uDC22 **Classical**:  " +usercal.get().perf().glicko().rating().intValue();
        }

        return calRating;

    }

    /**
     * returns the single classical rating value of a player
     * @return
     * return an Integer value of classical rating
     *
     */


    public int getSingleClassicalRating(){

        One<PerformanceStatistics> usercal = this.getClient().users().performanceStatisticsByIdAndType(this.getUserID(), Enums.PerfType.classical);

        if(usercal == null){
            return -1;
        }

        if( usercal.get() != null && !usercal.get().perf().glicko().provisional() && usercal.isPresent()){
            return  usercal.get().perf().glicko().rating().intValue();
        }

        return -1;

    }


    /**
     * returns the single blitz rating value of a player
     * @return
     *
     * return an Integer va;ue of classical rating
     */


    public int getSingleBlitzRating(){
        One<PerformanceStatistics> userBlitz = this.getClient().users().performanceStatisticsByIdAndType(this.getUserID(), Enums.PerfType.blitz);

        if(userBlitz == null){
            return -1;
        }

        if( userBlitz.get() != null && userBlitz.isPresent() && !userBlitz.get().perf().glicko().provisional()){
            return userBlitz.get().perf().glicko().rating().intValue();
        }

        return -1;

    }



    /**
     * returns the user profile of a user
     * @return
     * returns a String containing the profile of a user
     */




    public String getUserProfile(){

        try {

            One<User> userResult = this.getClient().users().byId(this.getUserID(), params -> params.withTrophies(true));



            boolean userPresent = userResult.isPresent();



            if (userPresent) {
                User user = userResult.get();
                String StatusEmoji = "\uD83D\uDD34";
                boolean checkOnline = this.getClient().users().statusByIds(this.getUserID()).stream().toList().get(0).online();
                if(checkOnline){
                    StatusEmoji = "\uD83D\uDFE2";
                }


                boolean cheater = user.tosViolation();

                boolean closedaccount = user.disabled();

                if (cheater) {
                    return " This user has violated Lichess Terms of Service";

                }
                if (closedaccount) {
                    return "This account is closed";

                }

                String name = user.id();

                int wins = user.accountStats().win();

                int lose = user.accountStats().loss();

                int all = user.accountStats().all();

                int draw = user.accountStats().draw();

                int playing = user.accountStats().playing();


                String sayTitle = user.title().orElse("");

                StringBuilder sayrewards = new StringBuilder();
                String embedRewards = "";

                List<Trophy> trophies = user.trophies().orElse(List.of());

                for (Trophy trophy : trophies) {

                    UserTrophy userTrophy = new UserTrophy(trophy);
                    sayrewards.append(userTrophy.getImageLink()).append("\n");
                }

                if(!trophies.isEmpty()) {

                    embedRewards += "\n\n ** \uD83D\uDCA0 Trophies:** \n\n" + sayrewards;

                }else{
                    embedRewards += "";
                }


                this.sayProfile +=  sayTitle + " " + name + " " +StatusEmoji + "\n" +"**All Games**: " + all + "\n\n" + "** ⚔️ Won:** " +   wins + "\n ** \uD83D\uDE14 Loss:** " + lose + "\n ** \uD83E\uDD1D Draw:** " + draw + "\n\n** ♗ Playing:** " + playing + "\n\n \uD83D\uDCB9 **Ratings**: \n" + this.getBlitzRatings() + "\n" + this.getRapidRatings() + "\n" + this.getBulletRatings() + "\n" + this.getClassicalRatings()+ embedRewards;
            }
            if (!userPresent) {
                return "User Not Present, Please try again";

            }

        }catch(Exception e){
            e.printStackTrace();
            return "Unknown Error..";
        }

        return sayProfile;


    }




}