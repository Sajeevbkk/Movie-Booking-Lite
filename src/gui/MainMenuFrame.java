package gui;

import service.MovieBookingSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Screen 1: Main Menu. This class also contains main() to start the program.
public class MainMenuFrame extends JFrame implements ActionListener {
    private MovieBookingSystem system;
    private JButton bookButton;
    private JButton historyButton;
    private JButton exitButton;

    public MainMenuFrame(MovieBookingSystem system) {
        super("Movie Ticket Booking System");
        this.system = system;

        JLabel title = new JLabel("Movie Ticket Booking", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        bookButton = new JButton("Book Tickets");
        historyButton = new JButton("Booking History / Cancel");
        exitButton = new JButton("Exit");
        bookButton.addActionListener(this);
        historyButton.addActionListener(this);
        exitButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 60, 30, 60));
        buttonPanel.add(bookButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(exitButton);

        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bookButton) {
            new MovieSelectionFrame(system).setVisible(true);
        } else if (e.getSource() == historyButton) {
            new BookingHistoryFrame(system).setVisible(true);
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        MovieBookingSystem system = new MovieBookingSystem();
        new MainMenuFrame(system).setVisible(true);
    }
}
