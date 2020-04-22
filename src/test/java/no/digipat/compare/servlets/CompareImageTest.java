package no.digipat.compare.servlets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.protocol.MessageBody;
import com.meterware.httpunit.protocol.ParameterCollection;
import com.mongodb.MongoClient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.compare.models.image.ImageChoice;
import no.digipat.compare.models.image.ImageComparison;
import no.digipat.compare.models.project.Project;
import no.digipat.compare.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;

@RunWith(JUnitParamsRunner.class)
public class CompareImageTest {
    
    private static URL baseUrl;
    private static MongoClient client;
    private static String databaseName;
    private MongoImageComparisonDAO comparisonDao;
    private MongoProjectDAO projectDao;
    private WebConversation conversation;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        baseUrl = IntegrationTests.getBaseUrl();
        client = IntegrationTests.getMongoClient();
        databaseName = IntegrationTests.getDatabaseName();
    }
    
    @Before
    public void setUp() throws Exception {
        comparisonDao = new MongoImageComparisonDAO(client, databaseName);
        projectDao = new MongoProjectDAO(client, databaseName);
        
        Project project = new Project().setId(20l).setName("testname").setActive(true);
        projectDao.createProject(project);
        
        conversation = new WebConversation();
        login(conversation);
        conversation.setExceptionsThrownOnErrorStatus(false);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
    private static void login(WebConversation conversation) throws Exception {
        WebRequest loginRequest = createPostRequestWithMessageBody(
                "session",
                "{\"monitorType\": \"normal\", \"hospital\": \"St. Olavs\", \"projectId\": 20}",
                "application/json");
        conversation.sendRequest(loginRequest);
    }
    
    private static PostMethodWebRequest createPostRequestWithMessageBody(String path, String messageBody, String contentType) throws MalformedURLException {
        return new PostMethodWebRequest(new URL(baseUrl, path).toString()) {
            @Override
            protected MessageBody getMessageBody() {
                return new MessageBody("UTF8") {
                    @Override
                    public void writeTo(OutputStream outputStream, ParameterCollection parameters) throws IOException {
                        PrintWriter writer = new PrintWriter(outputStream);
                        writer.print(messageBody);
                        writer.flush();
                    }
                    @Override
                    public String getContentType() {
                        return contentType;
                    }
                };
            }
        };
    }
    
    @Test
    public void testCompareImages() throws Exception {
        PostMethodWebRequest request = createPostRequestWithMessageBody("scoring",
                "{\"projectId\": 20, \"chosen\": {\"id\": 1, \"comment\": \"a comment\"}, \"other\": {\"id\": 2}}",
                "application/json");
        WebResponse response = conversation.sendRequest(request);
        
        assertEquals("\n" + response.getText() + "\n", 200, response.getResponseCode());
        List<ImageComparison> comparisons = comparisonDao.getAllImageComparisons(20L);
        assertEquals(1, comparisons.size());
        ImageComparison comparison = comparisons.get(0);
        assertEquals((Long) 20L, comparison.getProjectId());
        ImageChoice winner = comparison.getWinner();
        assertEquals(1L, winner.getImageId());
        assertEquals("a comment", winner.getComment());
        ImageChoice loser = comparison.getLoser();
        assertEquals(2L, loser.getImageId());
    }
    
    @Test
    @Parameters(method="getStatusCodeParameters")
    public void testStatusCode400(String messageBody, String failureMessage) throws Exception {
        // Use HttpURLConnection to work around the fact that HttpUnit throws an exception
        // on status code 400 even if setExceptionsThrownOnErrorStatus(false) has been called
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl, "scoring").openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Cookie", "JSESSIONID=" + conversation.getCookieValue("JSESSIONID"));
        try (PrintWriter writer = new PrintWriter(connection.getOutputStream())) {
            writer.print(messageBody);
            writer.flush();
        }
        try {
            connection.connect();
        } catch (IOException e) {}
        
        assertEquals(failureMessage, 400, connection.getResponseCode());
        
//        PostMethodWebRequest request = createPostRequestWithMessageBody("scoring", messageBody, "application/json");
//        WebResponse response = conversation.sendRequest(request);
//        
//        assertEquals(failureMessage, 400, response.getResponseCode());
    }
    
    private String[][] getStatusCodeParameters() {
        return new String[][] {
                {"This is not JSON", "Testing invalid JSON"},
                {"{\"chosen\": {\"id\": 1}, \"other\": {\"id\": 2}}", "Testing lack of project ID"},
                {"{\"projectId\": 20, \"other\": {\"id\": 2}}", "Testing lack of chosen image"},
                {"{\"projectId\": 20, \"chosen\": {\"id\": 2}}", "Testing lack of other image"},
                {"{\"projectId\": 20, \"chosen\": {}, \"other\": {\"id\": 2}}", "Testing lack of chosen image ID"},
                {"{\"projectId\": \"abc\", \"chosen\": {\"id\": 1}, \"other\": {\"id\": 2}}", "Testing invalid type for project ID"},
                {"{\"projectId\": 20, \"chosen\": {\"id\": \"abc\"}, \"other\": {\"id\": 2}}", "Testing invalid type for chosen ID"}
        };
    }
    
    @Test
    public void testUnicodeCharactersOnCreation() throws Exception {
        String comment = "ÆØÅæøåαβγ";
        JSONObject requestJson = new JSONObject();
        requestJson.put("projectId", 20);
        requestJson.put("other", new JSONObject("{\"id\": 2}"));
        JSONObject winnerJson = new JSONObject();
        winnerJson.put("comment", comment);
        winnerJson.put("id", 1);
        requestJson.put("chosen", winnerJson);
        
        WebRequest request = createPostRequestWithMessageBody("scoring", requestJson.toString(), "application/json");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals("\n" + response.getText() + "\n", 200, response.getResponseCode());
        ImageComparison comparison = comparisonDao.getAllImageComparisons(20L).get(0);
        assertEquals(comment, comparison.getWinner().getComment());
    }
    
}
