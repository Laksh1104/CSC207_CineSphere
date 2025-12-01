package view;

import entity.MovieTicket;
import interface_adapter.bookings.BookingsController;
import interface_adapter.logout.LogoutController;
import view.components.HeaderPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MyBookingsView extends JPanel {

    private final BookingsController bookingsController;

    private ScreenSwitchListener listener;
    private LogoutController logoutController;

    private static final Color COLOR = new Color(255, 255, 224);

    private JPanel listPanel;
    private JLabel emptyLabel;

    public MyBookingsView(BookingsController bookingsController) {
        this.bookingsController = bookingsController;

        setBackground(COLOR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createRigidArea(new Dimension(0, 20)));

        HeaderPanel headerPanel = new HeaderPanel();
        headerPanel.setHomeAction(() -> {
            if (listener != null) listener.onSwitchScreen("Home");
        });
        headerPanel.setWatchlistAction(() -> {
            if (listener != null) listener.onSwitchScreen("Watchlist");
        });
        headerPanel.setBookAction(() -> {
            if (listener != null) listener.onSwitchScreen("Booking");
        });
        headerPanel.setMyBookingsAction(() -> {
            if (listener != null) listener.onSwitchScreen("MyBookings");
        });
        headerPanel.setLogoutAction(() -> {
            if (logoutController != null) logoutController.execute();
        });

        headerPanel.setMaximumSize(new Dimension(900, 50));
        add(headerPanel);

        add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel title = new JLabel("My Bookings");
        title.setFont(new Font("Open Sans", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);

        add(Box.createRigidArea(new Dimension(0, 20)));

        listPanel = new JPanel();
        listPanel.setBackground(COLOR);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        emptyLabel = new JLabel("You have no bookings yet.");
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane(
                listPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(900, 500));
        scrollPane.setMaximumSize(new Dimension(900, 500));

        add(scrollPane);
    }

    public void setScreenSwitchListener(ScreenSwitchListener listener) {
        this.listener = listener;
    }

    public void setLogoutDependencies(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    /**
     * Reload bookings for the current user and update the UI.
     */
    public void refresh() {
        listPanel.removeAll();

        List<MovieTicket> bookings = bookingsController.getBookingsForCurrentUser();
        if (bookings == null || bookings.isEmpty()) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(COLOR);
            wrapper.add(emptyLabel, BorderLayout.CENTER);
            wrapper.setMaximumSize(new Dimension(900, 60));
            listPanel.add(wrapper);
        } else {
            for (MovieTicket ticket : bookings) {
                listPanel.add(createBookingCard(ticket));
                listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JComponent createBookingCard(MovieTicket ticket) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(850, 120));

        JLabel movieLabel = new JLabel(ticket.getMovieName());
        movieLabel.setFont(new Font("Open Sans", Font.BOLD, 14));

        JLabel cinemaLabel = new JLabel("Cinema: " + ticket.getCinemaName());
        JLabel dateLabel = new JLabel("Date: " + ticket.getDate());
        JLabel timeLabel = new JLabel("Time: " + ticket.getStartTime() + " - " + ticket.getEndTime());

        String seatsStr = String.join(", ", ticket.getSeats());
        JLabel seatsLabel = new JLabel("Seats: " + seatsStr);
        JLabel costLabel = new JLabel("Total cost: $" + ticket.getCost());

        card.add(movieLabel);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(cinemaLabel);
        card.add(dateLabel);
        card.add(timeLabel);
        card.add(seatsLabel);
        card.add(costLabel);

        return card;
    }
}
