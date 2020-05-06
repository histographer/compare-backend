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
        assertEquals(0, dao.getAllImageComparisons(1).size());
        // Test with some data
        final long projectId1 = 1;
        final long projectId2 = 2;
        ImageComparison comparison1 = new ImageComparison().setSessionId("user1")
                .setWinner(new ImageChoice(1, "comment1"))
                .setLoser(new ImageChoice(2, "comment2"))
                .setProjectId(projectId1);
        ImageComparison comparison2 = new ImageComparison().setSessionId("user2")
                .setWinner(new ImageChoice(3, "comment3"))
                .setLoser(new ImageChoice(1, "comment4"))
                .setProjectId(projectId1);
        ImageComparison comparison3 = new ImageComparison().setSessionId("some guy")
                .setWinner(new ImageChoice(1, "")).setLoser(new ImageChoice(2, ""))
                .setProjectId(projectId2);
        dao.createImageComparison(comparison1);
        dao.createImageComparison(comparison2);
        dao.createImageComparison(comparison3);
        List<ImageComparison> allComparisons = dao.getAllImageComparisons(projectId1);
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
        assertEquals(comparison1.getSessionId(), retrievedComparison1.getSessionId());
        assertEquals(comparison1.getWinner().getImageId(),
                retrievedComparison1.getWinner().getImageId());
        assertEquals(comparison1.getWinner().getComment(),
                retrievedComparison1.getWinner().getComment());
        assertEquals(comparison1.getLoser().getImageId(),
                retrievedComparison1.getLoser().getImageId());
        assertEquals(comparison1.getLoser().getComment(),
                retrievedComparison1.getLoser().getComment());
        assertEquals(comparison1.getProjectId(), retrievedComparison1.getProjectId());
        
        assertEquals(comparison2.getSessionId(), retrievedComparison2.getSessionId());
        assertEquals(comparison2.getWinner().getImageId(),
                retrievedComparison2.getWinner().getImageId());
        assertEquals(comparison2.getWinner().getComment(),
                retrievedComparison2.getWinner().getComment());
        assertEquals(comparison2.getLoser().getImageId(),
                retrievedComparison2.getLoser().getImageId());
        assertEquals(comparison2.getLoser().getComment(),
                retrievedComparison2.getLoser().getComment());
        assertEquals(comparison2.getProjectId(), retrievedComparison2.getProjectId());
    }
    
    @Test
    public void testGetNumberOfComparisonsForEachImage() {
        MongoImageDAO imageDao = new MongoImageDAO(client, databaseName);
        MongoImageComparisonDAO comparisonDao = new MongoImageComparisonDAO(client, databaseName);
        assertEquals(0, comparisonDao.getNumberOfComparisonsForEachImage(1).size());
        final long projectId1 = 10;
        final long projectId2 = 20;
        imageDao.createImage(new Image().setImageId(1L).setMimeType("image/png")
                .setProjectId(projectId1));
        imageDao.createImage(new Image().setImageId(2L).setMagnification(123L)
                .setProjectId(projectId1));
        imageDao.createImage(new Image().setImageId(42L).setHeight(100L)
                .setProjectId(projectId1));
        imageDao.createImage(new Image().setImageId(69L).setProjectId(projectId2));
        imageDao.createImage(new Image().setImageId(420L).setProjectId(projectId2));
        comparisonDao.createImageComparison(
                new ImageComparison().setSessionId("blah-blah")
                    .setWinner(new ImageChoice(1L, ""))
                    .setLoser(new ImageChoice(2L, ""))
                    .setProjectId(projectId1));
        comparisonDao.createImageComparison(
                new ImageComparison()
                    .setSessionId("blah-2").setWinner(new ImageChoice(2L, ""))
                    .setLoser(new ImageChoice(42L, "")).setProjectId(projectId1));
        comparisonDao.createImageComparison(
                new ImageComparison()
                    .setSessionId("blerg")
                    .setWinner(new ImageChoice(69L, ""))
                    .setLoser(new ImageChoice(420L, ""))
                    .setProjectId(projectId2));
        List<Map.Entry<Long, Long>> comparisonNumbers = comparisonDao
                .getNumberOfComparisonsForEachImage(projectId1);
        Collections.sort(comparisonNumbers,
            (entry1, entry2) -> (int) (entry1.getKey() - entry2.getKey()));
        long[] numbers = comparisonNumbers.stream()
                .mapToLong(entry -> entry.getValue()).toArray();
        // Image 1 is in 1 comparison, image 2 is in 2 comparisons, 42 is in 1 comparison:
        assertArrayEquals(new long[] {1, 2, 1}, numbers);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
