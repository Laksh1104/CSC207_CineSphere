package view;

import interface_adapter.SearchFilm.*;
import interface_adapter.logout.LogoutController;
import interface_adapter.movie_details.MovieDetailsController;
import interface_adapter.movie_details.MovieDetailsViewModel;
import interface_adapter.popular_movies.PopularMoviesController;
import interface_adapter.popular_movies.PopularMoviesViewModel;
import view.components.FilterPanel;
import view.components.Flyweight.PosterFlyweightFactory;
import view.components.HeaderPanel;
import view.components.ClickableButton;

import javax.swing.*;
import java.awt.*;

public class LoggedInView extends JPanel {

    private final SearchFilmController searchFilmController;
    private final SearchFilmViewModel searchFilmViewModel;

    private final PopularMoviesController popularMoviesController;
    private final PopularMoviesViewModel popularMoviesViewModel;

    private final MovieDetailsController movieDetailsController;
    private final MovieDetailsView movieDetailsView;
    private final MovieDetailsViewModel movieDetailsViewModel;

    // Logout is still injected via setter
    private LogoutController logoutController;

    private ScreenSwitchListener listener;

    private JPanel moviePanel;
    private FilterPanel filterPanel;

    public LoggedInView(SearchFilmController searchFilmController,
                        SearchFilmViewModel searchFilmViewModel,
                        PopularMoviesController popularMoviesController,
                        PopularMoviesViewModel popularMoviesViewModel,
                        MovieDetailsController movieDetailsController,
                        MovieDetailsView movieDetailsView,
                        MovieDetailsViewModel movieDetailsViewModel) {

        this.searchFilmController = searchFilmController;
        this.searchFilmViewModel = searchFilmViewModel;
        this.popularMoviesController = popularMoviesController;
        this.popularMoviesViewModel = popularMoviesViewModel;
        this.movieDetailsController = movieDetailsController;
        this.movieDetailsView = movieDetailsView;
        this.movieDetailsViewModel = movieDetailsViewModel;

        setBackground(new Color(255, 255, 224));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

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
            if (logoutController != null) {
                logoutController.execute();
            } else {
                JOptionPane.showMessageDialog(this, "Logout is not wired yet.");
            }
        });

        headerPanel.setMaximumSize(new Dimension(800, 50));

        filterPanel = new FilterPanel();
        filterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JLabel popularLabel = new JLabel("Popular Movies");
        popularLabel.setFont(new Font("Open Sans", Font.BOLD, 15));

        JScrollPane scrollPane = buildPosterScrollPane();

        add(Box.createRigidArea(new Dimension(0, 20)));
        add(headerPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(filterPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(popularLabel);
        add(scrollPane);

        setupFilterPanelHandlers();
        setupSearchFilmListeners();
        setupPopularMoviesListeners();

        if (popularMoviesController != null) {
            popularMoviesController.loadPopularMovies();
        }
    }

    private void setupFilterPanelHandlers() {
        filterPanel.setOnSearch(query -> {
            if (query == null || query.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a film name.");
                return;
            }
            if (searchFilmController == null) {
                JOptionPane.showMessageDialog(this, "Search is not wired yet.");
                return;
            }
            searchFilmController.execute(query.trim());
        });

        filterPanel.setOnFilter(() -> {
            if (listener != null) listener.onSwitchScreen("Filtered");
        });
    }

    private void setupSearchFilmListeners() {
        if (searchFilmViewModel == null) return;

        searchFilmViewModel.addPropertyChangeListener(evt -> {
            if (!"state".equals(evt.getPropertyName())) return;
            SearchFilmState state = (SearchFilmState) evt.getNewValue();

            SwingUtilities.invokeLater(() -> {
                if (state.getErrorMessage() != null) {
                    JOptionPane.showMessageDialog(
                            this,
                            state.getErrorMessage(),
                            "Search Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (state.getFilmId() != -1) {
                    int movieId = state.getFilmId();
                    movieDetailsController.showMovieDetails(movieId);

                    JFrame movieFrame = new JFrame("Movie Details");
                    movieFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    movieFrame.setSize(800, 900);
                    movieFrame.add(movieDetailsView);
                    movieFrame.setVisible(true);
                }
            });
        });
    }

    private void setupPopularMoviesListeners() {
        if (popularMoviesViewModel == null) return;

        popularMoviesViewModel.addPropertyChangeListener(evt -> {
            String property = evt.getPropertyName();

            if ("posterUrls".equals(property)) {
                SwingUtilities.invokeLater(this::refreshPopularMovies);
            } else if ("errorMessage".equals(property)) {
                String msg = popularMoviesViewModel.getErrorMessage();
                if (msg != null) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(
                                    this,
                                    msg,
                                    "Popular Movies Error",
                                    JOptionPane.ERROR_MESSAGE
                            )
                    );
                }
            }
        });
    }

    public void setScreenSwitchListener(ScreenSwitchListener listener) {
        this.listener = listener;
    }

    public void setLogoutDependencies(LogoutController controller) {
        this.logoutController = controller;
    }

    public void setGenres(java.util.List<String> genres) {
        if (filterPanel != null) {
            filterPanel.setGenres(genres);
        }
    }

    private JScrollPane buildPosterScrollPane() {
        moviePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        moviePanel.setBackground(new Color(255, 255, 224));

        JScrollPane scrollPane = new JScrollPane(
                moviePanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setPreferredSize(new Dimension(850, 340));
        scrollPane.setMaximumSize(new Dimension(850, 340));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);

        return scrollPane;
    }

    private void refreshPopularMovies() {
        if (moviePanel == null || popularMoviesViewModel == null) return;

        moviePanel.removeAll();

        java.util.List<String> posterUrls = popularMoviesViewModel.getPosterUrls();
        java.util.List<Integer> filmIds = popularMoviesViewModel.getFilmIds();

        int count = Math.min(posterUrls.size(), filmIds.size());

        for (int i = 0; i < count; i++) {
            String url = posterUrls.get(i);
            int filmId = filmIds.get(i);
            moviePanel.add(createPosterButton(url, filmId));
        }

        moviePanel.revalidate();
        moviePanel.repaint();
    }

    private JButton createPosterButton(String url, int filmId) {
        JButton button = new ClickableButton();
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setBorder(BorderFactory.createEmptyBorder());
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
            e.printStackTrace();
        }

        button.addActionListener(e -> {
            movieDetailsController.showMovieDetails(filmId);

            JFrame movieFrame = new JFrame("Movie Details");
            movieFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            movieFrame.setSize(800, 900);
            movieFrame.add(movieDetailsView);
            movieFrame.setVisible(true);
        });

        button.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
        return button;
    }
}
