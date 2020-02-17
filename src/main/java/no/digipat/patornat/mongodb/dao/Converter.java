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


    /** Takes in json object and returns image. If the optionals are not provided, empty string or 0
     * {
     *     "id": 1,
     *     "comment": "testcomment",
     *     "kjernestruktur": 1,
     *     "cellegrenser": 1,
     *     "kontrastKollagen": 1,
     *     "kontrastBindevev": 1
     *  }
     * @param json in the form of Image
     * @return Image
     */
    public static Image JsonToImage(JSONObject json) {
        try {
            int id = ((Long) json.get("id")).intValue();
            // Should not throw error if null, int is primitive and cant be null (so we will use 0)
            String comment = (json.get("comment") != null) ? (String) json.get("comment") : null;
            int kjernestruktur = (json.get("kjernestruktur") != null) ? ((Long) json.get("kjernestruktur")).intValue() : 0;
            int cellegrenser = (json.get("cellegrenser") != null) ? ((Long) json.get("cellegrenser")).intValue() : 0;
            int kontrastKollagen= (json.get("kontrastKollagen") != null) ? ((Long) json.get("kontrastKollagen")).intValue() : 0;
            int kontrastBindevev= (json.get("kontrastBindevev") != null) ? ((Long) json.get("kontrastBindevev")).intValue() : 0;
            return new Image(id, comment, kjernestruktur, cellegrenser, kontrastKollagen, kontrastBindevev);
        } catch (NullPointerException e) {
            throw new NullPointerException("JSON is not valid, something is missing");
        }
    }

    public static Document BestImageToDBDocument(IUser user) {
        return new Document("username", user.getUsername());
    }





    /**
     * Takes in
     * {
     *   "chosen": {
     *     "id": 1,
     *     "comment": "testcomment",
     *     "kjernestruktur": 1,
     *     "cellegrenser": 1,
     *     "kontrastKollagen": 1,
     *     "kontrastBindevev": 1
     *   },
     *   "other": {
     *     "id": 2,
     *     "comment": "testcomment2",
     *     "kjernestruktur": 2,
     *     "cellegrenser": 3,
     *     "kontrastKollagen": 2,
     *     "kontrastBindevev": 3
     *   }
     * }
     * @param json
     * @return
     */
    public static BestImage JsonToBestImage(JSONObject json) {
        Image chosen = Converter.JsonToImage((JSONObject) json.get("chosen"));
        Image other = Converter.JsonToImage((JSONObject) json.get("other"));
        return new BestImage(chosen, other);
    }
}
