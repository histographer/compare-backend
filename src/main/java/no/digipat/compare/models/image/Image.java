package no.digipat.compare.models.image;

import java.util.Arrays;
import java.util.Objects;

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
    
    /**
     * Indicates whether this image is "equal to" some other object.
     * This image is equal to {@code obj} if and only if {@code obj}
     * is an instance of {@code Image} whose properties have the same
     * values as those of this image.
     * 
     * @param obj the object to which this image is compared
     * 
     * @return {@code true} if {@code obj} is equal to this image,
     * {@code false} otherwise
     * 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Image) {
            Image img = (Image) obj;
            return Objects.equals(id, img.getId())
                    && Objects.equals(width, img.getWidth())
                    && Objects.equals(height, img.getHeight())
                    && Objects.equals(depth, img.getDepth())
                    && Objects.equals(magnification, img.getMagnification())
                    && Objects.equals(resolution, img.getResolution())
                    && Objects.equals(mimeType, img.getMimeType())
                    && Arrays.equals(imageServerURLs, img.getImageServerURLs());
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        // We need to override hashCode since we're overriding equals
        int code = Objects.hash(id, width, height, depth, magnification, resolution, mimeType);
        code += 31 * Arrays.hashCode(imageServerURLs);
        return code;
    }
    
}
