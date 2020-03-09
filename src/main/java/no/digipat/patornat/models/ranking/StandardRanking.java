package no.digipat.patornat.models.ranking;

public class StandardRanking {
    private Long id;
    private float score;
    private int numberOfRankings;

    public Long getId() {
        return id;
    }

    public StandardRanking setId(Long id) {
        this.id = id;
        return this;
    }

    public float getScore() {
        return score;
    }

    public StandardRanking setScore(float score) {
        this.score = score;
        return this;
    }

    public int getNumberOfRankings() {
        return numberOfRankings;
    }

    public StandardRanking setNumberOfRankings(int numberOfRankings) {
        this.numberOfRankings = numberOfRankings;
        return this;
    }
}
