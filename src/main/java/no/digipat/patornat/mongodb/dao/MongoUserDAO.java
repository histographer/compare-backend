package no.digipat.patornat.mongodb.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import no.digipat.patornat.mongodb.models.user.User;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class MongoUserDAO {
    private MongoCollection<Document> collection;


    public MongoUserDAO(MongoClient mongo, String database) {
        this.collection = mongo.getDatabase(database).getCollection("user");
    }

    public void createUser(User user) {
        Document document = Converter.userToDBDocument(user);
        this.collection.insertOne(document);
    }

}
