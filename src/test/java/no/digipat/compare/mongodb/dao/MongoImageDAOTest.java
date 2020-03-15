package no.digipat.compare.mongodb.dao;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;

import no.digipat.compare.models.image.Image;
import no.digipat.compare.mongodb.DatabaseUnitTests;
import no.digipat.compare.mongodb.dao.MongoImageDAO;

public class MongoImageDAOTest {

    private static MongoClient client;
    private static String databaseName;
    private static MongoImageDAO dao;
    
    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
        dao = new MongoImageDAO(client, databaseName);
    }
    
    @Test(expected=NullPointerException.class)
    public void testCreateNullImage() {
        dao.createImage(null);
    }
    
    @Test(expected=NullPointerException.class)
    public void testCreateImageWithNullImageId() {
        dao.createImage(new Image().setProjectId(123L));
    }
    
    @Test(expected=NullPointerException.class)
    public void testCreateImageWithNullProjectId() {
        dao.createImage(new Image().setImageId(42L));
    }
    
    @Test
    public void testCreateImageWithDuplicateImageId() {
        // We don't expect an exception here because duplicate image
        // IDs are allowed as long as the project IDs are different
        dao.createImage(new Image().setImageId(1L).setProjectId(123L));
        dao.createImage(new Image().setImageId(1L).setProjectId(456L));
    }
    
    @Test(expected=IllegalStateException.class)
    public void testCreateImageWithDuplicateCompositeId() {
        dao.createImage(new Image().setImageId(1L).setProjectId(123L).setDepth(2L));
        dao.createImage(new Image().setImageId(1L).setProjectId(123L).setWidth(100L));
    }
    
    @Test
    public void testGetAllImages() {
        // Test with no data
        assertEquals(0, dao.getAllImages(1L).size());
        // Test with data
        final long projectId1 = 1, projectId2 = 2;
        Image image1 = new Image().setImageId(1L).setProjectId(projectId1).setDepth(12L).setHeight(150L)
                    .setWidth(200L).setFileName("image 1.jpeg"),
                image2 = new Image().setImageId(69L).setProjectId(projectId1).setMagnification(4L)
                    .setResolution(100.1).setMimeType("image/png"),
                image3 = new Image().setImageId(1337L).setProjectId(projectId1)
                    .setImageServerURLs(new String[] {"http://www.example.com"}),
                image4 = new Image().setImageId(10L).setProjectId(projectId2); // Image 4 belongs to a different project
        dao.createImage(image1);
        dao.createImage(image2);
        dao.createImage(image3);
        dao.createImage(image4);
        List<Image> images = dao.getAllImages(projectId1);
        Collections.sort(images, new Comparator<Image>() {
            @Override
            public int compare(Image img1, Image img2) {
                return (int) (img1.getImageId() - img2.getImageId());
            }
        });
        assertArrayEquals(new Image[] {image1, image2, image3}, images.toArray());
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
