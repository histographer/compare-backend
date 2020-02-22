package no.digipat.patornat.mongodb.models.image;

public class BestImageChoice {
    private ImageChoice chosen;
    private ImageChoice other;

    public BestImageChoice(ImageChoice chosen, ImageChoice other) {
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

}
