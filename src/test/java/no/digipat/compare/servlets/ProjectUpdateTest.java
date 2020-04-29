package no.digipat.compare.servlets;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.net.URL;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.compare.models.project.Project;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;

@RunWith(JUnitParamsRunner.class)
public class ProjectUpdateTest {
    
    private static URL baseUrl;
    private static MongoClient client;
    private static String databaseName;
    private static MongoProjectDAO dao;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        client = IntegrationTests.getMongoClient();
        databaseName = IntegrationTests.getDatabaseName();
        dao = new MongoProjectDAO(client, databaseName);
        dao.createProject(new Project().setId(42L).setName("α prøject").setActive(true));
    }
    
    private static PostMethodWebRequest createPostRequestWithMessageBody(String path,
            String messageBody, String contentType) throws Exception {
        return new PostMethodWebRequest(
                new URL(baseUrl, path).toString(),
                new ByteArrayInputStream(messageBody.getBytes("UTF8")),
                contentType
        );
    }
    
    @Test
    @Parameters({"false", "true"})
    public void testUpdateProjectStatus(boolean active) throws Exception {
        WebConversation conversation = new WebConversation();
        JSONObject requestJson = new JSONObject();
        requestJson.put("projectId", 42);
        requestJson.put("active", active);
        WebRequest request = createPostRequestWithMessageBody("project/update",
                requestJson.toString(), "application/json");
        
        WebResponse response = conversation.sendRequest(request);
        
        assertEquals(200, response.getResponseCode());
        assertEquals(active, dao.getProject(42L).getActive());
        assertEquals("application/json", response.getContentType());
        JSONObject responseJson = new JSONObject(response.getText());
        assertEquals(active, responseJson.getBoolean("active"));
        assertEquals(42, responseJson.getLong("id"));
        assertEquals("α prøject", responseJson.getString("name"));
    }
    
    @Test
    public void testStatusCode404() throws Exception {
        WebConversation conversation = new WebConversation();
        JSONObject json = new JSONObject();
        json.put("projectId", 9999);
        json.put("active", true);
        WebRequest request = createPostRequestWithMessageBody("project/update",
                json.toString(), "application/json");
        
        WebResponse response = conversation.sendRequest(request);
        
        assertEquals(404, response.getResponseCode());
    }
    
    @Test
    @Parameters(method = "getStatusCode400Parameters")
    public void testStatusCode400(String messageBody, String failureMessage) throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = createPostRequestWithMessageBody("project/update",
                messageBody, "application/json");
        
        WebResponse response = conversation.sendRequest(request);
        
        assertEquals(failureMessage, 400, response.getResponseCode());
    }
    
    private String[][] getStatusCode400Parameters() {
        return new String[][] {
            {"This is not JSON", "Testing malformed JSON."},
            {"{\"projectId\": 42}", "Testing without active parameter."},
            {"{\"active\": true}", "Testing without projectId parameter."},
            {"{\"projectId\": \"abc\", \"active\": true}",
                "Testing with non-numeric project ID."
            },
            {"{\"projectId\": 42, \"active\": \"blah\"}",
                "Testing with non-boolean active parameter."
            }
        };
    }
    
    @AfterClass
    public static void tearDownClass() {
        client.getDatabase(databaseName).drop();
    }
    
}
