package no.digipat.patornat.mongodb.models.image;

public class BestImageChoice {
    private ImageChoice chosen;
    private ImageChoice other;
    private String user;


    public BestImageChoice(String user, ImageChoice chosen, ImageChoice other) {
        this.user = user;
        this.chosen = chosen;
        this.other = other;
    }

    public ImageChoice getChosen() {
        return chosen;
    }

    public void setChosen(ImageChoice chosen) {
        this.chosen = chosen;
    }

    public ImageChoice getOther() {
        return other;
    }

    public void setOther(ImageChoice other) {
        this.other = other;
    }

    public String getUser() {
        return user;
    }
}
