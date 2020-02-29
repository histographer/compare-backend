package no.digipat.patornat.mongodb.dao;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import no.digipat.patornat.mongodb.models.image.ImageComparison;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class MongoImageComparisonDAO {
    private MongoCollection<Document> collection;
    private String DB;



    public MongoImageComparisonDAO(MongoClient mongo, String DB) {
        this.collection = mongo.getDatabase(DB).getCollection("ImageComparison");
    }

    /**
     * Inserts a new scoring to the database
     * @param imageComparison
     * @return
     */
    public ImageComparison createImageComparison(ImageComparison imageComparison) {
        Document document = Converter.imageComparisonToDBDocument(imageComparison);
        this.collection.insertOne(document);
        return imageComparison;
    }
    
    /**
     * Retrieves every image comparison from the database.
     * 
     * @return a list of every image comparison
     */
    public List<ImageComparison> getAllImageComparisons() {
        final List<ImageComparison> comparisons = new ArrayList<>();
        for (Document document : this.collection.find()) {
            comparisons.add(Converter.dbDocumentToImageComparison(document));
        }
        return comparisons;
    }
    
}
