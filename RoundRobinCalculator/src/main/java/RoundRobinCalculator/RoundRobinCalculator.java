package RoundRobinCalculator;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Round robin calculator.
 */
public class RoundRobinCalculator {


    private final RoundRobinDbActions actions = new RoundRobinDbActions();
    private final MongoCollection<Document> RRcollection;
    private final MongoCollection<Document> RRplayercollection;

    /**
     * Instantiates a new Round robin calculator.
     *
     * @param RRcollection       the r rcollection
     * @param RRplayercollection the r rplayercollection
     */
    public RoundRobinCalculator(MongoCollection<Document> RRcollection, MongoCollection<Document> RRplayercollection) {
        this.RRcollection = RRcollection;
        this.RRplayercollection = RRplayercollection;
    }


    private ArrayList<String> getPairingsInListFormat(String tournamentID) throws RoundRobinException {
        String pair = actions.getPairingFromRunningTournament(RRcollection, tournamentID);

        Pattern pattern = Pattern.compile("\\*\\*Round \\d+:\\*\\* \\[(.*?)\\]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(pair);

        ArrayList<String> roundsList = new ArrayList<>();

        while (matcher.find()) {
            String roundContent = matcher.group(1).trim();
            roundsList.add(roundContent);
        }

        return roundsList;
    }

    /**
     * Perform calculation.
     *
     * @param tournamentID the tournament id
     * @throws RoundRobinException  the round robin exception
     * @throws InterruptedException the interrupted exception
     */
    public void performCalculation(String tournamentID) throws RoundRobinException, InterruptedException {

        Document tournamentDoc = actions.getTournamentIDDoc(RRcollection, tournamentID);
        PairingBuilder builder = new PairingBuilder();

        if (tournamentDoc == null) {
            throw new RoundRobinException("Invalid Tournament ID!");
        }

        int tc = tournamentDoc.getInteger("tc");

        int inc = tournamentDoc.getInteger("inc");

        new RoundRobinCrosstable(RRcollection, RRplayercollection, tournamentID).createCrossTable();

        Boolean israted = tournamentDoc.getBoolean("israted");

        String FEN = tournamentDoc.getString("fen");

        ArrayList<String> roundlist = getPairingsInListFormat(tournamentID);

        for (String r : roundlist) {

            String[] actualPairs = r.split(", ");

            for (String pair : actualPairs) {

                ChessComFinderStrategy chessComFinderStrategy = new ChessComFinderStrategy(
                        tc, inc, FEN, israted, pair, RRplayercollection, RRcollection, tournamentID
                );

                String[] findGameData = builder.buildPairingTransformer(pair);

                boolean isSuccess = chessComFinderStrategy.findGame(findGameData[0], findGameData[1], findGameData[2], findGameData[3]);

                if (isSuccess) {
                    System.out.println("Successfully Found And Updated Scores for Match: " + pair + " On Platform: " + chessComFinderStrategy.getPlatform());
                } else {
                    Thread.sleep(6000);

                    LichessFinderStrategy lichessFinderStrategy = new LichessFinderStrategy(
                            tc, inc, FEN, israted, pair, RRplayercollection, RRcollection, tournamentID
                    );

                    String[] findlichessdata = builder.buildPairingTransformer(pair);

                    boolean isLichessSuccess = lichessFinderStrategy.findGame(findlichessdata[0], findlichessdata[1], findlichessdata[2], findlichessdata[3]);

                    if (isLichessSuccess) {
                        System.out.println("Successfully Found And Updated Scores for Match: " + pair + " On Platform: " + lichessFinderStrategy.getPlatform());
                    } else {
                        System.out.println("Failed to Find and Update Scores for Match: " + pair);
                    }

                }

            }

        }


    }


}
