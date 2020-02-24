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
    
    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
    }
    
    @Test
    public void testGetAllImages() {
        MongoImageDAO dao = new MongoImageDAO(client, databaseName);
        // Test with no data
        assertEquals(0, dao.getAllImages().size());
        // Test with data
        dao.createImage(new Image(1));
        dao.createImage(new Image(1337));
        dao.createImage(new Image(69));
        List<Image> images = dao.getAllImages();
        Collections.sort(images, new Comparator<Image>() {
            @Override
            public int compare(Image img1, Image img2) {
                return img1.getId() - img2.getId();
            }
        });
        assertArrayEquals(new int[] {1, 69, 1337}, images.stream().mapToInt(img -> img.getId()).toArray());
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
