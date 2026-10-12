package gui;

import model.Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Screen 4: Booking Confirmation
public class ConfirmationFrame extends JFrame implements ActionListener {
    private JButton closeButton = new JButton("Close");

    public ConfirmationFrame(Booking booking, String paymentMessage) {
        super("Booking Confirmation");

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setText("BOOKING CONFIRMED\n\n" + booking.getDetails() + "\n\n" + paymentMessage);
        area.setMargin(new Insets(10, 10, 10, 10));

        closeButton.addActionListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(area), BorderLayout.CENTER);
        add(closeButton, BorderLayout.SOUTH);

        setSize(480, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            dispose();
        }
    }
}
