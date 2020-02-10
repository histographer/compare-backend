package mongodb.models;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

public class User implements IUser {
    private String id;
    private String username;

    /**
     * Convert from database obj to java obj
     * @return
     */
    @Override
    public DBObject toDBObject() {
        BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
                .append("username", this.getUsername());
            if(this.getId() != null) builder = builder.append("_id", new ObjectId((this.getId())));
            return builder.get();
    }

    /**
     * Convert from databaseobject to java object
     * @param object
     * @return
     */
    @Override
    public IUser toJavaObject(DBObject object) {
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
