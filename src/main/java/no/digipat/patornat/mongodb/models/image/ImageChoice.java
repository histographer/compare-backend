package no.digipat.patornat.mongodb.models.image;

public class ImageChoice {
    private int id;
    private String comment;

    public ImageChoice(int id, String comment) {
        this.id = id;
        this.comment = comment;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
