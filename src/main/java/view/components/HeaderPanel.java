package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class HeaderPanel extends JPanel {
    private final Color COLOR = new Color(255, 255, 224);
    private final JButton homeButton;
    private final JButton bookButton;

    public HeaderPanel() {
        super(new FlowLayout(FlowLayout.CENTER, 20, 10));
        setBackground(COLOR);

        JLabel title = new JLabel("CineSphere");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        homeButton = new JButton("Home");
        JButton watchlistButton = new JButton("Watchlist");
        bookButton = new JButton("Booking");
        JButton logoutButton = new JButton("Logout");

        add(title);
        add(homeButton);
        add(watchlistButton);
        add(bookButton);
        add(logoutButton);
    }

    public void setHomeAction(Runnable action) {
        for (ActionListener al : homeButton.getActionListeners()) {
            homeButton.removeActionListener(al);
        }
        homeButton.addActionListener(e -> action.run());
    }
    public void setBookAction(Runnable action) {
        for (ActionListener al : bookButton.getActionListeners()) {
            bookButton.removeActionListener(al);
        }
        bookButton.addActionListener(e -> action.run());
    }
}
