package mongodb.DAO;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import mongodb.models.IUser;
import org.bson.types.ObjectId;

public class MongoUserDAO {
    private DBCollection collection;
    private String DB = "patornat";


    public MongoUserDAO(MongoClient mongo) {
        this.collection = (DBCollection) mongo.getDatabase(DB).getCollection("user");
    }

    public IUser createUser(IUser user) {
        DBObject document = user.toDBObject();
        this.collection.insert(document);
        ObjectId id = (ObjectId) document.get("_id");
        user.setId(id.toString());
        return user;
    }

    public IUser readUser(IUser user) {
        DBObject query = BasicDBObjectBuilder.start()
                .append("_id", new ObjectId(user.getId())).get();
        DBObject returnedUser = this.collection.findOne(query);
        return user.toJavaObject(returnedUser);
    }
}
