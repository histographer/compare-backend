package no.digipat.compare.models.image;

import static org.junit.Assert.*;

import org.junit.Test;

public class ImageTest {
    
    @Test
    public void testEquality() {
        Image image1 = new Image();
        Image image2 = new Image();
        Image image3 = new Image();
        Image image4 = new Image();
        Image image5 = new Image();
        for (Image image : new Image[] {image1, image2, image3, image4, image5}) {
            image.setImageId(1L).setWidth(100L).setHeight(50L)
            .setDepth(10L).setMagnification(4L).setResolution(100.1)
            .setMimeType("image/png")
            .setImageServerURLs(new String[] {"https://www.example.com", "http://digipat.no"});
        }
        image3.setImageServerURLs(new String[] {"https://facebook.com"});
        image3.setHeight(150L);
        image4.setFileName("image.png");
        image5.setProjectId(123L);
        assertEquals(image1, image2);
        assertNotEquals(image2, image3);
        assertNotEquals(image1, image4);
        assertNotEquals(image1, image5);
    }
    
    @Test
    public void testHashCode() {
        Image image1 = new Image();
        Image image2 = new Image();
        for (Image image : new Image[] {image1, image2}) {
            image.setImageId(1L).setWidth(100L).setHeight(50L)
                .setDepth(10L).setMagnification(4L).setResolution(100.1)
                .setMimeType("image/png")
                .setImageServerURLs(new String[] {"https://www.example.com", "http://digipat.no"});
        }
        assertEquals(image1.hashCode(), image2.hashCode());
    }
    
}
