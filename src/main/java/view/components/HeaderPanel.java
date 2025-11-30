package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class HeaderPanel extends JPanel {
    private final Color COLOR = new Color(255, 255, 224);
    private final JButton homeButton;

    public HeaderPanel() {
        super(new FlowLayout(FlowLayout.CENTER, 20, 10));
        setBackground(COLOR);

        JLabel title = new JLabel("CineSphere");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        homeButton = new JButton("Home");
        JButton watchlistButton = new JButton("Watchlist");
        JButton bookButton = new JButton("Booking");
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
}
