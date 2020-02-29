package no.digipat.patornat.mongodb.dao;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import no.digipat.patornat.mongodb.models.image.ImageChoice;
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
        Document document = imageComparisonToDBDocument(imageComparison);
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
            comparisons.add(dbDocumentToImageComparison(document));
        }
        return comparisons;
    }
    
    private static Document imageChoiceToDBDocument(ImageChoice imageChoice) {
       return new Document()
               .append("id", imageChoice.getId())
               .append("comment", imageChoice.getComment());
    }
    
    private static Document imageComparisonToDBDocument(ImageComparison imageComparison) {
        return new Document().
                append("chosen", imageChoiceToDBDocument(imageComparison.getChosen()))
                .append("other", imageChoiceToDBDocument(imageComparison.getOther()))
                .append("user", imageComparison.getUser());
    }
    
    private static ImageComparison dbDocumentToImageComparison(Document document) {
        try {
            String user = document.getString("user");
            Document chosenDoc = (Document) document.get("chosen");
            long chosenId = (Long) chosenDoc.get("id");
            String chosenComment = chosenDoc.getString("comment");
            ImageChoice chosen = new ImageChoice(chosenId, chosenComment);
            Document otherDoc = (Document) document.get("other");
            long otherId = (Long) otherDoc.get("id");
            String otherComment = otherDoc.getString("comment");
            ImageChoice other = new ImageChoice(otherId, otherComment);
            ImageComparison imageComparison = new ImageComparison(user, chosen, other);
            return imageComparison;
        } catch (NullPointerException | ClassCastException e) {
            throw new IllegalArgumentException("Invalid document", e);
        }
    }
    
}
