package gui;

import exception.BookingException;
import model.Booking;
import model.Movie;
import model.Show;
import payment.CardPayment;
import payment.CashPayment;
import payment.Payment;
import service.MovieBookingSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// Screen 3: Ticket Booking
public class BookingFrame extends JFrame implements ActionListener {
    private MovieBookingSystem system;
    private Movie movie;

    private JComboBox<Show> showCombo = new JComboBox<>();
    private JTextField nameField = new JTextField();
    private JTextField ticketsField = new JTextField();
    private JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"Cash", "Card"});
    private JLabel totalLabel = new JLabel("Rs. 0.00");
    private JCheckBox[] seatBoxes = new JCheckBox[MovieBookingSystem.ALL_SEATS.length];
    private JButton totalButton = new JButton("Calculate Total");
    private JButton bookButton = new JButton("Book Ticket");
    private JButton backButton = new JButton("Back");

    public BookingFrame(MovieBookingSystem system, Movie movie) {
        super("Book Tickets");
        this.system = system;
        this.movie = movie;

        // ----- form -----
        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        form.add(new JLabel("Movie:"));
        form.add(new JLabel(movie.getTitle() + "  (Rs. " + movie.getTicketPrice() + " per ticket)"));
        form.add(new JLabel("Show Time:"));
        form.add(showCombo);
        form.add(new JLabel("Customer Name:"));
        form.add(nameField);
        form.add(new JLabel("Number of Tickets:"));
        form.add(ticketsField);
        form.add(new JLabel("Payment Method:"));
        form.add(paymentCombo);
        form.add(new JLabel("Total Amount:"));
        form.add(totalLabel);

        // ----- seats -----
        JPanel seatPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        seatPanel.setBorder(BorderFactory.createTitledBorder("Select Seats"));
        for (int i = 0; i < seatBoxes.length; i++) {
            seatBoxes[i] = new JCheckBox(MovieBookingSystem.ALL_SEATS[i]);
            seatPanel.add(seatBoxes[i]);
        }

        // ----- buttons -----
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(totalButton);
        buttonPanel.add(bookButton);
        buttonPanel.add(backButton);
        totalButton.addActionListener(this);
        bookButton.addActionListener(this);
        backButton.addActionListener(this);

        setLayout(new BorderLayout());
        add(form, BorderLayout.NORTH);
        add(seatPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadShows();
        showCombo.addActionListener(this);      // added after loading so it does not fire early
        paymentCombo.addActionListener(this);
        refreshSeats();

        setSize(480, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void loadShows() {
        try {
            for (Show s : system.getShows(movie.getMovieId())) {
                showCombo.addItem(s);
            }
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Disables seats that are already booked for the chosen show
    private void refreshSeats() {
        Show show = (Show) showCombo.getSelectedItem();
        if (show == null) {
            return;
        }
        try {
            List<String> booked = system.getBookedSeats(show.getShowId());
            for (int i = 0; i < seatBoxes.length; i++) {
                String seat = MovieBookingSystem.ALL_SEATS[i];
                boolean taken = booked.contains(seat);
                seatBoxes[i].setSelected(false);
                seatBoxes[i].setEnabled(!taken);
                seatBoxes[i].setText(taken ? seat + " (booked)" : seat);
            }
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Polymorphism: returns the parent type Payment, actual object is Cash or Card
    private Payment getSelectedPayment() {
        if (paymentCombo.getSelectedItem().equals("Cash")) {
            return new CashPayment();
        } else {
            return new CardPayment();
        }
    }

    private void calculateTotal() {
        try {
            int tickets = Integer.parseInt(ticketsField.getText().trim());
            if (tickets < 1 || tickets > MovieBookingSystem.ALL_SEATS.length) {
                JOptionPane.showMessageDialog(this,
                        "Number of tickets must be between 1 and " + MovieBookingSystem.ALL_SEATS.length + ".",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double base = tickets * movie.getTicketPrice();
            double total = getSelectedPayment().getFinalAmount(base);
            totalLabel.setText("Rs. " + String.format("%.2f", total));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of tickets.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void bookTicket() {
        try {
            int tickets = Integer.parseInt(ticketsField.getText().trim());

            List<String> chosenSeats = new ArrayList<>();
            for (int i = 0; i < seatBoxes.length; i++) {
                if (seatBoxes[i].isSelected()) {
                    chosenSeats.add(MovieBookingSystem.ALL_SEATS[i]);
                }
            }

            Show show = (Show) showCombo.getSelectedItem();
            if (show == null) {
                throw new BookingException("No show available for this movie.");
            }

            Payment payment = getSelectedPayment();
            Booking booking = system.bookTicket(nameField.getText(), show.getShowId(),
                                                tickets, chosenSeats, payment);

            String paymentMessage = payment.pay(booking.getTotalAmount());
            new ConfirmationFrame(booking, paymentMessage).setVisible(true);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of tickets.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
            refreshSeats();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == showCombo) {
            refreshSeats();
        } else if (e.getSource() == paymentCombo) {
            if (!ticketsField.getText().trim().isEmpty()) {
                calculateTotal();
            }
        } else if (e.getSource() == totalButton) {
            calculateTotal();
        } else if (e.getSource() == bookButton) {
            bookTicket();
        } else if (e.getSource() == backButton) {
            dispose();
        }
    }
}
