package view;

import data_access.MovieDetailsDataAccessObject;
import interface_adapter.SearchFilm.*;
import interface_adapter.logout.LogoutController;
import interface_adapter.movie_details.MovieDetailsController;
import interface_adapter.movie_details.MovieDetailsPresenter;
import interface_adapter.movie_details.MovieDetailsViewModel;
import interface_adapter.popular_movies.PopularMoviesController;
import interface_adapter.popular_movies.PopularMoviesViewModel;
import interface_adapter.watchlist.WatchlistController;
import use_case.movie_details.MovieDetailsDataAccessInterface;
import use_case.movie_details.MovieDetailsInputBoundary;
import use_case.movie_details.MovieDetailsInteractor;
import use_case.movie_details.MovieDetailsOutputBoundary;
import view.components.FilterPanel;
import view.components.Flyweight.PosterFlyweightFactory;
import view.components.HeaderPanel;
import view.components.ClickableButton;

import javax.swing.*;
import java.awt.*;

public class LoggedInView extends JPanel {

    private SearchFilmController searchFilmController;
    private SearchFilmViewModel searchFilmViewModel;

    private PopularMoviesController popularMoviesController;
    private PopularMoviesViewModel popularMoviesViewModel;

    private MovieDetailsController movieDetailsController;
    private MovieDetailsView movieDetailsView;

    private LogoutController logoutController;

    private ScreenSwitchListener listener;

    private JPanel moviePanel;
    private FilterPanel filterPanel;

    public LoggedInView() {
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

    public void setScreenSwitchListener(ScreenSwitchListener listener) {
        this.listener = listener;
    }

    public void setLogoutDependencies(LogoutController controller) {
        this.logoutController = controller;
    }

    public void setSearchDependencies(SearchFilmController controller, SearchFilmViewModel viewModel) {
        this.searchFilmController = controller;
        this.searchFilmViewModel = viewModel;

        viewModel.addPropertyChangeListener(evt -> {
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

    public void setPopularMoviesDependencies(PopularMoviesController controller, PopularMoviesViewModel viewModel) {
        this.popularMoviesController = controller;
        this.popularMoviesViewModel = viewModel;

        viewModel.addPropertyChangeListener(evt -> {
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

        if (popularMoviesController != null) {
            popularMoviesController.loadPopularMovies();
        }
    }

    public void setMovieDetailsDependencies(WatchlistController watchlistController) {
        MovieDetailsViewModel movieDetailsViewModel = new MovieDetailsViewModel();
        MovieDetailsOutputBoundary movieDetailsPresenter = new MovieDetailsPresenter(movieDetailsViewModel);
        MovieDetailsDataAccessInterface api = new MovieDetailsDataAccessObject();
        MovieDetailsInputBoundary movieDetailsInteractor = new MovieDetailsInteractor(api, movieDetailsPresenter);

        movieDetailsController = new MovieDetailsController(movieDetailsInteractor);
        movieDetailsView = new MovieDetailsView(movieDetailsViewModel, watchlistController);
    }

    /**
     * NEW: allow MainAppFrame to push the genre list into this view's FilterPanel.
     */
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
        if (moviePanel == null) return;

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
