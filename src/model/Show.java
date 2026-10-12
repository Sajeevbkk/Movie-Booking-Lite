package model;

public class Show {
    private int showId;
    private int movieId;
    private String showTime;

    public Show(int showId, int movieId, String showTime) {
        this.showId = showId;
        this.movieId = movieId;
        this.showTime = showTime;
    }

    public int getShowId() { return showId; }
    public void setShowId(int showId) { this.showId = showId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    // The show-time combo box displays this text
    @Override
    public String toString() {
        return showTime;
    }
}
