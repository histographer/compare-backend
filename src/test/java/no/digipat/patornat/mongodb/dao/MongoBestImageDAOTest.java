package no.digipat.patornat.mongodb.dao;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;

import no.digipat.patornat.mongodb.DatabaseUnitTests;
import no.digipat.patornat.mongodb.models.image.BestImageChoice;
import no.digipat.patornat.mongodb.models.image.ImageChoice;

public class MongoBestImageDAOTest {
    
    private static MongoClient client;
    private static String databaseName;
    
    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
    }
    
    @Test
    public void testGetAllBestImageChoices() {
        MongoBestImageDAO dao = new MongoBestImageDAO(client, databaseName);
        // Test with no data in database
        assertEquals(0, dao.getAllBestImageChoices().size());
        // Test with some data
        BestImageChoice choice1 = new BestImageChoice("user1", new ImageChoice(1, "comment1"), new ImageChoice(2, "comment2"));
        BestImageChoice choice2 = new BestImageChoice("user2", new ImageChoice(3, "comment3"),  new ImageChoice(1, "comment4"));
        dao.createBestImage(choice1);
        dao.createBestImage(choice2);
        List<BestImageChoice> allChoices = dao.getAllBestImageChoices();
        assertEquals(2, allChoices.size());
        Collections.sort(allChoices, new Comparator<BestImageChoice>() {
            // Sort by ID of chosen image so we can more easily test the contents of the list 
            @Override
            public int compare(BestImageChoice arg0, BestImageChoice arg1) {
                return (int) (arg0.getChosen().getId() - arg1.getChosen().getId());
            }
        });
        BestImageChoice retrievedChoice1 = allChoices.get(0);
        BestImageChoice retrievedChoice2 = allChoices.get(1);
        assertEquals(choice1.getUser(), retrievedChoice1.getUser());
        assertEquals(choice1.getChosen().getId(), retrievedChoice1.getChosen().getId());
        assertEquals(choice1.getChosen().getComment(), retrievedChoice1.getChosen().getComment());
        assertEquals(choice1.getOther().getId(), retrievedChoice1.getOther().getId());
        assertEquals(choice1.getOther().getComment(), retrievedChoice1.getOther().getComment());
        
        assertEquals(choice2.getUser(), retrievedChoice2.getUser());
        assertEquals(choice2.getChosen().getId(), retrievedChoice2.getChosen().getId());
        assertEquals(choice2.getChosen().getComment(), retrievedChoice2.getChosen().getComment());
        assertEquals(choice2.getOther().getId(), retrievedChoice2.getOther().getId());
        assertEquals(choice2.getOther().getComment(), retrievedChoice2.getOther().getComment());
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
