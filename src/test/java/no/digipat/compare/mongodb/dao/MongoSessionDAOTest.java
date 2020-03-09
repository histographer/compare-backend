package no.digipat.compare.mongodb.dao;

import com.mongodb.MongoClient;

import no.digipat.compare.models.session.Session;
import no.digipat.compare.mongodb.DatabaseUnitTests;
import no.digipat.compare.mongodb.dao.MongoSessionDAO;

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



    @Test(expected=NullPointerException.class)
    public void testCreateNullSession() {
        dao.createSession(null);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateSessionWithNullId() {
        dao.createSession(new Session());
    }



    @Test(expected=IllegalStateException.class)
    public void testCreateSessionWithDuplicateId() {
        String ID = "TESTID";
        String HOSPITAL1 = "TESTHOSPITAL";
        String MONITOR_TYPE1 = "TESTMONITOR";
        String HOSPITAL2 = "TEPITAL";
        String MONITOR_TYPE2 = "TEITOR";

        Session session1 = new Session().setId(ID).setHospital(HOSPITAL1).setMonitorType(MONITOR_TYPE1);
        Session session2 = new Session().setId(ID).setHospital(HOSPITAL2).setMonitorType(MONITOR_TYPE2);
        dao.createSession(session1);
        dao.createSession(session2);
    }
    @Test(expected=IllegalArgumentException.class)
    public void testGetSessionWithNoInserts() {
        Session fetchedSession = dao.getSession("NOID");
    }

    @Test
    public void testGetSession() {
        String ID = "getSession";
        String HOSPITAL1 = "hospitalz";
        String MONITOR_TYPE1 = "hospitalz1";
        Session session1 = new Session().setId(ID).setHospital(HOSPITAL1).setMonitorType(MONITOR_TYPE1);
        dao.createSession(session1);

        Session fetchedSession = dao.getSession(ID);

        assertEquals(session1.getId(), fetchedSession.getId());
        assertEquals(session1.getHospital(), fetchedSession.getHospital());
        assertEquals(session1.getMonitorType(), fetchedSession.getMonitorType());
    }
    @Test
    public void testSessionExists() {
        String ID = "getSession";
        String HOSPITAL1 = "hospitalz";
        String MONITOR_TYPE1 = "hospitalz1";
        Session session1 = new Session().setId(ID).setHospital(HOSPITAL1).setMonitorType(MONITOR_TYPE1);
        dao.createSession(session1);

        boolean exists = dao.sessionExists(session1.getId());
        assertEquals(exists, true);
    }

    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
}
