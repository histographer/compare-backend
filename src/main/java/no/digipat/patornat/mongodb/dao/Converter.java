package no.digipat.patornat.mongodb.dao;

import no.digipat.patornat.mongodb.models.image.ImageComparison;
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
     *  }
     * @param json in the form of Image
     * @return Image
     */
    public static ImageChoice jsonToImage(JSONObject json) {
        try {
            long id = (Long) json.get("id");
            String comment = (String) json.getOrDefault("comment", "");
            return new ImageChoice(id, comment);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JSON is not valid, id is missing");
        }
    }


    /**
     * Takes imageComparison object and creates mongodb document
     * @param imageComparison
     * @return
     */
    public static Document imageComparisonToDBDocument(ImageComparison imageComparison) {
        return new Document().
                append("chosen", imageToDBDocument(imageComparison.getChosen()))
                .append("other", imageToDBDocument(imageComparison.getOther()))
                .append("user", imageComparison.getUser());
    }
    
    /**
     * Converts a MongoDB document to an instance of {@code ImageComparison}.
     * 
     * @param document the MongoDB document
     * @return an instance of {@code ImageComparison} representing the given document
     * @throws IllegalArgumentException if {@code document} is not valid. Specifically,
     * if it is {@code null}, its {@code chosen} or {@code other} attribute is not a
     * non-{@code null} document, or if any attribute cannot be cast to the appropriate type.
     */
    public static ImageComparison dbDocumentToImageComparison(Document document) {
        try {
            String user = document.getString("user");
            Document chosenDoc = (Document) document.get("chosen");
            long chosenId = (Long) chosenDoc.get("id");
            String chosenComment = chosenDoc.getString("comment");
            ImageChoice chosen = new ImageChoice(chosenId, chosenComment);
            Document otherDoc = (Document) document.get("other");
            long otherId = (Long) otherDoc.get("id");
            String otherComment = otherDoc.getString("comment");
            ImageChoice other = new ImageChoice(otherId, otherComment);
            ImageComparison imageComparison = new ImageComparison(user, chosen, other);
            return imageComparison;
        } catch (NullPointerException | ClassCastException e) {
            throw new IllegalArgumentException("Invalid document", e);
        }
    }
    
    /**
     * Takes image object and creates mongodb document
     * @param imageChoice
     * @return
     */
    public static Document imageToDBDocument(ImageChoice imageChoice) {
       return new Document()
               .append("id", imageChoice.getId())
               .append("comment", imageChoice.getComment());
    }

    /**
     * Takes in
     * {
     *   "user": "string"
     *   "chosen": {
     *     "id": 1,
     *     "comment": "testcomment",
     *   },
     *   "other": {
     *     "id": 2,
     *     "comment": "testcomment2",
     *   }
     * }
     * @param json
     * @return
     */
    public static ImageComparison jsonToImageComparison(JSONObject json) {
        ImageChoice chosen = Converter.jsonToImage((JSONObject) json.get("chosen"));
        ImageChoice other = Converter.jsonToImage((JSONObject) json.get("other"));
        String user = (String) json.get("user");
        return new ImageComparison(user, chosen, other);
    }
}
