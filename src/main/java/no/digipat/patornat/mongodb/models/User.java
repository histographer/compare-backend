package no.digipat.patornat.mongodb.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class User implements IUser {
    private String id;
    private String username;


    public User(String id, String username) {
        this.id = id;
        this.username = username;
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
