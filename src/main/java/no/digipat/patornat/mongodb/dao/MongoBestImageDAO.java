package no.digipat.patornat.mongodb.dao;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import no.digipat.patornat.mongodb.models.image.BestImageChoice;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class MongoBestImageDAO {
    private MongoCollection<Document> collection;
    private String DB;



    public MongoBestImageDAO(MongoClient mongo, String DB) {
        this.collection = mongo.getDatabase(DB).getCollection("BestImage");
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
    
    /**
     * Retrieves every choice of best image from the database.
     * 
     * @return a list of every choice of best image
     */
    public List<BestImageChoice> getAllBestImageChoices() {
        final List<BestImageChoice> choices = new ArrayList<>();
        for (Document document : this.collection.find()) {
            choices.add(Converter.dbDocumentToBestImageChoice(document));
        }
        return choices;
    }
    
}
