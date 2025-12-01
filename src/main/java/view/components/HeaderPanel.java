package view.components;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {

    private Runnable onHome = () -> {};
    private Runnable onWatchlist = () -> {};
    private Runnable onBook = () -> {};
    private Runnable onMyBookings = () -> {};
    private Runnable onLogout = () -> {};

    public HeaderPanel() {
        super(new FlowLayout(FlowLayout.CENTER, 20, 10));
        Color COLOR = new Color(255, 255, 224);
        setBackground(COLOR);

        JLabel title = new JLabel("CineSphere");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JButton homeButton = new JButton("Home");
        JButton watchlistButton = new JButton("Watchlist");
        JButton bookButton = new JButton("Booking");
        JButton myBookingsButton = new JButton("My Bookings");
        JButton logoutButton = new JButton("Logout");

        homeButton.addActionListener(e -> onHome.run());
        watchlistButton.addActionListener(e -> onWatchlist.run());
        bookButton.addActionListener(e -> onBook.run());
        myBookingsButton.addActionListener(e -> onMyBookings.run());
        logoutButton.addActionListener(e -> onLogout.run());

        add(title);
        add(homeButton);
        add(watchlistButton);
        add(bookButton);
        add(myBookingsButton);
        add(logoutButton);
    }

    public void setHomeAction(Runnable action) { this.onHome = (action == null) ? () -> {} : action; }
    public void setWatchlistAction(Runnable action) { this.onWatchlist = (action == null) ? () -> {} : action; }
    public void setBookAction(Runnable action) { this.onBook = (action == null) ? () -> {} : action; }
    public void setMyBookingsAction(Runnable action) { this.onMyBookings = (action == null) ? () -> {} : action; }
    public void setLogoutAction(Runnable action) { this.onLogout = (action == null) ? () -> {} : action; }
}
