package no.digipat.compare.servlets;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.meterware.httpunit.HttpUnitOptions;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@RunWith(Suite.class)
@SuiteClasses({
    AuthenticationAndAuthorizationTest.class,
    NextImagePairTest.class,
    SessionServletTest.class,
    ProjectServletTest.class,
    CompareImageTest.class,
    RankingTest.class,
    ProjectUpdateTest.class
})
public class IntegrationTests {
    
    private static String databaseName;
    private static MongoClient client;
    private static URL baseUrl;
    
    @BeforeClass
    public static void setUpClass() throws UnsupportedEncodingException, MalformedURLException {
        String databaseHost = System.getenv("COMPARE_TEST_MONGODB_HOST");
        String databasePort = System.getenv("COMPARE_TEST_MONGODB_PORT");
        String databaseUsername = URLEncoder.encode(System.getenv("COMPARE_TEST_MONGODB_ROOT_USERNAME"), "UTF-8");
        String databasePassword = URLEncoder.encode(System.getenv("COMPARE_TEST_MONGODB_ROOT_PASSWORD"), "UTF-8");
        String databaseName = System.getenv("COMPARE_TEST_MONGODB_DATABASE");
        IntegrationTests.databaseName = databaseName;
        MongoClientURI  MONGO_URI = new MongoClientURI("mongodb://" + databaseUsername + ":" + databasePassword
                + "@" + databaseHost + ":" + databasePort);
        client = new MongoClient(MONGO_URI);
        String tomcatProtocol = System.getenv("COMPARE_TEST_TOMCAT_PROTOCOL"); // http or https
        String tomcatHost = System.getenv("COMPARE_TEST_TOMCAT_HOST");
        String tomcatPort = System.getenv("COMPARE_TEST_TOMCAT_PORT");
        baseUrl = new URL(tomcatProtocol + "://" + tomcatHost + ":" + tomcatPort);
        HttpUnitOptions.setExceptionsThrownOnErrorStatus(false);
    }
    
    /**
     * Gets a client for accessing the test database.
     * 
     * @return a client for the test database
     */
    public static MongoClient getMongoClient() {
        return client;
    }
    
    /**
     * Gets the name of the test database.
     * 
     * @return the name of the test database
     */
    public static String getDatabaseName() {
        return databaseName;
    }
    
    /**
     * Gets the application's base URL.
     * 
     * @return the application's base URL, e.g. {@code http://localhost:8081}
     */
    public static URL getBaseUrl() {
        return baseUrl;
    }
    
    @AfterClass
    public static void tearDownClass() {
        client.close();
    }

}
