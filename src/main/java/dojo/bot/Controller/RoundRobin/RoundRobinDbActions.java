package dojo.bot.Controller.RoundRobin;

import chariot.Client;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import dojo.bot.Controller.Database.MongoConnect;
import dojo.bot.Controller.User.CCProfile;
import dojo.bot.Controller.User.Profile;
import dojo.bot.Controller.User.Verification;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Round robin db actions.
 */
public class RoundRobinDbActions {



    private final int MAX_PLAYER_SIZE = 10;

    private final Verification verification = new Verification();


    /**
     * Get tournament doc for start cohort document.
     *
     * @param RRcollection the r rcollection
     * @param startCohort  the start cohort
     * @return the document
     */
    public Document getTournamentDocForStartCohort (MongoCollection<Document> RRcollection, int startCohort){
        Document query = new Document("cohort-start", startCohort);
        FindIterable<Document> finder = RRcollection.find(query);

        for(Document doc: finder){
            if(doc.getList("players", String.class).size() < MAX_PLAYER_SIZE){
                return doc;
            }
        }

        return null;
    }

    /**
     * Perform general search document.
     *
     * @param collection the collection
     * @param key        the key
     * @param val        the val
     * @return the document
     */
    public Document performGeneralSearch( @NotNull MongoCollection<Document> collection, String key, String val){
        Document query = new Document(key, val);
        return collection.find(query).first();
    }


    /**
     * Already registered in tournament boolean.
     *
     * @param RRcollection the r rcollection
     * @param Discordname  the discordname
     * @return the boolean
     */
    public boolean alreadyRegisteredInTournament(MongoCollection<Document> RRcollection, String Discordname){
        Document finder = performGeneralSearch(RRcollection, "players", Discordname);
        return finder != null;
    }


    /**
     * Get registered player tournament id document.
     *
     * @param RRplayercollection the r rplayercollection
     * @param RRcollection       the r rcollection
     * @param playerName         the player name
     * @param platform           the platform
     * @return the document
     */
    public Document getRegisteredPlayerTournamentID(MongoCollection<Document> RRplayercollection, MongoCollection<Document> RRcollection, String playerName, Platform platform){

        Document query = null;

        switch (platform){
            case LICHESS -> {
                query = performGeneralSearch(RRplayercollection, "Lichessname", playerName);
                String convertDiscordName = query.getString("Discordname");
                return RRcollection.find(new Document("players", convertDiscordName)).first();
            }
            case CHESSCOM -> {
                query = performGeneralSearch(RRplayercollection, "Chesscomname", playerName);
                String convertDiscordName = query.getString("Discordname");
                return RRcollection.find(new Document("players", convertDiscordName)).first();
            }
            case DISCORD -> {
                query = new Document("players", playerName);
                return RRcollection.find(query).first();
            }
        }

        return query;

    }


    /**
     * Get tournament id doc document.
     *
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @return the document
     */
    public Document getTournamentIDDoc (MongoCollection<Document> RRcollection, String tournamentID){
        Document query = new Document("tournamentId", tournamentID);
        return RRcollection.find(query).first();
    }

