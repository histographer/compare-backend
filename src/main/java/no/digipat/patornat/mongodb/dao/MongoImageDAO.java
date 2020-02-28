package no.digipat.patornat.mongodb.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import no.digipat.patornat.mongodb.models.image.Image;

/**
 * A data access object (DAO) for images.
 * 
 * @author Jon Wallem Anundsen
 *
 */
public class MongoImageDAO {
    
    private final MongoCollection<Document> collection;
    
    /**
     * Creates a DAO.
     * 
     * @param client the client used to connect to the database
     * @param database the name of the database
     */
    public MongoImageDAO(MongoClient client, String database) {
        this.collection = client.getDatabase(database).getCollection("Image");
    }
    
    /**
     * Inserts a new image into the database.
     * 
     * @param image the image to be inserted 
     */
    public void createImage(Image image) {
        Document document = new Document();
        document.put("id", image.getId());
        collection.insertOne(document);
    }
    
    /**
     * Retrieves every image in the database.
     * 
     * @return a list of every image in the database
     */
    public List<Image> getAllImages() {
        final List<Image> images = new ArrayList<>();
        for (Document document : collection.find()) {
            images.add(new Image(document.getLong("id")));
        }
        return images;
    }
    
}
