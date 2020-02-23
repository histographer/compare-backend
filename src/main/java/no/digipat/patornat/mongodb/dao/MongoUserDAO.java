package no.digipat.patornat.mongodb.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import no.digipat.patornat.mongodb.models.user.IUser;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class MongoUserDAO {
    private MongoCollection<Document> collection;
    private String DB = "users";


    public MongoUserDAO(MongoClient mongo) {
        this.collection = mongo.getDatabase(DB).getCollection("user");
    }

    public IUser createUser(IUser user) {
        Document document = Converter.userToDBDocument(user);
        this.collection.insertOne(document);
        ObjectId id = (ObjectId) document.get("_id");
        user.setId(id.toString());
        return user;
    }

    public IUser readUser(IUser user) {

        Document returnedUser = (Document) this.collection.find(eq("username", user.getUsername())).first();

        return Converter.userDocumentToJavaObject(returnedUser);
    }
}
