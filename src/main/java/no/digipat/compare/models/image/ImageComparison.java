package no.digipat.compare.models.image;

public class ImageComparison {
    private ImageChoice winner;
    private ImageChoice loser;
    private String sessionId;
    private Long projectId;

    public ImageChoice getWinner() {
        return winner;
    }

    public ImageComparison setWinner(ImageChoice winner) {
        this.winner = winner;
        return this;
    }

    public ImageChoice getLoser() {
        return loser;
    }

    public ImageComparison setLoser(ImageChoice loser) {
        this.loser = loser;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }
    
    public ImageComparison setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public ImageComparison setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }
    
}
