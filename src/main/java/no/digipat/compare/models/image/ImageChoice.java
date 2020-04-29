package no.digipat.compare.models.image;

/**
 * A representation of a choice of an image as the winner
 * or loser in an image comparison.
 * 
 * @author Kent Are Torvik
 *
 */
public class ImageChoice {
    private long imageId;
    private String comment;

    /**
     * Constructs an {@code ImageChoice} instance.
     * 
     * @param imageId the image ID
     * @param comment the user's comment
     */
    public ImageChoice(long imageId, String comment) {
        this.imageId = imageId;
        this.comment = comment;
    }


    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
