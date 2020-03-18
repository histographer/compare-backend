package no.digipat.compare.servlets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import no.digipat.compare.models.project.Project;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;
import org.json.JSONArray;
import org.junit.AfterClass;
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
import no.digipat.compare.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.compare.mongodb.dao.MongoImageDAO;

public class NextImagePairTest {
    
    private static URL baseUrl;
    private static MongoClient client;
    private static String databaseName;
    private static MongoImageDAO imageDao;
    private static MongoImageComparisonDAO comparisonDao;
    private static MongoProjectDAO projectDao;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        client = IntegrationTests.getMongoClient();
        databaseName = IntegrationTests.getDatabaseName();
        imageDao = new MongoImageDAO(client, databaseName);
        comparisonDao = new MongoImageComparisonDAO(client, databaseName);
        projectDao = new MongoProjectDAO(client, databaseName);
        Project project = new Project().setId(20l).setName("testname").setActive(true);
        projectDao.createProject(project);
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
    public void testNotEnoughImages() throws Exception {
        WebConversation conversation = new WebConversation();
        login(conversation);
        conversation.setExceptionsThrownOnErrorStatus(false);
        WebRequest request = new GetMethodWebRequest(baseUrl, "imagePair?projectId=20");
        // Zero images in database
        WebResponse response1 = conversation.getResponse(request);
        assertEquals(500, response1.getResponseCode());
        // One image in database
        imageDao.createImage(new Image().setImageId(30L).setProjectId(20L));
        WebResponse response2 = conversation.getResponse(request);
        assertEquals(500, response2.getResponseCode());
    }
    
    @Test
    public void testWithValidServerState() throws Exception {
        Image image1 = new Image().setImageId(42L).setWidth(150L).setHeight(200L)
                .setDepth(10L).setMagnification(4L).setResolution(50.67)
                .setMimeType("image/png").setProjectId(20l)
                .setImageServerURLs(new String[] {"https://example.com"});

        imageDao.createImage(image1);
        Image image2 = new Image().setImageId(1337L).setWidth(100L).setHeight(50L)
                .setDepth(5L).setMagnification(3L).setResolution(123.45)
                .setMimeType("image/jpeg").setProjectId(20l)
                .setImageServerURLs(new String[] {"https://example.com"});
        imageDao.createImage(image2);
        comparisonDao.createImageComparison(new ImageComparison().setProjectId(20L).setSessionID("some_user")
                .setWinner(new ImageChoice(1337L, "good")).setLoser(new ImageChoice(42L, "really bad")));
        WebConversation conversation = new WebConversation();
        login(conversation);
        WebRequest request = new GetMethodWebRequest(baseUrl, "imagePair?projectId=20");
        conversation.setExceptionsThrownOnErrorStatus(false);
        WebResponse response = conversation.getResponse(request);
        // Test status code
        assertEquals(200, response.getResponseCode());
        // Test content type
        assertEquals("application/json", response.getContentType());
        // Test response body
        String body = response.getText();
        JSONArray json = new JSONArray(body);
        Image[] receivedImages = json.toList().stream().map(new Function<Object, Image>() {
            @Override
            public Image apply(Object object) {
                Map<String, Object> map = (Map<String, Object>) object;
                return new Image().setImageId((long) (int) map.get("id"))
                        .setProjectId((long) (int) map.get("projectId"))
                        .setWidth((long) (int) map.get("width"))
                        .setHeight((long) (int) map.get("height"))
                        .setDepth((long) (int) map.get("depth"))
                        .setMagnification((long) (int) map.get("magnification"))
                        .setResolution((double) map.get("resolution"))
                        .setMimeType((String) map.get("mime"))
                        .setImageServerURLs(((List<String>) map.get("imageServerURLs")).toArray(new String[] {}));
            }
        }).toArray(Image[]::new);
        Arrays.sort(receivedImages, new Comparator<Image>() {
            @Override
            public int compare(Image img1, Image img2) {
                return (int) (img1.getImageId() - img2.getImageId());
            }
        });
        assertArrayEquals(new Image[] {image1, image2}, receivedImages);
    }
    
    @AfterClass
    public static void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
