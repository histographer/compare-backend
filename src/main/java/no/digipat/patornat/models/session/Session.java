package no.digipat.patornat.models.session;

public class Session {
    private String id;
    private String hospital;
    private String monitorType;



    public Session(){}

    public String getId() {
        return id;
    }

    public Session setId(String id) {
        this.id = id;
        return this;
    }

    public String getHospital() {
        return hospital;
    }

    public Session setHospital(String hospital) {
        this.hospital = hospital;
        return this;
    }

    public String getMonitorType() {
        return monitorType;
    }

    public Session setMonitorType(String monitorType) {
        this.monitorType = monitorType;
        return this;
    }
}
