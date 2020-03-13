package no.digipat.compare.mongodb.dao;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;

import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.image.ImageChoice;
import no.digipat.compare.models.image.ImageComparison;
import no.digipat.compare.mongodb.DatabaseUnitTests;
import no.digipat.compare.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.compare.mongodb.dao.MongoImageDAO;

public class MongoImageComparisonDAOTest {
    
    private static MongoClient client;
    private static String databaseName;
    
    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
    }
    
    @Test
    public void testGetAllImageComparisons() {
        MongoImageComparisonDAO dao = new MongoImageComparisonDAO(client, databaseName);
        // Test with no data in database
        assertEquals(0, dao.getAllImageComparisons().size());
        // Test with some data
        ImageComparison comparison1 = new ImageComparison().setSessionID("user1")
                .setWinner(new ImageChoice(1, "comment1")).setLoser(new ImageChoice(2, "comment2"));
        ImageComparison comparison2 = new ImageComparison().setSessionID("user2")
                .setWinner(new ImageChoice(3, "comment3")).setLoser(new ImageChoice(1, "comment4"));
        // TODO project ID
        dao.createImageComparison(comparison1);
        dao.createImageComparison(comparison2);
        List<ImageComparison> allComparisons = dao.getAllImageComparisons();
        assertEquals(2, allComparisons.size());
        Collections.sort(allComparisons, new Comparator<ImageComparison>() {
            // Sort by ID of chosen image so we can more easily test the contents of the list 
            @Override
            public int compare(ImageComparison arg0, ImageComparison arg1) {
                return (int) (arg0.getWinner().getImageId() - arg1.getWinner().getImageId());
            }
        });
        ImageComparison retrievedComparison1 = allComparisons.get(0);
        ImageComparison retrievedComparison2 = allComparisons.get(1);
        assertEquals(comparison1.getSessionID(), retrievedComparison1.getSessionID());
        assertEquals(comparison1.getWinner().getImageId(), retrievedComparison1.getWinner().getImageId());
        assertEquals(comparison1.getWinner().getComment(), retrievedComparison1.getWinner().getComment());
        assertEquals(comparison1.getLoser().getImageId(), retrievedComparison1.getLoser().getImageId());
        assertEquals(comparison1.getLoser().getComment(), retrievedComparison1.getLoser().getComment());
        
        assertEquals(comparison2.getSessionID(), retrievedComparison2.getSessionID());
        assertEquals(comparison2.getWinner().getImageId(), retrievedComparison2.getWinner().getImageId());
        assertEquals(comparison2.getWinner().getComment(), retrievedComparison2.getWinner().getComment());
        assertEquals(comparison2.getLoser().getImageId(), retrievedComparison2.getLoser().getImageId());
        assertEquals(comparison2.getLoser().getComment(), retrievedComparison2.getLoser().getComment());
    }
    
    @Test
    public void testGetNumberOfComparisonsForEachImage() {
        MongoImageDAO imageDao = new MongoImageDAO(client, databaseName);
        MongoImageComparisonDAO comparisonDao = new MongoImageComparisonDAO(client, databaseName);
        assertEquals(0, comparisonDao.getNumberOfComparisonsForEachImage(1).size());
        // TODO project ID
        imageDao.createImage(new Image().setImageId(1L).setMimeType("image/png"));
        imageDao.createImage(new Image().setImageId(2L).setMagnification(123L));
        imageDao.createImage(new Image().setImageId(42L).setHeight(100L));
        comparisonDao.createImageComparison(new ImageComparison().setSessionID("blah-blah")
                .setWinner(new ImageChoice(1L, "")).setLoser(new ImageChoice(2L, "")));
        comparisonDao.createImageComparison(new ImageComparison().setSessionID("blah-2").setWinner(new ImageChoice(2L, ""))
                .setLoser(new ImageChoice(42L, "")));
        List<Map.Entry<Long, Long>> comparisonNumbers = comparisonDao.getNumberOfComparisonsForEachImage(1);
        // TODO project ID
        Collections.sort(comparisonNumbers, (entry1, entry2) -> (int) (entry1.getKey() - entry2.getKey()));
        long[] numbers = comparisonNumbers.stream().mapToLong(entry -> entry.getValue()).toArray();
        assertArrayEquals(new long[] {1, 2, 1}, numbers); // Image 1 is in 1 comparison, image 2 in 2 comparisons, 42 is in 1 comparison
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
