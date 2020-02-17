package no.digipat.patornat.mongodb.dao;

import no.digipat.patornat.mongodb.models.BestImage;
import no.digipat.patornat.mongodb.models.IUser;
import no.digipat.patornat.mongodb.models.Image;
import no.digipat.patornat.mongodb.models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

/**
 *
 */
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


    /** Takes in json object and returns image
     * {
     *     "id": 1,
     *     "comment": "testcomment",
     *     "kjernestruktur": 1,
     *     "cellegrenser": 1,
     *     "kontrastKollagen": 1,
     *     "kontrastBindevev": 1
     *  }
     * @param Json object in the form of Image
     * @return Image
     */
    public static Image JsonToImage(JSONObject json) {
        try {
            int id = ((Long) json.get("id")).intValue();
            String comment = (String) json.get("comment");
            int kjernestruktur = ((Long) json.get("kjernestruktur")).intValue();
            int cellegrenser = ((Long) json.get("cellegrenser")).intValue();
            int kontrastKollagen = ((Long) json.get("kontrastKollagen")).intValue();
            int kontrastBindevev = ((Long) json.get("kontrastBindevev")).intValue();
            return new Image(id, comment, kjernestruktur, cellegrenser, kontrastKollagen, kontrastBindevev);
        } catch (NullPointerException e) {
            throw new NullPointerException("JSON is not valid, something is missing");
        }
    }

    public static BestImage JsonToBestImage(JSONObject json) {
        Image chosen = Converter.JsonToImage((JSONObject) json.get("chosen"));
        Image other = Converter.JsonToImage((JSONObject) json.get("other"));
        return new BestImage(chosen, other);
    }
}
