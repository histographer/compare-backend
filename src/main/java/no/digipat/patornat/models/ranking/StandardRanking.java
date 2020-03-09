package no.digipat.patornat.models.ranking;

public class StandardRanking {
    private Long id;
    private Float score;
    private Integer numberOfRankings;

    public Long getId() {
        return id;
    }

    public StandardRanking setId(Long id) {
        this.id = id;
        return this;
    }

    public Float getScore() {
        return score;
    }

    public StandardRanking setScore(float score) {
        this.score = score;
        return this;
    }

    public Integer getNumberOfRankings() {
        return numberOfRankings;
    }

    public StandardRanking setNumberOfRankings(int numberOfRankings) {
        this.numberOfRankings = numberOfRankings;
        return this;
    }
}
