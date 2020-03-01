package no.digipat.patornat.servlets;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;

import no.digipat.patornat.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.patornat.mongodb.dao.MongoImageDAO;
import no.digipat.patornat.mongodb.models.image.ImageComparison;
import no.digipat.patornat.mongodb.models.image.Image;
import no.digipat.patornat.mongodb.models.image.ImageChoice;

public class NextImagePairTest {
    
    private static URL baseUrl;
    private static MongoClient client;
    private static String databaseName;
    private MongoImageDAO imageDao;
    private MongoImageComparisonDAO comparisonDao;
    
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
    }
    
    @Test
    public void testNotEnoughImages() throws Exception {
        WebConversation conversation = new WebConversation();
        conversation.setExceptionsThrownOnErrorStatus(false);
        WebRequest request = new GetMethodWebRequest(baseUrl, "imagePair");
        // Zero images in database
        WebResponse response1 = conversation.getResponse(request);
        assertEquals(500, response1.getResponseCode());
        // One image in database
        imageDao.createImage(new Image().setId(1L));
        WebResponse response2 = conversation.getResponse(request);
        assertEquals(500, response2.getResponseCode());
    }
    
    @Test
    public void testWithValidServerState() throws Exception {
        Image image1 = new Image().setId(42L).setWidth(150L).setHeight(200L)
                .setDepth(10L).setMagnification(4L).setResolution(50.67)
                .setMimeType("image/png")
                .setImageServerURLs(new String[] {"https://example.com"});
        imageDao.createImage(image1);
        Image image2 = new Image().setId(1337L).setWidth(100L).setHeight(50L)
                .setDepth(5L).setMagnification(3L).setResolution(123.45)
                .setMimeType("image/jpeg")
                .setImageServerURLs(new String[] {"http://digipat.no"});
        imageDao.createImage(image2);
        comparisonDao.createImageComparison(new ImageComparison("some_user", new ImageChoice(1L, "good"), new ImageChoice(2L, "really bad")));
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(baseUrl, "imagePair");
        WebResponse response = conversation.getResponse(request);
        // Test status code
        assertEquals(200, response.getResponseCode());
        // Test content type
        assertEquals("application/json", response.getContentType());
        // Test response body
        String body = response.getText();
        JSONParser parser = new JSONParser();
        JSONArray json = (JSONArray) parser.parse(body);
        Image[] receivedImages = json.toList().stream().map(new Function<Object, Image>() {
            @Override
            public Image apply(Object object) {
                Map<String, Object> map = (Map<String, Object>) object;
                return new Image().setId((Long) map.get("id"))
                        .setWidth((Long) map.get("width"))
                        .setHeight((Long) map.get("height"))
                        .setDepth((Long) map.get("depth"))
                        .setMagnification((Long) map.get("magnification"))
                        .setMimeType((String) map.get("mime"))
                        .setImageServerURLs((String[]) map.get("imageServerURLs"));
            }
        }).toArray(Image[]::new);
        Arrays.sort(receivedImages, new Comparator<Image>() {
            @Override
            public int compare(Image img1, Image img2) {
                return (int) (img1.getId() - img2.getId());
            }
        });
        assertArrayEquals(new Image[] {image1, image2}, receivedImages);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
