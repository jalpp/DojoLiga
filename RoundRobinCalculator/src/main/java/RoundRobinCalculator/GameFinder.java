package RoundRobinCalculator;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Game finder.
 */
public class GameFinder {
    private final int TimeControl;
    private final int TimeControlInc;
    private final String FEN;
    private final boolean isRated;
    private final String pairing;
    private final RoundRobinDbActions actions = new RoundRobinDbActions();
    private final Platform platform;
    private final MongoCollection<Document> RRcollection;
    private final MongoCollection<Document> RRtournamentcollection;

    private final UpdatePlayerScores updateAction = new UpdatePlayerScores();

    private final String tourneyID;

    /**
     * Instantiates a new Game finder.
     *
     * @param timeControl            the time control
     * @param timeControlInc         the time control inc
     * @param FEN                    the fen
     * @param isRated                the is rated
     * @param pairing                the pairing
     * @param platform               the platform
     * @param RRplayercollection     the r rplayercollection
     * @param rRtournamentcollection the r rtournamentcollection
     * @param tourneyID              the tourney id
     */
    public GameFinder(int timeControl, int timeControlInc, String FEN, boolean isRated, String pairing, Platform platform, MongoCollection<Document> RRplayercollection, MongoCollection<Document> rRtournamentcollection, String tourneyID) {
        TimeControl = timeControl;
        TimeControlInc = timeControlInc;
        this.FEN = FEN;
        this.isRated = isRated;
        this.pairing = pairing;
        this.platform = platform;
        this.RRcollection = RRplayercollection;
        RRtournamentcollection = rRtournamentcollection;
        this.tourneyID = tourneyID;
    }

    /**
     * Gets r rtournamentcollection.
     *
     * @return the r rtournamentcollection
     */
    public MongoCollection<Document> getRRtournamentcollection() {
        return RRtournamentcollection;
    }


    /**
     * Gets update action.
     *
     * @return the update action
     */
    public UpdatePlayerScores getUpdateAction() {
        return updateAction;
    }

    /**
     * Gets time control.
     *
     * @return the time control
     */
    public int getTimeControl() {
        return TimeControl;
    }

    /**
     * Gets actions.
     *
     * @return the actions
     */
    public RoundRobinDbActions getActions() {
        return actions;
    }

    /**
     * Gets tourney id.
     *
     * @return the tourney id
     */
    public String getTourneyID() {
        return tourneyID;
    }

    /**
     * Gets platform.
     *
     * @return the platform
     */
    public Platform getPlatform() {
        return platform;
    }

    /**
     * Gets r rcollection.
     *
     * @return the r rcollection
     */
    public MongoCollection<Document> getRRcollection() {
        return RRcollection;
    }

    /**
     * Gets time control inc.
     *
     * @return the time control inc
     */
    public int getTimeControlInc() {
        return TimeControlInc;
    }

    /**
     * Gets fen.
     *
     * @return the fen
     */
    public String getFEN() {
        return FEN;
    }

    @Override
    public String toString() {
        return "GameFinder{" +
                ", TimeControl=" + TimeControl +
                ", TimeControlInc=" + TimeControlInc +
                ", FEN='" + FEN + '\'' +
                ", isRated=" + isRated +
                ", pairing='" + pairing + '\'' +
                ", actions=" + actions +
                ", platform=" + platform +
                '}';
    }

    /**
     * Is rated boolean.
     *
     * @return the boolean
     */
    public boolean isRated() {
        return isRated;
    }

    /**
     * Extract result string.
     *
     * @param pgn the pgn
     * @return the string
     */
    public String extractResult(String pgn) {
        String result = null;
        Pattern pattern = Pattern.compile("\\[Result \"([^\"]+)\"\\]");
        Matcher matcher = pattern.matcher(pgn);
        if (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    /**
     * Gets crosstable generator.
     *
     * @return the crosstable generator
     */
    public RoundRobinCrosstable getCrosstableGenerator() {
        return new RoundRobinCrosstable(RRtournamentcollection, RRcollection, tourneyID);
    }

    /**
     * Gets pairing.
     *
     * @return the pairing
     */
    public String getPairing() {
        return pairing;
    }
}
