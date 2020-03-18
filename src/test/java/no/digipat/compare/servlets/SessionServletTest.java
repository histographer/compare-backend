package no.digipat.compare.servlets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import com.meterware.httpunit.protocol.MessageBody;
import com.meterware.httpunit.protocol.ParameterCollection;
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
    public void test() throws Exception {
        JSONObject json = new JSONObject();
        final String hospital = "St. Olavs";
        final Long projectId = 30l;
        final String monitorType = "normal";
        json.put("hospital", hospital);
        json.put("projectId", projectId);
        json.put("monitorType", monitorType);
        WebConversation conversation = new WebConversation();
        WebRequest request = new PostMethodWebRequest(new URL(baseUrl, "session").toString()) {
            @Override
            protected MessageBody getMessageBody() {
                return new MessageBody("utf8") {
                    @Override
                    public void writeTo(OutputStream outputStream, ParameterCollection parameters) throws IOException {
                        PrintWriter writer = new PrintWriter(outputStream);
                        writer.print(json);
                        writer.flush();
                    }
                    @Override
                    public String getContentType() {
                        return "application/json";
                    }
                };
            }
        };
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
