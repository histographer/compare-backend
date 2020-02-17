package no.digipat.patornat.mongodb.models;

public class BestImage {
    private Image chosen;
    private Image other;

    public BestImage(Image chosen, Image other) {
        this.chosen = chosen;
        this.other = other;
    }

    public Image getChosen() {
        return chosen;
    }

    public void setChosen(Image chosen) {
        this.chosen = chosen;
    }

    public Image getOther() {
        return other;
    }

    public void setOther(Image other) {
        this.other = other;
    }

}
