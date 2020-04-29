package no.digipat.compare.mongodb.dao;
import com.mongodb.MongoClient;

import no.digipat.compare.models.project.Project;
import no.digipat.compare.mongodb.DatabaseUnitTests;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

public class MongoProjectDAOTest {
    private static MongoClient client;
    private static String databaseName;
    private static MongoProjectDAO dao;
    private static Project sampleProject1;
    private static Project sampleProject2;


    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
        dao = new MongoProjectDAO(client, databaseName);
        sampleProject1 = new Project().setId(42L).setName("Forty two").setActive(false);
        sampleProject2 = new Project().setId(1337L).setName("Leet").setActive(false);
    }


    @Test(expected = NullPointerException.class)
    public void testCreateNullProject() {
        dao.createProject(null);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateProjectWithNullId() {
        dao.createProject(new Project());
    }


    @Test(expected = IllegalStateException.class)
    public void testCreateProjectWithDuplicateId() {
        Long id = 69L;
        String name1 = "TEST NAME";
        String name2 = "TEST NAME 2";

        Project project1 = new Project().setId(id).setName(name1).setActive(false);
        Project project2 = new Project().setId(id).setName(name2).setActive(false);
        dao.createProject(project1);
        dao.createProject(project2);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetProjectWithNoInserts() {
        dao.getProject(999L);
    }


    @Test
    public void testGetProject() {
        dao.createProject(sampleProject1);

        Project fetchedProject = dao.getProject(sampleProject1.getId());

        assertEquals(sampleProject1.getId(), fetchedProject.getId());
        assertEquals(sampleProject1.getName(), fetchedProject.getName());
    }


    @Test
    public void testGetAllProjects() {
        dao.createProject(sampleProject1);
        dao.createProject(sampleProject2);

        List<Project> fetchedProjects = dao.getAllProjects();

        assertEquals(fetchedProjects.size(), 2);

        Collections.sort(fetchedProjects, new Comparator<Project>() {
            @Override
            public int compare(Project arg0, Project arg1) {
                return (int) (arg0.getId() - arg1.getId());
            }
        });

        Project project1 = fetchedProjects.get(0);
        Project project2 = fetchedProjects.get(1);
        assertEquals(sampleProject1.getId(), project1.getId());
        assertEquals(sampleProject1.getName(), project1.getName());
        assertEquals(sampleProject2.getId(), project2.getId());
        assertEquals(sampleProject2.getName(), project2.getName());
    }
    
    @Test
    public void testUpdateProjectActive() {
        dao.createProject(sampleProject1);
        dao.updateProjectActive(sampleProject1.getId(), true);
        
        assertTrue(dao.getProject(sampleProject1.getId()).getActive());
        
        dao.updateProjectActive(sampleProject1.getId(), false);
        
        assertFalse(dao.getProject(sampleProject1.getId()).getActive());
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
}
