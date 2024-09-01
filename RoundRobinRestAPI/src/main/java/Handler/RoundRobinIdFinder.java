package Handler;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;

/**
 * The type Round robin id finder.
 */
public class RoundRobinIdFinder {


    private final int MAX_PLAYER_SIZE = 10;

    private ArrayList<Document> getTournamentDocForStartCohort(MongoCollection<Document> RRcollection, int startCohort) {
        Document query = new Document("cohort-start", startCohort);
        FindIterable<Document> finder = RRcollection.find(query);
        ArrayList<Document> list = new ArrayList<>();

        for (Document doc : finder) {
                list.add(doc);
        }

        return list;
    }

    /**
     * Gets tournament id for cohort.
     *
     * @param RRcollection the r rcollection
     * @param startCohort  the start cohort
     * @return the tournament id for cohort
     */
    public ArrayList<String> getTournamentIDForCohort(MongoCollection<Document> RRcollection, int startCohort) {

        ArrayList<Document> documents = getTournamentDocForStartCohort(RRcollection, startCohort);
        ArrayList<String> ids = new ArrayList<>();
        for (Document doc : documents) {
            ids.add(doc.getString("tournamentId").replace(" ", "").trim());
        }

        return ids;

    }


}
