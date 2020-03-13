package no.digipat.compare.models.image;

import java.util.Arrays;
import java.util.Objects;

/**
 * A representation of a single image in a project. All the setters
 * of this class return the instance on which they are called.
 * 
 * @author Jon Wallem Anundsen
 *
 */
public class Image {
    
    private Long imageId;
    private Long projectId;
    private String fileName;
    private Long width;
    private Long height;
    private Long depth;
    private Long magnification;
    private Double resolution;
    private String mimeType;
    private String[] imageServerURLs;
    
    /**
     * Gets the ID of this image.
     * 
     * @return the ID of a Cytomine abstract image
     */
    public Long getImageId() {
        return imageId;
    }
    
    public Image setImageId(Long id) {
        this.imageId = id;
        return this;
    }
    
    /**
     * Gets the ID of the project that this image belongs to.
     * 
     * @return the ID of a Cytomine project
     */
    public Long getProjectId() {
        return projectId;
    }
    
    public Image setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public Image setFileName(String fileName) {
        this.fileName = fileName;
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
        // TODO update
        if (obj instanceof Image) {
            Image img = (Image) obj;
            return Objects.equals(imageId, img.getImageId())
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
        // TODO update
        // We need to override hashCode since we're overriding equals
        int code = Objects.hash(imageId, width, height, depth, magnification, resolution, mimeType);
        code += 31 * Arrays.hashCode(imageServerURLs);
        return code;
    }
    
}
