package no.digipat.compare.models.project;

public class Project {
    private Long id;
    private String name;

    public Boolean getActive() {
        return active;
    }

    public Project setActive(Boolean active) {
        this.active = active;
        return this;
    }

    private Boolean active;

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
