package no.digipat.compare.servlets;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Collections;
import static java.util.Comparator.comparingInt;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.image.ImageChoice;
import no.digipat.compare.models.image.ImageComparison;
import no.digipat.compare.models.project.Project;
import no.digipat.compare.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.compare.mongodb.dao.MongoImageDAO;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;

@RunWith(JUnitParamsRunner.class)
public class RankingTest {
    
    private static URL baseUrl;
    private static MongoClient client;
    private static String databaseName;
    private MongoImageDAO imageDao;
    private MongoImageComparisonDAO comparisonDao;
    private MongoProjectDAO projectDao;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        client = IntegrationTests.getMongoClient();
        databaseName = IntegrationTests.getDatabaseName();
    }
    
    @Before
    public void setUp() {
        imageDao = new MongoImageDAO(client, databaseName);
        comparisonDao = new MongoImageComparisonDAO(client, databaseName);
        projectDao = new MongoProjectDAO(client, databaseName);
        projectDao.createProject(new Project().setId(20L).setName("testname").setActive(true));
    }
    
    private static void login(WebConversation conversation) throws Exception {
        WebRequest loginRequest = new PostMethodWebRequest(
                new URL(baseUrl, "session").toString(),
                new ByteArrayInputStream(
                        ("{\"monitorType\": \"normal\", \"hospital\": "
                        + "\"St. Olavs\", \"projectId\": 20}").getBytes("UTF8")
                ),
                "application/json");
        conversation.sendRequest(loginRequest);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testWithValidServerState() throws Exception {
        Image image1 = new Image().setImageId(42L).setProjectId(20L).setFileName("imαge1.png");
        Image image2 = new Image().setImageId(1337L).setProjectId(20L).setFileName("imæge2.jpg");
        Image image3 = new Image().setImageId(69L).setProjectId(30L).setFileName("image3.jpeg");
        Image image4 = new Image().setImageId(56L).setProjectId(30L).setFileName("image4.gif");
        imageDao.createImage(image1);
        imageDao.createImage(image2);
        imageDao.createImage(image3);
        ImageComparison comparison = new ImageComparison().setProjectId(20L)
                .setWinner(new ImageChoice(image1.getImageId(), ""))
                .setLoser(new ImageChoice(image2.getImageId(), ""));
        // Insert the same comparison several times:
        comparisonDao.createImageComparison(comparison);
        comparisonDao.createImageComparison(comparison);
        comparisonDao.createImageComparison(comparison);
        // Insert a modified comparison:
        ImageChoice tempWinner = comparison.getWinner();
        ImageChoice tempLoser = comparison.getLoser();
        comparison.setWinner(tempLoser).setLoser(tempWinner);
        comparisonDao.createImageComparison(comparison);
        // Insert a comparison for another project:
        comparisonDao.createImageComparison(new ImageComparison()
                .setWinner(new ImageChoice(image3.getImageId(), ""))
                .setLoser(new ImageChoice(image4.getImageId(), "")));
        
        WebConversation conversation = new WebConversation();
        login(conversation);
        WebRequest request = new GetMethodWebRequest(baseUrl, "ranking?projectId=20");
        WebResponse response = conversation.sendRequest(request);
        
        assertEquals(response.getText() + "\n", 200, response.getResponseCode());
        JSONArray json = new JSONArray(response.getText());
        List<Object> list = json.toList();
        assertEquals(2, list.size());
        Collections.sort(list, comparingInt(map -> (int) ((Map) map).get("id")));
        Map<String, Object> map1 = (Map<String, Object>) list.get(0);
        assertEquals((int) (long) image1.getImageId(), map1.get("id"));
        assertEquals(image1.getFileName(), map1.get("fileName"));
        // Check that score is non-null and has correct type:
        assertNotNull((Double) map1.get("score"));
        assertEquals(4, map1.get("rankings"));
        Map<String, Object> map2 = (Map<String, Object>) list.get(1);
        assertEquals((int) (long) image2.getImageId(), map2.get("id"));
        assertEquals(image2.getFileName(), map2.get("fileName"));
        // Check that score is non-null and has correct type:
        assertNotNull((Double) map2.get("score"));
        assertEquals(4, map2.get("rankings"));
    }
    
    @Test
    public void testZeroImagesInProject() throws Exception {
        imageDao.createImage(new Image().setImageId(1L).setProjectId(30L));
        imageDao.createImage(new Image().setImageId(2L).setProjectId(30L));
        WebConversation conversation = new WebConversation();
        
        login(conversation);
        conversation.setExceptionsThrownOnErrorStatus(false);
        WebRequest request = new GetMethodWebRequest(baseUrl, "ranking?projectId=20");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(500, response.getResponseCode());
    }
    
    @Test
    public void testOneImageInProject() throws Exception {
        imageDao.createImage(new Image().setImageId(1L).setProjectId(30L));
        imageDao.createImage(new Image().setImageId(2L).setProjectId(30L));
        imageDao.createImage(new Image().setImageId(10L).setProjectId(20L));
        
        WebConversation conversation = new WebConversation();
        login(conversation);
        conversation.setExceptionsThrownOnErrorStatus(false);
        WebRequest request = new GetMethodWebRequest(baseUrl, "ranking?projectId=20");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(500, response.getResponseCode());
    }
    
    @Test
    public void testStatusCode404() throws Exception {
        WebConversation conversation = new WebConversation();
        login(conversation);
        WebRequest request = new GetMethodWebRequest(baseUrl, "ranking?projectId=9999");
        WebResponse response = conversation.sendRequest(request);
                
        assertEquals(404, response.getResponseCode());
    }
    
    @Test
    @Parameters({
        "ranking",
        "ranking?projectId=notANumber"
    })
    public void testStatusCode400(String path) throws Exception {
        WebConversation conversation = new WebConversation();
        login(conversation);
        WebRequest request = new GetMethodWebRequest(baseUrl, path);
        WebResponse response = conversation.sendRequest(request);
        
        assertEquals(400, response.getResponseCode());
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }

}
