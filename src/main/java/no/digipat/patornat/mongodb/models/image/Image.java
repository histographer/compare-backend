package no.digipat.patornat.mongodb.models.image;

/**
 * A representation of a single image.
 * 
 * @author Jon Wallem Anundsen
 *
 */
public class Image {
    
    private int id;
    
    public Image(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
}
