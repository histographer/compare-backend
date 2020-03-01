package no.digipat.patornat.mongodb.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
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
     * 
     * @throws IllegalStateException if an image with the given ID already exists
     * 
     */
    public void createImage(Image image) throws IllegalStateException {
        try {
            collection.insertOne(imageToDocument(image));
        } catch (MongoWriteException e) {
            if (e.getCode() == 11000) { // Error code 11000 indicates a duplicate key
                throw new IllegalStateException("Duplicate image ID", e);
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Retrieves every image in the database.
     * 
     * @return a list of every image in the database
     */
    public List<Image> getAllImages() {
        final List<Image> images = new ArrayList<>();
        for (Document document : collection.find()) {
            images.add(documentToImage(document));
        }
        return images;
    }
    
    private static Image documentToImage(Document document) {
        Image image =  new Image().setId(document.getLong("_id"))
                .setWidth(document.getLong("width"))
                .setHeight(document.getLong("height"))
                .setDepth(document.getLong("depth"))
                .setMagnification(document.getLong("magnification"))
                .setResolution(document.getDouble("resolution"))
                .setMimeType(document.getString("mimeType"));
        List<String> urls = document.getList("imageServerURLs", String.class);
        if (urls != null) {
            image.setImageServerURLs(urls.toArray(new String[urls.size()]));
        }
        return image;
    }
    
    private static Document imageToDocument(Image image) {
        Document document = new Document();
        document.put("_id", image.getId());
        document.put("width", image.getWidth());
        document.put("height", image.getHeight());
        document.put("depth", image.getDepth());
        document.put("magnification", image.getMagnification());
        document.put("resolution", image.getResolution());
        document.put("mimeType", image.getMimeType());
        String[] imageServerURLs = image.getImageServerURLs();
        if (imageServerURLs != null) {
            document.put("imageServerURLs", Arrays.asList(imageServerURLs));
        }
        return document;
    }
    
}
