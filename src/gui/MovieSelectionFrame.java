package gui;

import exception.BookingException;
import model.Movie;
import service.MovieBookingSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// Screen 2: shows the available movies in a table
public class MovieSelectionFrame extends JFrame implements ActionListener {
    private MovieBookingSystem system;
    private List<Movie> movies = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton selectButton;
    private JButton backButton;

    public MovieSelectionFrame(MovieBookingSystem system) {
        super("Select a Movie");
        this.system = system;

        String[] columns = {"Title", "Genre", "Duration (min)", "Price (Rs.)"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);          // table is read-only

        selectButton = new JButton("Select Movie");
        backButton = new JButton("Back");
        selectButton.addActionListener(this);
        backButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectButton);
        buttonPanel.add(backButton);

        setLayout(new BorderLayout());
        add(new JLabel("  Available Movies (select a row):"), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadMovies();

        setSize(550, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void loadMovies() {
        try {
            movies = system.getMovies();
            for (Movie m : movies) {
                tableModel.addRow(new Object[]{m.getTitle(), m.getGenre(), m.getDuration(), m.getTicketPrice()});
            }
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectButton) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a movie first.");
                return;
            }
            new BookingFrame(system, movies.get(row)).setVisible(true);
            dispose();
        } else if (e.getSource() == backButton) {
            dispose();
        }
    }
}
