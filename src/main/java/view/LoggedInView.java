package view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import data_access.MovieDetailsDataAccessObject;
import interface_adapter.movie_details.MovieDetailsPresenter;
import interface_adapter.movie_details.MovieDetailsViewModel;
import interface_adapter.SearchFilm.*;
import interface_adapter.popular_movies.PopularMoviesController;
import interface_adapter.popular_movies.PopularMoviesViewModel;
import interface_adapter.logout.LogoutController;
import use_case.movie_details.MovieDetailsDataAccessInterface;
import use_case.movie_details.MovieDetailsInputBoundary;
import use_case.movie_details.MovieDetailsInteractor;
import use_case.movie_details.MovieDetailsOutputBoundary;
import interface_adapter.movie_details.MovieDetailsController;
import view.components.HeaderPanel;

public class LoggedInView extends JPanel {

    private SearchFilmController searchFilmController;
    private SearchFilmViewModel searchFilmViewModel;
    private PopularMoviesController popularMoviesController;
    private PopularMoviesViewModel popularMoviesViewModel;
    private MovieDetailsController movieDetailsController;
    private MovieDetailsView movieDetailsView;

    // LOGOUT
    private LogoutController logoutController;

    // Header (so we can wire logout button)
    private final HeaderPanel headerPanel;

    // UI
    private JPanel moviePanel;

    // Cache
    private final Map<String, ImageIcon> iconCache = new HashMap<>();

    public LoggedInView() {

        setBackground(new Color(255, 255, 224));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Header
        headerPanel = new HeaderPanel();
        headerPanel.setMaximumSize(new Dimension(800, 50));

        headerPanel.setLogoutAction(() -> {
            if (logoutController != null) {
                logoutController.execute();
            } else {
                JOptionPane.showMessageDialog(this, "Logout is not wired yet.");
            }
        });

        // Filter
        JPanel filterPanel = buildFilterPanel();

        // Popular lavel
        JLabel popularLabel = new JLabel("Popular Movies");
        popularLabel.setFont(new Font("Open Sans", Font.BOLD, 15));

        // Posters
        JScrollPane scrollPane = buildPosterScrollPane();

        add(Box.createRigidArea(new Dimension(0, 20)));
        add(headerPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(filterPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(popularLabel);
        add(scrollPane);
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
                    JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Search Error",
                            JOptionPane.ERROR_MESSAGE);

                } else if (state.getFilmId() != -1) {
                    int movieId = state.getFilmId();
                    JFrame movieFrame = new JFrame("Movie Page - ID: " + movieId);
                    movieFrame.setSize(500, 300);
                    movieFrame.add(new JLabel("Movie Page for ID: " + movieId), SwingConstants.CENTER);
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
                            JOptionPane.showMessageDialog(this, msg, "Popular Movies Error",
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

    public void setMovieDetailsDependencies() {

        MovieDetailsViewModel movieDetailsViewModel = new MovieDetailsViewModel();
        MovieDetailsOutputBoundary movieDetailsPresenter = new MovieDetailsPresenter(movieDetailsViewModel);
        MovieDetailsDataAccessInterface api = new MovieDetailsDataAccessObject();
        MovieDetailsInputBoundary movieDetailsInteractor = new MovieDetailsInteractor(api, movieDetailsPresenter);
        movieDetailsController = new MovieDetailsController(movieDetailsInteractor);
        movieDetailsView = new MovieDetailsView(movieDetailsViewModel);
    }

    private JPanel buildFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setPreferredSize(new Dimension(800, 40));
        filterPanel.setMaximumSize(new Dimension(800, 40));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton filterButton = new JButton("Filter");
        JLabel browseTitle = new JLabel("Browse Films By:");

        JComboBox<String> yearDropdown = new JComboBox<>(
                new String[]{"All Years", "2025", "2024", "2023", "2022"}
        );
        JComboBox<String> genreDropdown = new JComboBox<>(
                new String[]{"All Genres", "Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Romance"}
        );
        JComboBox<String> ratingDropdown = new JComboBox<>(
                new String[]{"All ratings", "4.5+", "4.0+", "3.5+", "3.0+", "2.5+", "2.0+", "1.5+", "1.0+"}
        );

        JTextField searchField = new JTextField(10);

        // Keep Enter-to-search
        searchField.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a film name");
                return;
            }
            if (searchFilmController == null) {
                JOptionPane.showMessageDialog(this, "Search is not wired yet.");
                return;
            }
            searchFilmController.execute(query);
        });

        JLabel findFilmLabel = new JLabel("Find Film:");

        filterPanel.add(browseTitle);
        filterPanel.add(yearDropdown);
        filterPanel.add(genreDropdown);
        filterPanel.add(ratingDropdown);
        filterPanel.add(filterButton);
        filterPanel.add(findFilmLabel);
        filterPanel.add(searchField);

        return filterPanel;
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
        if (moviePanel == null) {return;}

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
        JButton button = new JButton();
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        try {
            ImageIcon cached = iconCache.get(url);
            if (cached == null) {
                ImageIcon original = new ImageIcon(new java.net.URL(url));
                Image scaled = original.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                cached = new ImageIcon(scaled);
                iconCache.put(url, cached);
            }
            button.setIcon(cached);
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
