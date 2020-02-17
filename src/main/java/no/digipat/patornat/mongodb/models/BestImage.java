package no.digipat.patornat.mongodb.models;

public class BestImage {
    private Image chosen;
    private Image other;

    public BestImage(Image chosen, Image other) {
        this.chosen = chosen;
        this.other = other;
    }
}
