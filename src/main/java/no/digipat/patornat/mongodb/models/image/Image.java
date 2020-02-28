package no.digipat.patornat.mongodb.models.image;

/**
 * A representation of a single image.
 * 
 * @author Jon Wallem Anundsen
 *
 */
public class Image {
    
    private long id;
    
    public Image(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
}
