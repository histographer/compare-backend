package no.digipat.compare.servlets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.protocol.MessageBody;
import com.meterware.httpunit.protocol.ParameterCollection;
import com.mongodb.MongoClient;

import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.image.ImageChoice;
import no.digipat.compare.models.image.ImageComparison;
import no.digipat.compare.models.project.Project;
import no.digipat.compare.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.compare.mongodb.dao.MongoImageDAO;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;

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
        projectDao.createProject(new Project().setId(20l).setName("testname"));
    }
    
    private static void login(WebConversation conversation) throws Exception {

        WebRequest loginRequest = new PostMethodWebRequest(new URL(baseUrl, "session").toString()) {
            @Override
            protected MessageBody getMessageBody() {
                return new MessageBody("UTF8") {
                    @Override
                    public void writeTo(OutputStream outputStream, ParameterCollection parameters) throws IOException {
                        PrintWriter writer = new PrintWriter(outputStream);
                        writer.print("{\"monitorType\": \"normal\", \"hospital\": \"St. Olavs\", \"projectId\": 20}");
                        writer.flush();
                    }
                    @Override
                    public String getContentType() {
                        return "application/json";
                    }
                };
            }
        };
        conversation.sendRequest(loginRequest);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testWithValidServerState() throws Exception {
        Image image1 = new Image().setImageId(42L).setProjectId(20L);
        Image image2 = new Image().setImageId(1337L).setProjectId(20L);
        Image image3 = new Image().setImageId(69L).setProjectId(30L);
        Image image4 = new Image().setImageId(56L).setProjectId(30L);
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
        
        assertEquals(200, response.getResponseCode());
        JSONArray json = new JSONArray(response.getText());
        List<Object> list = json.toList();
        assertEquals(2, list.size());
        Collections.sort(list, comparingInt(map -> (int) ((Map) map).get("id")));
        Map<String, Object> map1 = (Map<String, Object>) list.get(0);
        assertEquals(image1.getImageId(), map1.get("id"));
        assertNotNull((Float) map1.get("score")); // Check that score is non-null and has correct type
        assertEquals(4, map1.get("rankings"));
        Map<String, Object> map2 = (Map<String, Object>) list.get(2);
        assertEquals(image2.getImageId(), map2.get("id"));
        assertNotNull((Float) map2.get("score")); // Check that score is non-null and has correct type
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
        
        assertEquals(400, response.getResponseCode());
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
        
        assertEquals(400, response.getResponseCode());
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }

}
