package no.digipat.compare.servlets;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.net.URL;

import no.digipat.compare.models.project.Project;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;

import no.digipat.compare.models.session.Session;
import no.digipat.compare.mongodb.dao.MongoSessionDAO;

public class SessionServletTest {
    
    private static URL baseUrl;
    private static MongoClient client;
    private static String databaseName;
    private MongoSessionDAO sessionDao;
    private MongoProjectDAO projectDao;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        client = IntegrationTests.getMongoClient();
        databaseName = IntegrationTests.getDatabaseName();
    }
    
    @Before
    public void setUp() {
        projectDao = new MongoProjectDAO(client, databaseName);
        Project project = new Project().setId(30l).setName("testname").setActive(true);
        projectDao.createProject(project);
        sessionDao = new MongoSessionDAO(client, databaseName);
    }

    @Test
    public void testCreateSession() throws Exception {
        JSONObject json = new JSONObject();
        final String hospital = "St. Ølavs";
        final Long projectId = 30l;
        final String monitorType = "normαl";
        json.put("hospital", hospital);
        json.put("projectId", projectId);
        json.put("monitorType", monitorType);
        WebConversation conversation = new WebConversation();
        WebRequest request = new PostMethodWebRequest(new URL(baseUrl, "session").toString(),
                new ByteArrayInputStream(json.toString().getBytes("UTF8")), "application/json");
        WebResponse response = conversation.sendRequest(request);
        
        assertEquals(200, response.getResponseCode());
        Session session = sessionDao.getSession(conversation.getCookieValue("JSESSIONID"));
        assertEquals(hospital, session.getHospital());
        assertEquals(monitorType, session.getMonitorType());
        assertEquals(projectId, session.getProjectId());
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
}
