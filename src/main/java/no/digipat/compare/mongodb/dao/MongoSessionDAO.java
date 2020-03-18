package no.digipat.compare.mongodb.dao;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;

import no.digipat.compare.models.session.Session;

import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

/**
 * A data access object (DAO) for sessions.
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
     * @param session the session that has to be inserted
     *
     * @throws IllegalStateException if a session with the given ID already exists
     * @throws NullPointerException if {@code session} or {@code session.getId()} is {@code null}
     */
    public void createSession (Session session) throws IllegalStateException {
        try {
            this.collection.insertOne(sessionToDocument(session));
        }
        catch (
            MongoWriteException e) {
            if (e.getCode() == 11000) { // Error code 11000 indicates a duplicate key
                throw new IllegalStateException("Duplicate session ID", e);
            } else {
                throw e;
            }
        }
    }

    /**
     * Checks if a session exists in the database.
     *
     * @param id the id of the session
     * @return boolean
     */
    public boolean sessionExists(String id) {
        try {
            getSession(id);
            return true;
        } catch(IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Gets an existing session from the database.
     *
     * @param id the id
     * @return the session
     * @throws IllegalArgumentException if not found exception
     */
    public Session getSession(String id) throws IllegalArgumentException {
       Document session = this.collection.find(eq("_id", id)).first();
       if(session == null) {
           throw new IllegalArgumentException("There is no session with this id that exists in the database");
       }
       return documentToSession(session);
    }

    private static Document sessionToDocument(Session session) {
        Document document = new Document();
        String id = session.getId();
        if (id == null) {
            throw new NullPointerException();
        }
        document.put("_id", id);
        document.put("hospital", session.getHospital());
        document.put("monitorType", session.getMonitorType());
        document.put("projectId", session.getProjectId());
        return document;
    }

    private static Session documentToSession(Document document) {
        Session session = new Session().setId(document.getString("_id"))
                .setHospital(document.getString("hospital"))
                .setMonitorType(document.getString("monitorType"))
                .setProjectId(document.getLong("projectId"));
        return session;
    }


}
