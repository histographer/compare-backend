package no.digipat.patornat.mongodb.dao;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import no.digipat.patornat.mongodb.models.image.BestImageChoice;
import org.bson.Document;

public class MongoBestImageDAO {
    private MongoCollection<Document> collection;
    private String DB = System.getenv("PATORNAT_MONGODB_DATABASE");


    public MongoBestImageDAO(MongoClient mongo) {
        this.collection = mongo.getDatabase(DB).getCollection("BestImage");
    }

    /**
     * Inserts a new scoring to the database
     * @param bestImageChoice
     * @return
     */
    public BestImageChoice createBestImage(BestImageChoice bestImageChoice) {
        Document document = Converter.bestImageToDBDocument(bestImageChoice);
        System.out.println(document);
        this.collection.insertOne(document);
        return bestImageChoice;
    }
}
