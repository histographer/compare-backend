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
import no.digipat.patornat.mongodb.models.image.Image;

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
    public void testCreateImageWithNullId() {
        dao.createImage(new Image());
    }
    
    @Test(expected=IllegalStateException.class)
    public void testCreateImageWithDuplicateId() {
        dao.createImage(new Image().setId(1L).setDepth(2L));
        dao.createImage(new Image().setId(1L).setWidth(100L));
    }
    
    @Test
    public void testGetAllImages() {
        // Test with no data
        assertEquals(0, dao.getAllImages().size());
        // Test with data
        Image image1 = new Image().setId(1L).setDepth(12L).setHeight(150L).setWidth(200L),
                image2 = new Image().setId(69L).setMagnification(4L).setResolution(100.1).setMimeType("image/png"),
                image3 = new Image().setId(1337L).setImageServerURLs(new String[] {"http://www.example.com"});
        dao.createImage(image1);
        dao.createImage(image2);
        dao.createImage(image3);
        List<Image> images = dao.getAllImages();
        Collections.sort(images, new Comparator<Image>() {
            @Override
            public int compare(Image img1, Image img2) {
                return (int) (img1.getId() - img2.getId());
            }
        });
        assertArrayEquals(new Image[] {image1, image2, image3}, images.toArray());
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
