package no.digipat.patornat.mongodb.dao;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import no.digipat.patornat.mongodb.models.image.BestImageChoice;
import org.bson.Document;

public class MongoBestImageDAO {
    private MongoCollection<Document> collection;



    public MongoBestImageDAO(MongoClient mongo, String database) {
        this.collection = mongo.getDatabase(database).getCollection("BestImage");
    }

    /**
     * Inserts a new scoring to the database
     * @param bestImageChoice
     * @return
     */
    public BestImageChoice createBestImage(BestImageChoice bestImageChoice) {
        Document document = Converter.bestImageToDBDocument(bestImageChoice);
        this.collection.insertOne(document);
        return bestImageChoice;
    }
}
