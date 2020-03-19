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
    private static Project SAMPLE_PROJECT1;
    private static Project SAMPLE_PROJECT2;


    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
        dao = new MongoProjectDAO(client, databaseName);
        SAMPLE_PROJECT1 = new Project().setId(42l).setName("Forty two").setActive(false);
        SAMPLE_PROJECT2 = new Project().setId(1337l).setName("Leet").setActive(false);
    }


    @Test(expected=NullPointerException.class)
    public void testCreateNullProject() {
        dao.createProject(null);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateProjectWithNullId() {
        dao.createProject(new Project());
    }


    @Test(expected=IllegalStateException.class)
    public void testCreateProjectWithDuplicateId() {
        Long ID = 69l;
        String NAME = "TEST NAME";
        String NAME2 = "TEST NAME 2";

        Project project1 = new Project().setId(ID).setName(NAME).setActive(false);
        Project project2 = new Project().setId(ID).setName(NAME2).setActive(false);
        dao.createProject(project1);
        dao.createProject(project2);
    }


    @Test(expected=IllegalArgumentException.class)
    public void testGetProjectWithNoInserts() {
        dao.getProject(999l);
    }


    @Test
    public void testGetProject () {
        dao.createProject(SAMPLE_PROJECT1);

        Project fetchedProject = dao.getProject(SAMPLE_PROJECT1.getId());

        assertEquals(SAMPLE_PROJECT1.getId(), fetchedProject.getId());
        assertEquals(SAMPLE_PROJECT1.getName(), fetchedProject.getName());
    }


    @Test
    public void testGetAllProjects () {
        dao.createProject(SAMPLE_PROJECT1);
        dao.createProject(SAMPLE_PROJECT2);

        List<Project> fetchedProjects = dao.getAllProjects();

        assertEquals(fetchedProjects.size(), 2);

        Collections.sort(fetchedProjects, new Comparator<Project>() {
            @Override
            public int compare(Project arg0, Project arg1) {
                return (int) (arg0.getId() - arg1.getId());
            }
        });

        Project PROJECT1 = fetchedProjects.get(0);
        Project PROJECT2 = fetchedProjects.get(1);
        assertEquals(SAMPLE_PROJECT1.getId(), PROJECT1.getId());
        assertEquals(SAMPLE_PROJECT1.getName(), PROJECT1.getName());
        assertEquals(SAMPLE_PROJECT2.getId(), PROJECT2.getId());
        assertEquals(SAMPLE_PROJECT2.getName(), PROJECT2.getName());
    }
    
    @Test
    public void testUpdateProjectActive() throws Exception {
        dao.createProject(SAMPLE_PROJECT1);
        dao.updateProjectActive(SAMPLE_PROJECT1.getId(), true);
        
        assertTrue(dao.getProject(SAMPLE_PROJECT1.getId()).getActive());
        
        dao.updateProjectActive(SAMPLE_PROJECT1.getId(), false);
        
        assertFalse(dao.getProject(SAMPLE_PROJECT1.getId()).getActive());
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
}
