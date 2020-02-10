package mongodb.models;

import com.mongodb.DBObject;

public class User implements IUser {
    private String id;
    private String username;

    /**
     * Convert from database obj to java obj
     * @param user
     * @return
     */
    @Override
    public DBObject toDBObject(IUser user) {
        return null;
    }

    /**
     * Convert from databaseobject to java object
     * @param object
     * @return
     */
    @Override
    public IUser toJavaObject(DBObject object) {
        return null;
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
