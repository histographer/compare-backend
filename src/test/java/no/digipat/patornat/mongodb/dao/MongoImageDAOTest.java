package no.digipat.patornat.mongodb.dao;

import static org.junit.Assert.*;

import java.util.Arrays;
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
    
    @Test
    public void testGetAllImages() {
        // Test with no data
        assertEquals(0, dao.getAllImages().size());
        // Test with data
        dao.createImage(new Image().setId(1L));
        dao.createImage(new Image().setId(1337L));
        dao.createImage(new Image().setId(69L));
        List<Image> images = dao.getAllImages();
        Collections.sort(images, new Comparator<Image>() {
            @Override
            public int compare(Image img1, Image img2) {
                return (int) (img1.getId() - img2.getId());
            }
        });
        assertArrayEquals(new long[] {1, 69, 1337}, images.stream().mapToLong(img -> img.getId()).toArray());
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
