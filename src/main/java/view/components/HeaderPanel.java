package view.components;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {
    private final Color COLOR = new Color(255, 255, 224);

    private final JButton logoutButton;

    private Runnable onLogout = () -> {};

    public HeaderPanel() {
        super(new FlowLayout(FlowLayout.CENTER, 20, 10));
        setBackground(COLOR);

        JLabel title = new JLabel("CineSphere");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JButton homeButton = new JButton("Home");
        JButton watchlistButton = new JButton("Watchlist");
        JButton bookButton = new JButton("Booking");
        logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> onLogout.run());

        add(title);
        add(homeButton);
        add(watchlistButton);
        add(bookButton);
        add(logoutButton);
    }

    public void setLogoutAction(Runnable action) {
        if (action == null) {
            this.onLogout = () -> {};
        } else {
            this.onLogout = action;
        }
    }
}
