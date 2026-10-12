package model;

// Holds one booking plus the customer/movie/show text needed for display.
public class Booking {
    private int bookingId;
    private String customerName;
    private String movieTitle;
    private String showTime;
    private String seats;
    private int numTickets;
    private double totalAmount;
    private String paymentMethod;
    private String status;

    public Booking(int bookingId, String customerName, String movieTitle, String showTime,
                   String seats, int numTickets, double totalAmount,
                   String paymentMethod, String status) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.movieTitle = movieTitle;
        this.showTime = showTime;
        this.seats = seats;
        this.numTickets = numTickets;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public int getBookingId() { return bookingId; }
    public String getCustomerName() { return customerName; }
    public String getMovieTitle() { return movieTitle; }
    public String getShowTime() { return showTime; }
    public String getSeats() { return seats; }
    public int getNumTickets() { return numTickets; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Text used by the confirmation screen and "View Details"
    public String getDetails() {
        return "Booking ID : " + bookingId + "\n"
             + "Customer   : " + customerName + "\n"
             + "Movie      : " + movieTitle + "\n"
             + "Show Time  : " + showTime + "\n"
             + "Seats      : " + seats + "\n"
             + "Tickets    : " + numTickets + "\n"
             + "Payment    : " + paymentMethod + "\n"
             + "Total      : Rs. " + String.format("%.2f", totalAmount) + "\n"
             + "Status     : " + status;
    }
}
