package no.digipat.patornat.mongodb.dao;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import no.digipat.compare.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.compare.mongodb.dao.MongoImageDAO;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;

import no.digipat.patornat.mongodb.DatabaseUnitTests;
import no.digipat.compare.models.image.ImageComparison;
import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.image.ImageChoice;

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
        ImageComparison comparison1 = new ImageComparison("user1", new ImageChoice(1, "comment1"), new ImageChoice(2, "comment2"));
        ImageComparison comparison2 = new ImageComparison("user2", new ImageChoice(3, "comment3"),  new ImageChoice(1, "comment4"));
        dao.createImageComparison(comparison1);
        dao.createImageComparison(comparison2);
        List<ImageComparison> allComparisons = dao.getAllImageComparisons();
        assertEquals(2, allComparisons.size());
        Collections.sort(allComparisons, new Comparator<ImageComparison>() {
            // Sort by ID of chosen image so we can more easily test the contents of the list 
            @Override
            public int compare(ImageComparison arg0, ImageComparison arg1) {
                return (int) (arg0.getChosen().getId() - arg1.getChosen().getId());
            }
        });
        ImageComparison retrievedComparison1 = allComparisons.get(0);
        ImageComparison retrievedComparison2 = allComparisons.get(1);
        assertEquals(comparison1.getUser(), retrievedComparison1.getUser());
        assertEquals(comparison1.getChosen().getId(), retrievedComparison1.getChosen().getId());
        assertEquals(comparison1.getChosen().getComment(), retrievedComparison1.getChosen().getComment());
        assertEquals(comparison1.getOther().getId(), retrievedComparison1.getOther().getId());
        assertEquals(comparison1.getOther().getComment(), retrievedComparison1.getOther().getComment());
        
        assertEquals(comparison2.getUser(), retrievedComparison2.getUser());
        assertEquals(comparison2.getChosen().getId(), retrievedComparison2.getChosen().getId());
        assertEquals(comparison2.getChosen().getComment(), retrievedComparison2.getChosen().getComment());
        assertEquals(comparison2.getOther().getId(), retrievedComparison2.getOther().getId());
        assertEquals(comparison2.getOther().getComment(), retrievedComparison2.getOther().getComment());
    }
    
    @Test
    public void testGetNumberOfComparisonsForEachImage() {
        MongoImageDAO imageDao = new MongoImageDAO(client, databaseName);
        MongoImageComparisonDAO comparisonDao = new MongoImageComparisonDAO(client, databaseName);
        assertEquals(0, comparisonDao.getNumberOfComparisonsForEachImage().size());
        imageDao.createImage(new Image().setId(1L).setMimeType("image/png"));
        imageDao.createImage(new Image().setId(2L).setMagnification(123L));
        imageDao.createImage(new Image().setId(42L).setHeight(100L));
        comparisonDao.createImageComparison(new ImageComparison("blah-blah", new ImageChoice(1L, ""), new ImageChoice(2L, "")));
        comparisonDao.createImageComparison(new ImageComparison("blah-2", new ImageChoice(2L, ""), new ImageChoice(42L, "")));
        List<Map.Entry<Long, Long>> comparisonNumbers = comparisonDao.getNumberOfComparisonsForEachImage();
        Collections.sort(comparisonNumbers, (entry1, entry2) -> (int) (entry1.getKey() - entry2.getKey()));
        long[] numbers = comparisonNumbers.stream().mapToLong(entry -> entry.getValue()).toArray();
        assertArrayEquals(new long[] {1, 2, 1}, numbers); // Image 1 is in 1 comparison, image 2 in 2 comparisons, 42 is in 1 comparison
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
