package no.digipat.patornat.servlets;
import static org.junit.Assert.*;

import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class ExampleTest {
    // An example of how to access and use the resources from the integration test suite
    // Feel free to delete this class once there are some other tests
    
    private static URL baseUrl;
    private static MongoClient client;
    private static MongoDatabase database;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        client = IntegrationTests.getMongoClient();
        String databaseName = IntegrationTests.getDatabaseName();
        database = client.getDatabase(databaseName);
    }
    
    @Test
    public void testHttpCode() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(baseUrl, "testservlet");
        WebResponse response = conversation.getResponse(request);
        assertEquals(200, response.getResponseCode());
    }
    
}
