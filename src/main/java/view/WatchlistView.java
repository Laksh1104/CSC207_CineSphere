package view;

import interface_adapter.logout.LogoutController;
import interface_adapter.movie_details.MovieDetailsState;
import interface_adapter.movie_details.MovieDetailsViewModel;
import interface_adapter.watchlist.WatchlistController;
import interface_adapter.watchlist.WatchlistViewModel;
import view.components.Flyweight.PosterFlyweightFactory;
import view.components.HeaderPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * View that displays the current user's watchlist.
 *
 * It observes WatchlistViewModel and triggers the Watchlist use case
 * via WatchlistController.
 */
public class WatchlistView extends JPanel {

    private final WatchlistController watchlistController;
    private final WatchlistViewModel watchlistViewModel;

    private ScreenSwitchListener listener;
    private LogoutController logoutController;

    private static final Color COLOR = new Color(255, 255, 224);

    private JPanel gridPanel;
    private JLabel emptyLabel;

    public WatchlistView(WatchlistController watchlistController,
                         WatchlistViewModel watchlistViewModel) {
        this.watchlistController = watchlistController;
        this.watchlistViewModel = watchlistViewModel;

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

        JLabel title = new JLabel("Your Watchlist");
        title.setFont(new Font("Open Sans", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);

        add(Box.createRigidArea(new Dimension(0, 20)));

        gridPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        gridPanel.setBackground(COLOR);

        emptyLabel = new JLabel("You have no movies in your watchlist yet.");
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane(
                gridPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(900, 500));
        scrollPane.setMaximumSize(new Dimension(900, 500));

        add(scrollPane);

        // Listen to ViewModel changes
        watchlistViewModel.addPropertyChangeListener(evt -> {
            if (!"state".equals(evt.getPropertyName())) return;
            SwingUtilities.invokeLater(this::renderFromState);
        });
    }

    public void setScreenSwitchListener(ScreenSwitchListener listener) {
        this.listener = listener;
    }

    public void setLogoutDependencies(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    /**
     * Called by MainAppFrame whenever the Watchlist screen is opened.
     * Triggers the use case to load the current watchlist.
     */
    public void refresh() {
        watchlistController.loadWatchlist();
    }

    /**
     * Rebuild the gridPanel using the latest WatchlistState.
     */
    private void renderFromState() {
        gridPanel.removeAll();

        List<String> urls = watchlistViewModel.getPosterUrls();
        String error = watchlistViewModel.getErrorMessage();

        if (error != null && !error.isBlank()) {
            JLabel errorLabel = new JLabel(error);
            errorLabel.setForeground(Color.RED);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gridPanel.add(errorLabel);
        } else if (urls == null || urls.isEmpty()) {
            gridPanel.add(emptyLabel);
        } else {
            for (String url : urls) {
                gridPanel.add(createPosterButton(url));
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Creates a clickable poster button for a watchlist item.
     */
    private JButton createPosterButton(String url) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(200, 300));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        try {
            ImageIcon icon = PosterFlyweightFactory.getPoster(
                    url,
                    200,
                    300,
                    () -> {
                        button.setIcon(PosterFlyweightFactory.getPoster(url, 200, 300, null));
                        button.revalidate();
                        button.repaint();
                    }
            );
            button.setIcon(icon);
        } catch (Exception e) {
            button.setText("No Image");
        }

        // On click, open the movie details popup for this poster URL.
        button.addActionListener(e -> openDetailsForPoster(url));

        return button;
    }

    /**
     * Opens a MovieDetailsView for a poster that came from the watchlist.
     * It uses the cached MovieDetailsState (with real title, rating, etc.)
     * when available; otherwise it falls back to a minimal placeholder.
     */
    private void openDetailsForPoster(String posterUrl) {
        MovieDetailsViewModel viewModel = new MovieDetailsViewModel();
        MovieDetailsView detailsView = new MovieDetailsView(viewModel, watchlistController);

        // When the user adds/removes this from their watchlist inside the details view,
        // refresh this WatchlistView automatically.
        detailsView.setOnWatchlistChanged(this::refresh);

        MovieDetailsState cached = MovieDetailsView.getCachedState(posterUrl);

        MovieDetailsState stateToShow;
        if (cached != null) {
            stateToShow = cached;
        } else {
            // Fallback: we never loaded this movie in this session,
            // so show minimal info plus the poster.
            stateToShow = new MovieDetailsState(
                    "Watchlisted Movie",
                    "",
                    "",
                    0.0,
                    Collections.emptyList(),
                    "",
                    Collections.emptyList(),
                    posterUrl
            );
        }

        viewModel.setState(stateToShow);

        JFrame frame = new JFrame("Movie Details");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 900);
        frame.setContentPane(detailsView);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }
}
