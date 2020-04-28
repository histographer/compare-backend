package no.digipat.compare.mongodb.dao;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import no.digipat.compare.models.project.Project;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

/**
 * A data access object (DAO) for projects.
 *
 * @author Kent Are Torvik
 *
 */
public class MongoProjectDAO {
    private MongoCollection<Document> collection;


    /**
     * Creates a DAO.
     *
     * @param client the client used to connect to the database
     * @param database the name of the database
     */
    public MongoProjectDAO(MongoClient client, String database) {
        this.collection = client.getDatabase(database).getCollection("projects");
    }

    /**
     * inserts a new project to the database.
     *
     * @param project the project that has to be inserted
     *
     * @throws IllegalStateException if a project with the given ID already exists
     * @throws NullPointerException if {@code project} or {@code project.getId()} is {@code null}
     */
    public void createProject(Project project) {
        try {
            this.collection.insertOne(projectToDocument(project));
        } catch (MongoWriteException e) {
            if (e.getCode() == 11000) { // Error code 11000 indicates a duplicate key
                throw new IllegalStateException("Duplicate ID", e);
            } else {
                throw e;
            }
        }
    }

    public Project getProject(long id) {
        Document project = this.collection.find(eq("_id", id)).first();
        if (project == null) {
            throw new IllegalArgumentException("There is no project with this id that exists in the database");
        }
        return documentToProject(project);
    }

    public Project updateProjectActive(long id, boolean active) {
        Bson filter = eq("_id", id);
        Bson updateOperation = set("active", active);
        UpdateResult updateResult = this.collection.updateOne(filter, updateOperation);
        Document projectFromDb = this.collection.find(filter).first();

        if (projectFromDb == null) {
            throw new IllegalArgumentException("There is no project with this id that exists in the database");
        }
        Project project = documentToProject(projectFromDb);
        return project;
    }


    public List<Project> getAllProjects() {
        final List<Project> projects = new ArrayList<>();
        for (Document document : this.collection.find()) {
           projects.add(documentToProject(document));
        }
        return projects;
    }

    public boolean projectExists(long id) {
        try {
            getProject(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }

    }

    private Document projectToDocument(Project project) {
        Document document = new Document();
        Long id = project.getId();
        String name = project.getName();
        Boolean active = project.getActive();
        if (id == null || name == null || active == null) {
            throw new NullPointerException("One or more value is null. id="
                    + id + ". name=" + name + ". active=" + active);
        }
        document.put("_id", id);
        document.put("name", name);
        document.put("active", active);
        return document;
    }

    private Project documentToProject(Document document) {
        Project project = new Project().setId(document.getLong("_id"))
                .setName(document.getString("name")).setActive(document.getBoolean("active"));
        return project;
    }

}