    /**
     * Create new round robin tournament string.
     *
     * @param name         the name
     * @param desc         the desc
     * @param cohortRange  the cohort range
     * @param mode         the mode
     * @param RRcollection the r rcollection
     * @return the string
     */
    public String createNewRoundRobinTournament(String name, String desc, CohortRange cohortRange, boolean mode, MongoCollection<Document> RRcollection){

        String tournamentID = UUID.randomUUID().toString();

        Document document = new Document("tournamentId", tournamentID)
                .append("name", name) // tournament name
                .append("desc", desc) // tournament desc
                .append("status", "closed") // running or finished or closed
                .append("tc", cohortRange.getTimeControl()) // cohort tc
                .append("inc", cohortRange.getTimeIncrement()) // cohort inc
                .append("fen", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
                .append("israted", true) // check is rated
                .append("cohort-start", cohortRange.getStart()) // cohort.values()
                .append("cohort-end", cohortRange.getEnd())
                .append("leaderboard", new ArrayList<String>())
                .append("automode", mode) // auto or manual
                .append("player-size", MAX_PLAYER_SIZE) // 10
                .append("players", new ArrayList<String>()) // player list
                .append("pairings", "not computed") // string pairings
                .append("crosstable", "not computed") // tournament crosstable
                .append("crosstable-data", new ArrayList<ArrayList<String>>())
                .append("game-submissions", new ArrayList<String>()); // game list

        RRcollection.insertOne(document);

        return tournamentID;

    }

    /**
     * Open tournament to calculation.
     *
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @throws RoundRobinException the round robin exception
     */
    public void openTournamentToCalculation(MongoCollection<Document> RRcollection, String tournamentID) throws RoundRobinException {

        Document openTourney = getTournamentIDDoc(RRcollection, tournamentID);

        if(openTourney != null){

            if(openTourney.getList("players", String.class).size() < 3){
                throw new RoundRobinException("Tournament Can't be opened due to less than 10 players!");
            }

            UpdateResult result = RRcollection.updateOne(
                   openTourney,
                    Updates.set("status", "running")
            );
        }else{
            throw new RoundRobinException("Invalid ID, can't open unknown tournament!");
        }

    }

    /**
     * Finish tournament to players.
     *
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @throws RoundRobinException the round robin exception
     */
    public void finishTournamentToPlayers(MongoCollection<Document> RRcollection, String tournamentID) throws RoundRobinException {

        Document openTourney = getTournamentIDDoc(RRcollection, tournamentID);

        if(openTourney != null){
            if(openTourney.getString("status").equalsIgnoreCase("closed") || openTourney.getString("status").equalsIgnoreCase("closed")){
                throw new RoundRobinException("Invalid state error! Can't close a closed tournament! Please open the tournament first");
            }
            UpdateResult result = RRcollection.updateOne(
                    openTourney,
                    Updates.set("status", "finished")

            );
        }else{
            throw new RoundRobinException("Invalid ID, can't finish unknown tournament!");
        }

    }


    /**
     * Create new player.
     *
     * @param Lichessname        the lichessname
     * @param Chesscomname       the chesscomname
     * @param DiscordName        the discord name
     * @param DiscordID          the discord id
     * @param score              the score
     * @param RRplayercollection the r rplayercollection
     */
    public void createNewPlayer(String Lichessname, String Chesscomname, String DiscordName, String DiscordID, double score, MongoCollection<Document> RRplayercollection){
       Document document = new Document("Lichessname", Lichessname)
               .append("Chesscomname", Chesscomname)
               .append("Discordid", DiscordID)
               .append("Discordname", DiscordName)
               .append("score", score);
       System.out.println("Successfully added Player into Round Robin Collection");
       RRplayercollection.insertOne(document);
   }


    /**
     * Add player to db boolean.
     *
     * @param DiscordID          the discord id
     * @param DiscordName        the discord name
     * @param RRplayerCollection the r rplayer collection
     * @return the boolean
     */
    public boolean addPlayerToDB(String DiscordID, String DiscordName, MongoCollection<Document> RRplayerCollection){

      if(verification.userPresentNormal(RRplayerCollection, DiscordID)){
          return true;
      }

       if(verification.userPresentNormal(MongoConnect.getLichessplayers(), DiscordID) || verification.userPresentNormal(MongoConnect.getChesscomplayers(), DiscordID)){
           String Lichessname = verification.getReletatedLichessName(DiscordID, MongoConnect.getLichessplayers());
           String Chesscomname = verification.getReletatedChessName(DiscordID, MongoConnect.getChesscomplayers());
           createNewPlayer(Lichessname, Chesscomname, DiscordName , DiscordID, 0.0,  RRplayerCollection);


           return true;
       }

       return false;
   }

    /**
     * Admin force push player to tournament.
     *
     * @param username           the username
     * @param platform           the platform
     * @param tournamentID       the tournament id
     * @param RRplayerCollection the r rplayer collection
     * @param RRcollection       the r rcollection
     * @throws RoundRobinException the round robin exception
     */
    public void AdminForcePushPlayerToTournament(String username, Platform platform, String tournamentID , MongoCollection<Document> RRplayerCollection, MongoCollection<Document> RRcollection) throws RoundRobinException {

       Document tourneyID = getTournamentIDDoc(RRcollection, tournamentID);

       if(tourneyID != null) {
           switch (platform) {
               case LICHESS -> {
                   createNewPlayer(username, "null", "null", "null", 0.0,  RRplayerCollection);
                   addPlayerToTournamentSimpleAlgo(tourneyID, RRcollection, tournamentID, username);
               }
               case DISCORD -> {
                   createNewPlayer("null", "null", username, "null" ,0.0,  RRplayerCollection);
                   addPlayerToTournamentSimpleAlgo(tourneyID, RRcollection, tournamentID, username);
               }
               case CHESSCOM -> {
                   createNewPlayer("null", username, "null", "null" ,0.0, RRplayerCollection);
                   addPlayerToTournamentSimpleAlgo(tourneyID, RRcollection, tournamentID, username);
               }
           }

       }else{
           throw new RoundRobinException("Invalid tournament ID!");
       }

   }


    /**
     * Add player to tournament simple algo.
     *
     * @param tourneyID    the tourney id
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @param username     the username
     * @throws RoundRobinException the round robin exception
     */
    public void addPlayerToTournamentSimpleAlgo(Document tourneyID, MongoCollection<Document> RRcollection, String tournamentID, String username) throws RoundRobinException {
       List<String> currentPlayerCount = tourneyID.getList("players", String.class);
       if(!(currentPlayerCount.size() <= MAX_PLAYER_SIZE)) {
           throw new RoundRobinException("Player can not sign up due to hitting registration limit");
       } else if (currentPlayerCount.contains(username)) {
           throw new RoundRobinException("Same player can't be added in the tournament!");
       }


       UpdateResult result = RRcollection.updateOne(
               new Document("tournamentId", tournamentID),
               Updates.push("players", username)
       );

       if(currentPlayerCount.size() >= 4 && currentPlayerCount.size() <= MAX_PLAYER_SIZE){
           String pairs = getRoundRobinPairingsInternally(RRcollection, tournamentID);
           openTournamentToCalculation(RRcollection, tournamentID);
       }

       if (result.getModifiedCount() > 0) {
           System.out.println("Player " + username + " added successfully");
       } else {
           throw new RoundRobinException("Internal Error");
       }
   }

    /**
     * Withdraw player to tournament simple algo.
     *
     * @param tourneyID    the tourney id
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @param username     the username
     * @throws RoundRobinException the round robin exception
     */
    public void withdrawPlayerToTournamentSimpleAlgo(Document tourneyID, MongoCollection<Document> RRcollection, String tournamentID, String username) throws RoundRobinException {
        List<String> currPlayers = tourneyID.getList("players", String.class);
        RoundRobinCrosstable crosstableManager = new RoundRobinCrosstable(RRcollection, MongoConnect.getRRplayercollection(), tournamentID);
        boolean scoresSheetPresent = isScoresSheetPresent(username, crosstableManager, currPlayers);

       if(scoresSheetPresent){
           for(String player: currPlayers){
               crosstableManager.updateCrossTableScores(player, username, GameState.PLAYER_ONE_WON);
           }
           currPlayers.remove(username);

           for(String player: currPlayers){
               pushPlayerScore(player, MongoConnect.getRRplayercollection(), Platform.DISCORD, 1);
           }
       }

       UpdateResult result = RRcollection.updateOne(
               new Document("tournamentId", tournamentID),
               Updates.pull("players", username)
       );

   }

    private boolean isScoresSheetPresent(String username, RoundRobinCrosstable crosstableManager, List<String> currPlayers) throws RoundRobinException {
        List<ArrayList<String>> crosstable = crosstableManager.getTournamentCrosstable();

        boolean scoresSheetPresent = false;

        if(!currPlayers.contains(username)){
            throw new RoundRobinException("Player not found and can't be withdrawn!");
        }


        for(ArrayList<String> scoresheet: crosstable){
            for(String sheet: scoresheet){
                if (sheet.equalsIgnoreCase("1/2") || sheet.equalsIgnoreCase("1") || sheet.equalsIgnoreCase("0")) {
                    scoresSheetPresent = true;
                    break;
                }
            }
        }
        return scoresSheetPresent;
    }


    /**
     * Gets round robin pairings internally.
     *
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @return the round robin pairings internally
     * @throws RoundRobinException the round robin exception
     */
    public String getRoundRobinPairingsInternally(MongoCollection<Document> RRcollection, String tournamentID) throws RoundRobinException {
       Document tournamentDoc = getTournamentIDDoc(RRcollection, tournamentID);

       if(tournamentDoc != null){
           try{
               RoundRobin roundRobin = new RoundRobin(tournamentDoc.getList("players", String.class), tournamentDoc.getString("name"),
                       tournamentDoc.getString("desc"), Client.basic(), CohortRange.findCohortRange(tournamentDoc.getInteger("cohort-start"),
                       tournamentDoc.getInteger("cohort-end")), tournamentDoc.getBoolean("automode"));

               String pairings = roundRobin.createTournamentPairings();

               System.out.println(roundRobin.toString());
               System.out.println(pairings);

               pushPairingForRunningTournament(RRcollection, pairings, tournamentID);

               return "I have successfully generated the pairings!";
           }catch (RoundRobinException e){
               return e.getMessage();
           }
       }else{
           throw new RoundRobinException("Invalid Tournament ID!");
       }

   }

    /**
     * Gets round robin internally.
     *
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @return the round robin internally
     * @throws RoundRobinException the round robin exception
     */
    public RoundRobin getRoundRobinInternally(MongoCollection<Document> RRcollection, String tournamentID) throws RoundRobinException {
        Document tournamentDoc = getTournamentIDDoc(RRcollection, tournamentID);

        if(tournamentDoc != null){

            return new RoundRobin(tournamentDoc.getList("players", String.class), tournamentDoc.getString("name"),
                    tournamentDoc.getString("desc"), Client.basic(), CohortRange.findCohortRange(tournamentDoc.getInteger("cohort-start"),
                    tournamentDoc.getInteger("cohort-end")), tournamentDoc.getBoolean("automode"));
        }else{
            throw new RoundRobinException("Invalid Tournament ID!");
        }

    }

    /**
     * Gets players eligible tournament id.
     *
     * @param DiscordID          the discord id
     * @param RRplayerCollection the r rplayer collection
     * @param RRcollection       the r rcollection
     * @param cohortRange        the cohort range
     * @return the players eligible tournament id
     * @throws RoundRobinException     the round robin exception
     * @throws ChessComPubApiException the chess com pub api exception
     * @throws IOException             the io exception
     */
    public String getPlayersEligibleTournamentID(String DiscordID, MongoCollection<Document> RRplayerCollection, MongoCollection<Document> RRcollection, CohortRange cohortRange) throws RoundRobinException, ChessComPubApiException, IOException {

        if(cohortRange == null){
            String Lichessname = verification.getReletatedLichessName(DiscordID, RRplayerCollection);
            String Chesscomname = verification.getReletatedChessName(DiscordID, RRplayerCollection);
            Profile profile = new Profile(Client.basic(), Lichessname);
            CCProfile ccProfile = new CCProfile(Chesscomname);
            CohortRange cohort = CohortRange.getMaxCohortPerPlatform(ccProfile.getRapidRating(), profile.getSingleClassicalRating());
            if(cohort == null){
                throw new RoundRobinException("Invalid player cohort!");
            }else{
                return getTournamentIDForStartCohort(RRcollection, cohort);
            }
        }else{
            return getTournamentIDForStartCohort(RRcollection, cohortRange);
        }

   }

    /**
     * Gets tournament id for start cohort.
     *
     * @param RRcollection the r rcollection
     * @param cohortRange  the cohort range
     * @return the tournament id for start cohort
     * @throws RoundRobinException the round robin exception
     */
    public String getTournamentIDForStartCohort(MongoCollection<Document> RRcollection, CohortRange cohortRange) throws RoundRobinException{
       Document eligibleDoc = getTournamentDocForStartCohort(RRcollection, cohortRange.getStart());

       if(eligibleDoc != null){
           return eligibleDoc.getString("tournamentId");
       }else{
           throw new RoundRobinException("No Cohort: " + cohortRange.getStart() + " tournament found to register the player!");
       }
   }


    /**
     * Search the ambiguous username boolean.
     *
     * @param username           the username
     * @param platform           the platform
     * @param RRplayercollection the r rplayercollection
     * @return the boolean
     */
    public boolean searchTheAmbiguousUsername(String username, Platform platform, MongoCollection<Document> RRplayercollection){

        switch (platform){
            case LICHESS -> {
                return performGeneralSearch(RRplayercollection, "Lichessname", username) != null;
            }
            case CHESSCOM -> {
                return performGeneralSearch(RRplayercollection, "Chesscomname", username) != null;
            }
            case DISCORD -> {
                return performGeneralSearch(RRplayercollection, "Discordname", username) != null;
            }
        }

       return false;
   }


    /**
     * Push player score.
     *
     * @param username           the username
     * @param RRplayercollection the r rplayercollection
     * @param platform           the platform
     * @param newScore           the new score
     */
    public void pushPlayerScore(@NotNull String username, @NotNull MongoCollection<Document> RRplayercollection, @NotNull  Platform platform, double newScore){

        Document searchAmb = null;

        switch (platform){
            case LICHESS -> searchAmb = performGeneralSearch(RRplayercollection, "Lichessname", username);
            case CHESSCOM -> searchAmb = performGeneralSearch(RRplayercollection, "Chesscomname", username);
            case DISCORD -> searchAmb = performGeneralSearch(RRplayercollection, "Discordname", username);
        }

       RRplayercollection.updateOne(searchAmb, Updates.inc("score", newScore));
       System.out.println("Successfully updated the player " + username + " Score by: " + newScore);

   }


    /**
     * Add player to running tournament.
     *
     * @param playerUsername the player username
     * @param RRcollection   the r rcollection
     * @param tournamentID   the tournament id
     * @throws RoundRobinException the round robin exception
     */
    public void addPlayerToRunningTournament(String playerUsername, MongoCollection<Document> RRcollection, String tournamentID) throws RoundRobinException {

       Document firstActiveTournament = getTournamentIDDoc(RRcollection, tournamentID);

       if (firstActiveTournament != null) {
           String tournamentId = firstActiveTournament.getString("tournamentId");
           System.out.println("First Active Eligible Tournament ID: " + tournamentId);
           addPlayerToTournamentSimpleAlgo(firstActiveTournament, RRcollection, tournamentId, playerUsername);
       }else{
           throw new RoundRobinException("No Round Robin found internal error!");
       }




   }

    /**
     * Remove player to running tournament.
     *
     * @param playerUsername the player username
     * @param RRcollection   the r rcollection
     * @param tournamentID   the tournament id
     * @throws RoundRobinException the round robin exception
     */
    public void removePlayerToRunningTournament(String playerUsername, MongoCollection<Document> RRcollection, String tournamentID) throws RoundRobinException {
        Document activeTournament = getTournamentIDDoc(RRcollection, tournamentID);

        if(activeTournament != null){
            String tournamentId = activeTournament.getString("tournamentId");
            System.out.println("First Active Eligible Tournament ID: " + tournamentId);
            withdrawPlayerToTournamentSimpleAlgo(activeTournament, RRcollection, tournamentID, playerUsername);
        }else{
            throw new RoundRobinException("No Round Robin found internal error!");
        }


   }


    /**
     * Gets pairing from running tournament.
     *
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @return the pairing from running tournament
     * @throws RoundRobinException the round robin exception
     */
    public String getPairingFromRunningTournament(MongoCollection<Document> RRcollection, String tournamentID) throws RoundRobinException {

       Document tournamentDoc = getTournamentIDDoc(RRcollection, tournamentID);

       if (tournamentDoc != null) {
           String tournamentId = tournamentDoc.getString("tournamentId");
           System.out.println("First Active Tournament ID: " + tournamentId);

           return tournamentDoc.getString("pairings");


       } else {
           throw new RoundRobinException("Invalid Tournament ID!");
       }
   }

    /**
     * Push pairing for running tournament.
     *
     * @param RRcollection the r rcollection
     * @param pairings     the pairings
     * @param tournamentID the tournament id
     * @throws RoundRobinException the round robin exception
     */
    public void pushPairingForRunningTournament(MongoCollection<Document> RRcollection, String pairings, String tournamentID) throws RoundRobinException{
           RRcollection.updateOne(
                   new Document("tournamentId", tournamentID),
                   Updates.set("pairings", pairings)
           );
   }

    /**
     * Gets game submissions from running tournament.
     *
     * @param RRcollection the r rcollection
     * @param tournamentID the tournament id
     * @return the game submissions from running tournament
     * @throws RoundRobinException the round robin exception
     */
    public List<String> getGameSubmissionsFromRunningTournament(MongoCollection<Document> RRcollection, String tournamentID) throws RoundRobinException {

        Document tournamentDoc = getTournamentIDDoc(RRcollection, tournamentID);

        if (tournamentDoc != null) {
            String tournamentId = tournamentDoc.getString("tournamentId");
            System.out.println("First Active Tournament ID: " + tournamentId);

            return tournamentDoc.getList("game-submissions", String.class);


        } else {
            throw new RoundRobinException("Invalid Tournament ID!");
        }
    }


    /**
     * Push game submission for running tournament.
     *
     * @param RRcollection the r rcollection
     * @param gameInfo     the game info
     * @param tournamentID the tournament id
     */
    public void pushGameSubmissionForRunningTournament(MongoCollection<Document> RRcollection, String gameInfo, String tournamentID){

            RRcollection.updateOne(
                    new Document("tournamentId", tournamentID),
                    Updates.push("game-submissions", gameInfo)
            );


    }

    /**
     * Push crosstable string.
     *
     * @param RRcollection the r rcollection
     * @param crosstable   the crosstable
     * @param tournamentID the tournament id
     */
    public void pushCrosstableString(MongoCollection<Document> RRcollection, String crosstable, String tournamentID){
        RRcollection.updateOne(
                new Document("tournamentId", tournamentID),
                Updates.set("crosstable", crosstable)
        );
    }

    /**
     * Push cross table list.
     *
     * @param RRcollection   the r rcollection
     * @param crosstableList the crosstable list
     * @param tournamentID   the tournament id
     */
    public void pushCrossTableList(MongoCollection<Document> RRcollection, ArrayList<ArrayList<String>> crosstableList, String tournamentID){
        RRcollection.updateOne(
                new Document("tournamentId", tournamentID),
                Updates.set("crosstable-data", crosstableList)
        );
    }







}
