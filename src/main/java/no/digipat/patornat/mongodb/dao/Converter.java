package no.digipat.patornat.mongodb.dao;

import no.digipat.patornat.mongodb.models.image.BestImageChoice;
import no.digipat.patornat.mongodb.models.user.IUser;
import no.digipat.patornat.mongodb.models.image.ImageChoice;
import no.digipat.patornat.mongodb.models.user.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

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
    public static ImageChoice jsonToImage(JSONObject json) {
        try {
            int id = ((Long) json.get("id")).intValue();
            // Should not throw error if null, int is primitive and cant be null (so we will use 0)
            String comment = (json.get("comment") != null) ? (String) json.get("comment") : null;
            int kjernestruktur = (json.get("kjernestruktur") != null) ? ((Long) json.get("kjernestruktur")).intValue() : 0;
            int cellegrenser = (json.get("cellegrenser") != null) ? ((Long) json.get("cellegrenser")).intValue() : 0;
            int kontrastKollagen= (json.get("kontrastKollagen") != null) ? ((Long) json.get("kontrastKollagen")).intValue() : 0;
            int kontrastBindevev= (json.get("kontrastBindevev") != null) ? ((Long) json.get("kontrastBindevev")).intValue() : 0;
            return new ImageChoice(id, comment, kjernestruktur, cellegrenser, kontrastKollagen, kontrastBindevev);
        } catch (NullPointerException e) {
            throw new NullPointerException("JSON is not valid, something is missing");
        }
    }


    /**
     * Takes bestImage object and creates mongodb document
     * @param bestImageChoice
     * @return
     */
    public static Document bestImageToDBDocument(BestImageChoice bestImageChoice) {
        return new Document().
                append("chosen", imageToDBDocument(bestImageChoice.getChosen()))
                .append("other", imageToDBDocument(bestImageChoice.getOther()));
    }


    /**
     * Takes in an integer and returns null if the int is null. This is used for easier DB analysis
     * @param integer
     * @return
     */
    public static Integer convertToNullIfZero(int integer) {
        return (integer == 0) ? null : integer;
    }

    /**
     * Takes image object and creates mongodb document
     * @param imageChoice
     * @return
     */
    public static Document imageToDBDocument(ImageChoice imageChoice) {
       return new Document()
               .append("id", imageChoice.getId())
               .append("comment", imageChoice.getComment())
               .append("kjernestruktur", convertToNullIfZero(imageChoice.getKjernestruktur()))
               .append("cellegrenser", convertToNullIfZero(imageChoice.getCellegrenser()))
               .append("kontrastKollagen", convertToNullIfZero(imageChoice.getKontrastKollagen()))
               .append("kontrastBindevev", convertToNullIfZero(imageChoice.getKontrastBindevev()));
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
    public static BestImageChoice jsonToBestImageChoice(JSONObject json) {
        ImageChoice chosen = Converter.jsonToImage((JSONObject) json.get("chosen"));
        ImageChoice other = Converter.jsonToImage((JSONObject) json.get("other"));
        return new BestImageChoice(chosen, other);
    }
}
