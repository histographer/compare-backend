package no.digipat.compare.servlets;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Comparator.comparingLong;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.AfterClass;
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
    private static MongoProjectDAO dao;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        client = IntegrationTests.getMongoClient();
        databaseName = IntegrationTests.getDatabaseName();
        dao = new MongoProjectDAO(client, databaseName);
        dao.createProject(new Project().setId(42L).setName("a project"));
        dao.createProject(new Project().setId(1337L).setName("leet project"));
    }
    
    @Test
    public void testGetOneProject() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(baseUrl, "project?projectId=42");
        WebResponse response = conversation.sendRequest(request);
        
        assertEquals(200, response.getResponseCode());
        JSONObject json = new JSONObject(response.getText());
        assertEquals(42, json.get("id"));
        assertEquals("a project", json.get("name"));
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
            return new Project().setId((long) (int) mapObject.get("id")).setName((String) mapObject.get("name"));
        }).toArray(Project[]::new);
        List<Project> projectList = Arrays.asList(projectArray);
        Collections.sort(projectList, comparingLong(project -> project.getId()));
        assertEquals((Long) 42L, projectList.get(0).getId());
        assertEquals("a project", projectList.get(0).getName());
        assertEquals((Long) 1337L, projectList.get(1).getId());
        assertEquals("leet project", projectList.get(1).getName());
    }
    
    @AfterClass
    public static void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
