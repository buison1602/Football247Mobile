package vn.edu.tlu.buicongson.football247.models;

public class Match {
    private String home_team_id;
    private String away_team_id;
    private String match_timestamp;
    private String status;
    private boolean isFeatured;
    private String score;
    private String elapsed_time;

    public Match() {}

    public String getHome_team_id() {
        return home_team_id;
    }

    public void setHome_team_id(String home_team_id) {
        this.home_team_id = home_team_id;
    }

    public String getAway_team_id() {
        return away_team_id;
    }

    public void setAway_team_id(String away_team_id) {
        this.away_team_id = away_team_id;
    }

    public String getMatch_timestamp() {
        return match_timestamp;
    }

    public void setMatch_timestamp(String match_timestamp) {
        this.match_timestamp = match_timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getElapsed_time() {
        return elapsed_time;
    }

    public void setElapsed_time(String elapsed_time) {
        this.elapsed_time = elapsed_time;
    }
}
