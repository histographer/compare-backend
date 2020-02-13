package no.digipat.patornat.mongodb.dao;

import no.digipat.patornat.mongodb.models.IUser;
import no.digipat.patornat.mongodb.models.User;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Converter {
    /**
     *  Creates a database document that can be inserted directly to the db
     * @return
     */
    public static Document userToDBDocument(IUser user) {
        return new Document("username", user.getUsername());
    }

    /**
     * Convert from database document to java object
     * @param object
     * @return
     */
    public static IUser userDocumentToJavaObject(Document object) {
        String username = (String) object.get("username");
        ObjectId id = (ObjectId) object.get("_id");
        User user = new User(id.toString(), username);
        return user;
    }
}
