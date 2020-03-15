package no.digipat.compare.models.image;

public class ImageChoice {
    private long imageId;
    private String comment;

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
