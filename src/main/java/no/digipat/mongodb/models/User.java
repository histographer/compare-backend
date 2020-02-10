package no.digipat.mongodb.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class User implements IUser {
    private String id;
    private String username;

    /**
     *  Creates a database document that can be inserted directly to the db
     * @return
     */
    @Override
    public Document toDBDocument() {
        return new Document("username", this.username);
    }

    /**
     * Convert from database document to java object
     * @param object
     * @return
     */
    @Override
    public IUser toJavaObject(Document object) {
        this.setUsername((String) object.get("username"));
        ObjectId id = (ObjectId) object.get("_id");
        this.setId(id.toString());
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
