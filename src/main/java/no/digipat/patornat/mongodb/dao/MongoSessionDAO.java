package no.digipat.patornat.mongodb.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import javassist.NotFoundException;
import no.digipat.patornat.mongodb.models.session.Session;
import org.bson.Document;
import org.json.simple.JSONObject;

import static com.mongodb.client.model.Filters.eq;

/**
 * A data access object (DAO) for images.
 *
 * @author Kent Are Torvik
 *
 */
public class MongoSessionDAO {
    private MongoCollection<Document> collection;


    /**
     * Creates a DAO.
     *
     * @param client the client used to connect to the database
     * @param database the name of the database
     */
    public MongoSessionDAO(MongoClient client, String database) {
        this.collection = client.getDatabase(database).getCollection("sessions");
    }


    /**
     * inserts a new session to the database.
     *
     * @param session the session
     */
    public void createSession (Session session) {
        this.collection.insertOne(sessionToDocument(session));
    }

    /**
     * Checks if a session exists in the database.
     *
     * @param id the id of the session
     * @return boolean
     */
    public boolean sessionExists(String id) {
        try {
            Session session = getSession(id);
            return true;
        } catch(NotFoundException e) {
            return false;
        }
    }

    /**
     * Gets an existing session from the database.
     *
     * @param id the id
     * @return the session
     * @throws NotFoundException if not found exception
     */
    public Session getSession(String id) throws NotFoundException {
       Document session = this.collection.find(eq("_id", id)).first();
       if(session == null) {
           throw new NotFoundException("There is no session with this id that exists in the database");
       }
       return documentToSession(session);
    }

    private static Document sessionToDocument(Session session) {
        Document document = new Document();
        document.put("_id", session.getId());
        document.put("hospital", session.getHospital());
        document.put("monitorType", session.getMonitorType());
        return document;
    }

    private static Session documentToSession(Document document) {
        Session session = new Session().setId(document.getString("_id"))
                .setHospital(document.getString("hosital"))
                .setMonitorType(document.getString("monitorType"));
        return session;
    }


}
