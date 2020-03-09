package no.digipat.patornat.models.image;

import static org.junit.Assert.*;

import no.digipat.compare.models.image.Image;
import org.junit.Test;

public class ImageTest {
    
    @Test
    public void testEquality() {
        Image image1 = new Image(), image2 = new Image(), image3 = new Image();
        for (Image image : new Image[] {image1, image2, image3}) {
            image.setId(1L).setWidth(100L).setHeight(50L)
            .setDepth(10L).setMagnification(4L).setResolution(100.1)
            .setMimeType("image/png")
            .setImageServerURLs(new String[] {"https://www.example.com", "http://digipat.no"});
        }
        image3.setImageServerURLs(new String[] {"https://facebook.com"});
        image3.setHeight(150L);
        assertEquals(image1, image2);
        assertNotEquals(image2, image3);
    }
    
    @Test
    public void testHashCode() {
        Image image1 = new Image(), image2 = new Image();
        for (Image image : new Image[] {image1, image2}) {
            image.setId(1L).setWidth(100L).setHeight(50L)
                .setDepth(10L).setMagnification(4L).setResolution(100.1)
                .setMimeType("image/png")
                .setImageServerURLs(new String[] {"https://www.example.com", "http://digipat.no"});
        }
        assertEquals(image1.hashCode(), image2.hashCode());
    }
    
}
