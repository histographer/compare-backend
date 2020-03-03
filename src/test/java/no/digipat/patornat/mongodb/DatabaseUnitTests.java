package no.digipat.patornat.mongodb;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.mongodb.MongoClient;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import no.digipat.patornat.mongodb.dao.MongoImageComparisonDAOTest;
import no.digipat.patornat.mongodb.dao.MongoImageDAOTest;

/**
 * A suite of unit tests that require database connectivity.
 * Test cases in this suite should clear the contents of the
 * test database once all their test methods have been run.
 * 
 * @author Jon Wallem Anundsen
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
    MongoImageComparisonDAOTest.class,
    MongoImageDAOTest.class
})
public class DatabaseUnitTests {
    
    private static MongoClient client;
    private static MongoServer server;
    private static String databaseName = "test";
    
    @BeforeClass
    public static void setUpClass() {
        server = new MongoServer(new MemoryBackend());
        server.bind("localhost", 27017);
        client = new MongoClient("localhost", 27017);
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
    
    @AfterClass
    public static void tearDownClass() {
        client.close();
        server.shutdown();
    }
    
}
