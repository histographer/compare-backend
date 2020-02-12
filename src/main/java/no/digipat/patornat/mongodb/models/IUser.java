package no.digipat.patornat.mongodb.models;

import org.bson.Document;

public interface IUser{
    public void setId(String id);
    public String getId();
    public void setUsername(String username);
    public String getUsername();
}
