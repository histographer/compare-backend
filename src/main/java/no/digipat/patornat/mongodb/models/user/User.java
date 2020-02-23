package no.digipat.patornat.mongodb.models.user;

import java.util.UUID;

public class User {
    private String id;
    private String hospital;
    private String monitorType;



    public User(String monitorType, String hospital) {
        this.id = UUID.randomUUID().toString();
        this.monitorType = monitorType;
        this.hospital = hospital;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(String monitorType) {
        this.monitorType = monitorType;
    }
}
