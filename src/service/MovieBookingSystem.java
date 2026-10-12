package service;

import database.DatabaseConnection;
import exception.BookingException;
import model.Booking;
import model.Customer;
import model.Movie;
import model.Show;
import payment.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// The "brain" of the project: all database work and booking rules are here.
// The GUI only calls these methods.
public class MovieBookingSystem {

    // Fixed small set of seats
    public static final String[] ALL_SEATS = {"A1", "A2", "A3", "A4", "B1", "B2", "B3", "B4"};

    // Common SELECT used to read a booking with customer, movie and show details
    private static final String BOOKING_SELECT =
        "SELECT b.booking_id, c.name, m.title, s.show_time, b.seats, b.num_tickets, "
      + "b.total_amount, b.payment_method, b.status "
      + "FROM bookings b "
      + "JOIN customers c ON b.customer_id = c.customer_id "
      + "JOIN shows s ON b.show_id = s.show_id "
      + "JOIN movies m ON s.movie_id = m.movie_id ";

    // ---------- SELECT: movies ----------
    public List<Movie> getMovies() throws BookingException {
        List<Movie> movies = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM movies");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                movies.add(new Movie(rs.getInt("movie_id"), rs.getString("title"),
                        rs.getString("genre"), rs.getInt("duration_minutes"),
                        rs.getDouble("ticket_price")));
            }
        } catch (SQLException e) {
            throw new BookingException("Could not load movies: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(conn);   // always runs
        }
        return movies;
    }

    // ---------- SELECT: shows of one movie ----------
    public List<Show> getShows(int movieId) throws BookingException {
        List<Show> shows = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM shows WHERE movie_id = ?");
            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                shows.add(new Show(rs.getInt("show_id"), rs.getInt("movie_id"), rs.getString("show_time")));
            }
        } catch (SQLException e) {
            throw new BookingException("Could not load shows: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return shows;
    }

    // ---------- SELECT: seats already booked for a show ----------
    public List<String> getBookedSeats(int showId) throws BookingException {
        List<String> bookedSeats = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT seats FROM bookings WHERE show_id = ? AND status = 'BOOKED'");
            ps.setInt(1, showId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] parts = rs.getString("seats").split(",");   // "A1,A2" -> A1, A2
                for (String seat : parts) {
                    bookedSeats.add(seat.trim());
                }
            }
        } catch (SQLException e) {
            throw new BookingException("Could not check seats: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return bookedSeats;
    }

    // ---------- INSERT: new booking ----------
    // Payment is the abstract type: it can be CashPayment or CardPayment (polymorphism)
    public Booking bookTicket(String customerName, int showId, int numTickets,
                              List<String> seats, Payment payment) throws BookingException {

        // 1. Validation (throw our custom exception)
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new BookingException("Customer name cannot be empty.");
        }
        if (numTickets < 1 || numTickets > ALL_SEATS.length) {
            throw new BookingException("Number of tickets must be between 1 and " + ALL_SEATS.length + ".");
        }
        if (seats == null || seats.size() != numTickets) {
            throw new BookingException("Please select exactly " + numTickets + " seat(s).");
        }

        // 2. Check seats are not already booked
        List<String> alreadyBooked = getBookedSeats(showId);
        for (String seat : seats) {
            if (alreadyBooked.contains(seat)) {
                throw new BookingException("Seat " + seat + " is already booked.");
            }
        }

        // 3. Save to database
        int newBookingId = 0;
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            // price per ticket
            PreparedStatement priceStmt = conn.prepareStatement(
                "SELECT m.ticket_price FROM shows s JOIN movies m ON s.movie_id = m.movie_id "
              + "WHERE s.show_id = ?");
            priceStmt.setInt(1, showId);
            ResultSet priceRs = priceStmt.executeQuery();
            if (!priceRs.next()) {
                throw new BookingException("Show not found.");
            }
            double price = priceRs.getDouble("ticket_price");

            // Polymorphism: same method call, different result for Cash / Card
            double total = payment.getFinalAmount(price * numTickets);

            Customer customer = getOrCreateCustomer(conn, customerName.trim());

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO bookings (customer_id, show_id, seats, num_tickets, total_amount, "
              + "payment_method, status) VALUES (?, ?, ?, ?, ?, ?, 'BOOKED')",
                Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, customer.getCustomerId());
            ps.setInt(2, showId);
            ps.setString(3, String.join(",", seats));
            ps.setInt(4, numTickets);
            ps.setDouble(5, total);
            ps.setString(6, payment.getMethodName());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                newBookingId = keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new BookingException("Could not save booking: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return getBookingById(newBookingId);
    }

    // Finds a customer by name, or inserts a new one (SELECT + INSERT)
    private Customer getOrCreateCustomer(Connection conn, String name) throws SQLException {
        PreparedStatement find = conn.prepareStatement("SELECT customer_id FROM customers WHERE name = ?");
        find.setString(1, name);
        ResultSet rs = find.executeQuery();
        if (rs.next()) {
            return new Customer(rs.getInt("customer_id"), name);
        }
        PreparedStatement insert = conn.prepareStatement(
            "INSERT INTO customers (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
        insert.setString(1, name);
        insert.executeUpdate();
        ResultSet keys = insert.getGeneratedKeys();
        if (keys.next()) {
            return new Customer(keys.getInt(1), name);
        }
        throw new SQLException("Failed to create customer record.");
    }

    // ---------- SELECT: one booking by ID ----------
    public Booking getBookingById(int bookingId) throws BookingException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(BOOKING_SELECT + "WHERE b.booking_id = ?");
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return readBooking(rs);
            }
            throw new BookingException("Invalid booking ID: " + bookingId);
        } catch (SQLException e) {
            throw new BookingException("Could not read booking: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    // ---------- Method overloading: two getBookingHistory methods ----------
    // 1) all bookings
    public List<Booking> getBookingHistory() throws BookingException {
        return getBookingHistory("");        // empty text matches every name
    }

    // 2) bookings of one customer (name search)
    public List<Booking> getBookingHistory(String customerName) throws BookingException {
        List<Booking> list = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                BOOKING_SELECT + "WHERE c.name LIKE ? ORDER BY b.booking_id DESC");
            ps.setString(1, "%" + (customerName != null ? customerName.trim() : "") + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readBooking(rs));
            }
        } catch (SQLException e) {
            throw new BookingException("Could not load history: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return list;
    }

    // ---------- UPDATE: cancel booking (status becomes CANCELLED, seats free again) ----------
    public void cancelBooking(int bookingId) throws BookingException {
        Booking booking = getBookingById(bookingId);          // throws if ID is invalid
        if (booking.getStatus().equalsIgnoreCase("CANCELLED")) {
            throw new BookingException("Booking " + bookingId + " is already cancelled.");
        }
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?");
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BookingException("Could not cancel booking: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    // ---------- DELETE: remove a cancelled booking record ----------
    public void deleteBooking(int bookingId) throws BookingException {
        Booking booking = getBookingById(bookingId);
        if (!booking.getStatus().equalsIgnoreCase("CANCELLED")) {
            throw new BookingException("Only cancelled bookings can be deleted. Cancel it first.");
        }
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM bookings WHERE booking_id = ?");
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BookingException("Could not delete booking: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    // Converts one database row into a Booking object
    private Booking readBooking(ResultSet rs) throws SQLException {
        return new Booking(rs.getInt("booking_id"), rs.getString("name"), rs.getString("title"),
                rs.getString("show_time"), rs.getString("seats"), rs.getInt("num_tickets"),
                rs.getDouble("total_amount"), rs.getString("payment_method"), rs.getString("status"));
    }
}
