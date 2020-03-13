package no.digipat.compare.mongodb.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import no.digipat.compare.models.image.Image;

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
     * @throws IllegalStateException if an image with the given combination of
     * image ID and project ID already exists
     * @throws NullPointerException if {@code image}, {@code image.getImageId()},
     * or {@code image.getProjectId()} is {@code null}
     * 
     */
    public void createImage(Image image) throws IllegalStateException {
        try {
            collection.insertOne(imageToDocument(image));
        } catch (MongoWriteException e) {
            if (e.getCode() == 11000) { // Error code 11000 indicates a duplicate key
                throw new IllegalStateException("Duplicate combination of image ID and project ID", e);
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Retrieves every image in a given project.
     * 
     * @param projectId the ID of the project
     * 
     * @return a list of every image in the project
     */
    public List<Image> getAllImages(long projectId) {
        final List<Image> images = new ArrayList<>();
        for (Document document : collection.find(Filters.eq("_id.projectId", projectId))) {
            images.add(documentToImage(document));
        }
        return images;
    }
    
    private static Image documentToImage(Document document) {
        Document compositeKey = (Document) document.get("_id");
        Image image =  new Image().setImageId(compositeKey.getLong("imageId"))
                .setProjectId(compositeKey.getLong("projectId"))
                .setFileName(document.getString("fileName"))
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
        DBObject compositeKey = new BasicDBObject();
        compositeKey.put("imageId", (long) image.getImageId());
        compositeKey.put("projectId", (long) image.getProjectId());
        document.put("_id", compositeKey);
        document.put("fileName", image.getFileName());
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
