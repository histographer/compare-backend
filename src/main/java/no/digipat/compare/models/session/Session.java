package no.digipat.compare.models.session;

/**
 * A representation of a user session.
 * 
 * @author Kent Are Torvik
 *
 */
public class Session {
    private String id;
    private Long projectId;
    private String hospital;
    private String monitorType;

    public String getId() {
        return id;
    }

    public Session setId(String id) {
        this.id = id;
        return this;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public Session setProjectId(Long projectId) {
        this.projectId = projectId;
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
