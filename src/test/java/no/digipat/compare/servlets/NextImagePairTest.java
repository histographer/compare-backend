package no.digipat.compare.servlets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import no.digipat.compare.models.project.Project;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;
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
import com.meterware.httpunit.protocol.MessageBody;
import com.meterware.httpunit.protocol.ParameterCollection;
import com.mongodb.MongoClient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.image.ImageChoice;
import no.digipat.compare.models.image.ImageComparison;
import no.digipat.compare.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.compare.mongodb.dao.MongoImageDAO;

@RunWith(JUnitParamsRunner.class)
public class NextImagePairTest {
    
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
        Project project = new Project().setId(20l).setName("testname").setActive(true);
        projectDao.createProject(project);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
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
                .setImageServerURLs(new String[] {"https://example.com/Ææα"});
        imageDao.createImage(image2);
        comparisonDao.createImageComparison(new ImageComparison().setProjectId(20L).setSessionID("some_user")
                .setWinner(new ImageChoice(1337L, "good")).setLoser(new ImageChoice(42L, "really bad")));
        WebConversation conversation = new WebConversation();
        login(conversation);
        WebRequest request = new GetMethodWebRequest(baseUrl, "imagePair?projectId=20");
        conversation.setExceptionsThrownOnErrorStatus(false);
        WebResponse response = conversation.getResponse(request);
        // Test status code
        assertEquals(response.getText() + "\n", 200, response.getResponseCode());
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
    
    @Test
    @Parameters(method="getSkipParameters")
    public void testSkipPairs(String skippedList, long expectedId1, long expectedId2) throws Exception {
        // Parametrized to test every possible pair when there are three images.
        // If the "skipped" parameter is ignored or doesn't work as it should,
        // it's pretty unlikely that every run of the test will succeed, as the expected
        // pair would have to be "guessed" correctly three times, which means that
        // the probability of a false positive is (1/3)^3, which is about 3.7 percent.
        imageDao.createImage(new Image().setImageId(1L).setProjectId(20L));
        imageDao.createImage(new Image().setImageId(2L).setProjectId(20L));
        imageDao.createImage(new Image().setImageId(3L).setProjectId(20L));
        WebConversation conversation = new WebConversation();
        login(conversation);
        
        WebRequest request = new GetMethodWebRequest(baseUrl, "imagePair");
        request.setParameter("projectId", "20");
        request.setParameter("skipped", skippedList);
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(response.getText(), 200, response.getResponseCode());
        JSONArray responseArray = new JSONArray(response.getText());
        JSONObject object1 = responseArray.getJSONObject(0),
                object2 = responseArray.getJSONObject(1);
        long id1 = object1.getLong("id"), id2 = object2.getLong("id");
        assertTrue("Expected the unordered pair (" + expectedId1 + ", "  + expectedId2 + "), but got (" + id1 + ", " + id2 + ")",
                (id1 == expectedId1 && id2 == expectedId2) || (id1 == expectedId2 && id2 == expectedId1));
    }
    
    private static Object[][] getSkipParameters() {
        return new Object[][] {
            {"[[1, 2], [1,3]]", 2, 3},
            {"[[1, 2], [2,3]]", 1, 3},
            {"[[1, 3], [2,3]]", 1, 2},
        };
    }
    
    @Test
    @Parameters(method="getInvalidSkippedLists")
    public void testStatusCode400InvalidSkipList(String skippedList) throws Exception {
        WebConversation conversation = new WebConversation();
        login(conversation);
        
        // Workaround for the fact that receiving status code 400 sometimes throws an exception:
        String encodedList = URLEncoder.encode(skippedList, "UTF8");
        HttpResponse response = Request.Get(new URL(baseUrl, "imagePair?projectId=20&skipped=" + encodedList).toString())
            .addHeader("Cookie", "JSESSIONID=" + conversation.getCookieValue("JSESSIONID"))
            .execute().returnResponse();
//        WebRequest request = new GetMethodWebRequest(baseUrl, "imagePair");
//        request.setParameter("projectId", "20");
//        request.setParameter("skipped", skippedList);
//        WebResponse response = conversation.getResponse(request);
        assertEquals("Testing with \"skipped\" array: " + skippedList + ". Response body:\n"
                + IOUtils.toString(response.getEntity().getContent()) + "\n",
                400, response.getStatusLine().getStatusCode());
    }
    
    private static String[][] getInvalidSkippedLists() {
        return new String[][] {
            {""},
            {"hello"},
            {"1"},
            {"[1]"},
            {"[[1, 2, 3]]"},
            {"[[1]]"},
        };
    }
    
    @Test
    public void testStatusCode404OnSkipAllPairs() throws Exception {
        imageDao.createImage(new Image().setImageId(1L).setProjectId(20L));
        imageDao.createImage(new Image().setImageId(2L).setProjectId(20L));
        imageDao.createImage(new Image().setImageId(3L).setProjectId(20L));
        WebConversation conversation = new WebConversation();
        login(conversation);
        
        WebRequest request = new GetMethodWebRequest(baseUrl, "imagePair");
        request.setParameter("projectId", "20");
        request.setParameter("skipped", "[[1, 2], [1, 3], [2, 3]]"); // Skip all three possible unordered pairs
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(404, response.getResponseCode());
    }
    
}
