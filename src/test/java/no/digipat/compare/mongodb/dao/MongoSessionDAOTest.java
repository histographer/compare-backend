package no.digipat.compare.mongodb.dao;

import com.mongodb.MongoClient;

import no.digipat.compare.models.session.Session;
import no.digipat.compare.mongodb.DatabaseUnitTests;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MongoSessionDAOTest {
    private static MongoClient client;
    private static String databaseName;
    private static MongoSessionDAO dao;

    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
        dao = new MongoSessionDAO(client, databaseName);
    }



    @Test(expected = NullPointerException.class)
    public void testCreateNullSession() {
        dao.createSession(null);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateSessionWithNullId() {
        dao.createSession(new Session());
    }



    @Test(expected = IllegalStateException.class)
    public void testCreateSessionWithDuplicateId() {
        String id = "TESTID";
        String hospital1 = "TESTHOSPITAL";
        String monitorType1 = "TESTMONITOR";
        String hospital2 = "TEPITAL";
        String monitorType2 = "TEITOR";

        Session session1 = new Session().setId(id).setHospital(hospital1)
                .setMonitorType(monitorType1);
        Session session2 = new Session().setId(id).setHospital(hospital2)
                .setMonitorType(monitorType2);
        dao.createSession(session1);
        dao.createSession(session2);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testGetSessionWithNoInserts() {
        dao.getSession("NOID");
    }

    @Test
    public void testGetSession() {
        String id = "getSession";
        String hospital1 = "hospitalz";
        String monitorType1 = "hospitalz1";
        long projectId = 123;
        Session session1 = new Session().setId(id).setHospital(hospital1)
                .setMonitorType(monitorType1).setProjectId(projectId);
        dao.createSession(session1);

        Session fetchedSession = dao.getSession(id);

        assertEquals(session1.getId(), fetchedSession.getId());
        assertEquals(session1.getHospital(), fetchedSession.getHospital());
        assertEquals(session1.getMonitorType(), fetchedSession.getMonitorType());
        assertEquals(session1.getProjectId(), fetchedSession.getProjectId());
    }
    
    @Test
    public void testSessionExists() {
        String id = "getSession";
        String hospital1 = "hospitalz";
        String monitorType1 = "hospitalz1";
        Session session1 = new Session().setId(id).setHospital(hospital1)
                .setMonitorType(monitorType1);
        dao.createSession(session1);

        boolean exists = dao.sessionExists(session1.getId());
        assertEquals(exists, true);
    }

    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
}
