package model;

public class Movie {
    // Encapsulation: fields are private, accessed only through getters/setters
    private int movieId;
    private String title;
    private String genre;
    private int duration;
    private double ticketPrice;

    // Constructor 1: empty
    public Movie() {
    }

    // Constructor 2: with all values (constructor overloading)
    public Movie(int movieId, String title, String genre, int duration, double ticketPrice) {
        this.movieId = movieId;      // this keyword: field = parameter
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.ticketPrice = ticketPrice;
    }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }

    // Method overriding: replaces Object.toString()
    @Override
    public String toString() {
        return title + " (" + genre + ")";
    }
}
