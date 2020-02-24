package no.digipat.patornat.mongodb.dao;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

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
    
}
