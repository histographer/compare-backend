package mongodb.models;

import com.mongodb.DBObject;

public interface IUser{
    public DBObject toDBObject();
    public IUser toJavaObject(DBObject object);
    public void setId(String id);
    public String getId();
    public void setUsername(String username);
    public String getUsername();
}
