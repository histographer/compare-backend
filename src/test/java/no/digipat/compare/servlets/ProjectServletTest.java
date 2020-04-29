package no.digipat.compare.servlets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Comparator.comparingLong;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;

import no.digipat.compare.models.project.Project;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;

public class ProjectServletTest {
    
    // TODO test POST (if possible)
    
    private static URL baseUrl;
    private static MongoClient client;
    private static String databaseName;
    private MongoProjectDAO dao;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        client = IntegrationTests.getMongoClient();
        databaseName = IntegrationTests.getDatabaseName();
    }
    
    @Before
    public void setUp() {
        dao = new MongoProjectDAO(client, databaseName);
        dao.createProject(new Project().setId(42L).setName("α prøject").setActive(true));
        dao.createProject(new Project().setId(1337L).setName("leet project").setActive(true));
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
    @Test
    public void testGetOneProject() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(baseUrl, "project?projectId=42");
        WebResponse response = conversation.sendRequest(request);
        
        assertEquals(200, response.getResponseCode());
        JSONObject json = new JSONObject(response.getText());
        assertEquals(42, json.get("id"));
        assertEquals("α prøject", json.get("name"));
    }
    
    @Test
    public void testGetAllProjects() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(baseUrl, "project");
        WebResponse response = conversation.sendRequest(request);
        
        assertEquals(200, response.getResponseCode());
        JSONArray json = new JSONArray(response.getText());
        Project[] projectArray = json.toList().stream().<Project>map(object -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapObject = (Map<String, Object>) object;
            return new Project()
                    .setId((long) (int) mapObject.get("id"))
                    .setName((String) mapObject.get("name"));
        }).toArray(Project[]::new);
        List<Project> projectList = Arrays.asList(projectArray);
        Collections.sort(projectList, comparingLong(project -> project.getId()));
        assertEquals((Long) 42L, projectList.get(0).getId());
        assertEquals("α prøject", projectList.get(0).getName());
        assertEquals((Long) 1337L, projectList.get(1).getId());
        assertEquals("leet project", projectList.get(1).getName());
    }
    
    @Test
    public void test404() throws Exception {
        WebConversation conversation = new WebConversation();
        conversation.setExceptionsThrownOnErrorStatus(false);
        WebRequest request = new GetMethodWebRequest(baseUrl, "project?projectId=9999");
        WebResponse response = conversation.sendRequest(request);
        
        assertEquals(404, response.getResponseCode());
    }
    
    @Test
    public void test400() throws Exception {
        // Use HttpURLConnection to work around the fact that HttpUnit throws an exception
        // on status code 400 even if setExceptionsThrownOnErrorStatus(false) has been called
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl,
                "project?projectId=notANumber").openConnection();
        try {
            connection.connect();
        } catch (IOException e) {
            
        }
        
        assertEquals(400, connection.getResponseCode());
    }
    
    @Test
    public void testUnicodeCharactersOnRetrieval() throws Exception {
        String projectName = "ÆØÅæøåαβγ";
        dao.createProject(new Project().setActive(true).setId(20L).setName(projectName));
        
        WebRequest request = new GetMethodWebRequest(baseUrl, "/project?projectId=" + 20);
        WebResponse response = new WebConversation().getResponse(request);
        
        assertEquals(200, response.getResponseCode());
        JSONObject json = new JSONObject(response.getText());
        assertEquals(projectName, json.getString("name"));
    }
    
}
