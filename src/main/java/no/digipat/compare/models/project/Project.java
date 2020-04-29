package no.digipat.compare.models.project;

/**
 * A representation of a Cytomine project whose images can be
 * compared by users.
 * 
 * @author Kent Are Torvik
 *
 */
public class Project {
    private Long id;
    private String name;
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public Project setActive(Boolean active) {
        this.active = active;
        return this;
    }


    public Long getId() {
        return id;
    }

    public Project setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Project setName(String name) {
        this.name = name;
        return this;
    }
}
