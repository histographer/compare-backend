package mongodb.models;

import com.mongodb.DBObject;

public interface IUser{
    public DBObject toDBObject(IUser user);
    public IUser toJavaObject(DBObject object);
}
