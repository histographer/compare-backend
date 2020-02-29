package no.digipat.patornat.mongodb.models.image;

/**
 * A representation of a single image. All the setters of this
 * class return the instance on which they are called.
 * 
 * @author Jon Wallem Anundsen
 *
 */
public class Image {
    
    private Long id;
    private Long width;
    private Long height;
    private Long depth;
    private Long magnification;
    private Double resolution;
    private String mimeType;
    private String[] imageServerURLs;
    
    public Long getId() {
        return id;
    }
    
    public Image setId(Long id) {
        this.id = id;
        return this;
    }
    
    public Long getWidth() {
        return width;
    }
    
    public Image setWidth(Long width) {
        this.width = width;
        return this;
    }
    
    public Long getHeight() {
        return height;
    }
    
    public Image setHeight(Long height) {
        this.height = height;
        return this;
    }
    
    public Long getDepth() {
        return depth;
    }
    
    public Image setDepth(Long depth) {
        this.depth = depth;
        return this;
    }
    
    public Long getMagnification() {
        return magnification;
    }
    
    public Image setMagnification(Long magnification) {
        this.magnification = magnification;
        return this;
    }
    
    public Double getResolution() {
        return resolution;
    }
    
    public Image setResolution(Double resolution) {
        this.resolution = resolution;
        return this;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public Image setMimeType(String mime) {
        this.mimeType = mime;
        return this;
    }
    
    public String[] getImageServerURLs() {
        return imageServerURLs;
    }
    
    public Image setImageServerURLs(String[] imageServerURLs) {
        this.imageServerURLs = imageServerURLs;
        return this;
    }
    
}
