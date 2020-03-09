package no.digipat.compare.mongodb.dao;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.image.ImageChoice;
import no.digipat.compare.models.image.ImageComparison;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

public class MongoImageComparisonDAO {
    private MongoCollection<Document> collection;
    private String databaseName;
    private MongoClient client;

    public MongoImageComparisonDAO(MongoClient mongo, String DB) {
        this.collection = mongo.getDatabase(DB).getCollection("ImageComparison");
        this.databaseName = DB;
        this.client = mongo;
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
    
    /**
     * Retrieves the number of times each image in the database has been compared.
     * 
     * @return a list of map entries, each of whose key is an image ID and value
     * is the number of times that image has been compared
     */
    public List<Map.Entry<Long, Long>> getNumberOfComparisonsForEachImage() {
        final List<Map.Entry<Long, Long>> numbers = new ArrayList<>();
        MongoImageDAO imageDao = new MongoImageDAO(client, databaseName);
        for (Image image : imageDao.getAllImages()) {
            Long id = image.getId();
            long count = collection.countDocuments(or(eq("chosen.id", id), eq("other.id", id)));
            numbers.add(new Map.Entry<Long, Long>() {
                @Override
                public Long getKey() {
                    return id;
                }
                @Override
                public Long getValue() {
                    return count;
                }
                @Override
                public Long setValue(Long value) {return null;}
            });
        }
        return numbers;
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
                .append("id", imageComparison.getUser());
    }
    
    private static ImageComparison dbDocumentToImageComparison(Document document) {
        try {
            String user = document.getString("id");
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
