package gui;

import exception.BookingException;
import model.Booking;
import service.MovieBookingSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// Screen 5: Booking History, view details, cancel, delete
public class BookingHistoryFrame extends JFrame implements ActionListener {
    private MovieBookingSystem system;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField = new JTextField(12);
    private JTextField idField = new JTextField(5);
    private JButton searchButton = new JButton("Search");
    private JButton showAllButton = new JButton("Show All");
    private JButton detailsButton = new JButton("View Details");
    private JButton cancelButton = new JButton("Cancel Booking");
    private JButton deleteButton = new JButton("Delete Record");
    private JButton backButton = new JButton("Back");

    public BookingHistoryFrame(MovieBookingSystem system) {
        super("Booking History");
        this.system = system;

        String[] columns = {"ID", "Customer", "Movie", "Show", "Seats", "Tickets", "Amount", "Payment", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);

        JPanel top = new JPanel();
        top.add(new JLabel("Customer name:"));
        top.add(searchField);
        top.add(searchButton);
        top.add(showAllButton);

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Booking ID (or select a row):"));
        bottom.add(idField);
        bottom.add(detailsButton);
        bottom.add(cancelButton);
        bottom.add(deleteButton);
        bottom.add(backButton);

        searchButton.addActionListener(this);
        showAllButton.addActionListener(this);
        detailsButton.addActionListener(this);
        cancelButton.addActionListener(this);
        deleteButton.addActionListener(this);
        backButton.addActionListener(this);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    idField.setText(tableModel.getValueAt(row, 0).toString());
                }
            }
        });

        loadTable("");

        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // Fills the table. Uses the two overloaded service methods.
    private void loadTable(String name) {
        try {
            List<Booking> list;
            if (name.isEmpty()) {
                list = system.getBookingHistory();          // overload 1: all
            } else {
                list = system.getBookingHistory(name);      // overload 2: by name
            }
            tableModel.setRowCount(0);
            for (Booking b : list) {
                tableModel.addRow(new Object[]{b.getBookingId(), b.getCustomerName(), b.getMovieTitle(),
                        b.getShowTime(), b.getSeats(), b.getNumTickets(),
                        String.format("%.2f", b.getTotalAmount()), b.getPaymentMethod(), b.getStatus()});
            }
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Typed ID wins; otherwise use the selected table row
    private int getBookingIdInput() throws BookingException {
        String text = idField.getText().trim();
        if (!text.isEmpty()) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                throw new BookingException("Booking ID must be a number.");
            }
        }
        int row = table.getSelectedRow();
        if (row == -1) {
            throw new BookingException("Enter a booking ID or select a row first.");
        }
        return (Integer) tableModel.getValueAt(row, 0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == searchButton) {
                loadTable(searchField.getText().trim());

            } else if (e.getSource() == showAllButton) {
                searchField.setText("");
                loadTable("");

            } else if (e.getSource() == detailsButton) {
                Booking b = system.getBookingById(getBookingIdInput());
                JOptionPane.showMessageDialog(this, b.getDetails(), "Booking Details",
                        JOptionPane.INFORMATION_MESSAGE);

            } else if (e.getSource() == cancelButton) {
                int id = getBookingIdInput();
                int choice = JOptionPane.showConfirmDialog(this,
                        "Cancel booking " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    system.cancelBooking(id);
                    idField.setText("");
                    JOptionPane.showMessageDialog(this, "Booking " + id + " cancelled.");
                    loadTable(searchField.getText().trim());
                }

            } else if (e.getSource() == deleteButton) {
                int id = getBookingIdInput();
                int choice = JOptionPane.showConfirmDialog(this,
                        "Permanently delete record " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    system.deleteBooking(id);
                    idField.setText("");
                    JOptionPane.showMessageDialog(this, "Record " + id + " deleted.");
                    loadTable(searchField.getText().trim());
                }

            } else if (e.getSource() == backButton) {
                dispose();
            }
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
