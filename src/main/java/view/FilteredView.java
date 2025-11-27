package view;

import data_access.MovieDetailsDataAccessObject;
import interface_adapter.filter_movies.FilterMoviesController;
import interface_adapter.filter_movies.FilterMoviesViewModel;
import interface_adapter.movie_details.*;
import use_case.movie_details.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Final FilteredView that:
 * - Uses your EXACT original GUI layout (buttons, spacing, grid, colors)
 * - Uses Clean Architecture (controller + viewmodel)
 * - Converts genre names → TMDB IDs internally
 * - Displays posters from viewmodel instead of calling the API directly
 */
public class FilteredView extends JFrame {

    private final FilterMoviesController filterMoviesController;
    private final FilterMoviesViewModel filterMoviesViewModel;
    private MovieDetailsController movieDetailsController;
    private MovieDetailsView movieDetailsView;
    private MovieDetailsViewModel movieDetailsViewModel;


    // UI components
    private JComboBox<String> yearDropdown;
    private JComboBox<String> ratingDropdown;
    private JComboBox<String> genreDropdown;
    private JTextField searchField;

    private JLabel filteredByLabel;
    private JPanel gridPanel;
    private JLabel pageLabel;

    // caching poster icons
    private final Map<String, ImageIcon> iconCache = new HashMap<>();

    private int currentPage = 1;
    private static final int PAGE_SIZE = 8;

    // Genre → ID mapping
    private static final Map<String, Integer> GENRE_MAP = Map.of(
            "Action", 28,
            "Comedy", 35,
            "Drama", 18,
            "Sci-Fi", 878,
            "Romance", 10749
    );

    public FilteredView(FilterMoviesController filterMoviesController,
                        FilterMoviesViewModel filterMoviesViewModel) {
        this.filterMoviesController = filterMoviesController;
        this.filterMoviesViewModel = filterMoviesViewModel;

        setTitle("CineSphere - Filtered Results");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 800);
        setLocationRelativeTo(null);

        buildUI();
        setMovieDetailsDependencies();
        callFilter();
    }

    private void buildUI() {

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(255, 255, 224));
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("CineSphere", SwingConstants.CENTER);
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 255, 224));
        buttonPanel.setPreferredSize(new Dimension(800, 50));
        buttonPanel.setMaximumSize(new Dimension(800, 50));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton watchlistButton = new JButton("Watchlist");
        JButton bookButton = new JButton("Booking");
        JButton logoutButton = new JButton("Logout");
        JButton homeButton = new JButton("Home");

        buttonPanel.add(title);
        buttonPanel.add(homeButton);
        buttonPanel.add(watchlistButton);
        buttonPanel.add(bookButton);
        buttonPanel.add(logoutButton);

        logoutButton.addActionListener(e -> this.dispose());

        JPanel filterPanel = new JPanel();
        filterPanel.setPreferredSize(new Dimension(800, 40));
        filterPanel.setMaximumSize(new Dimension(800, 40));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton filterButton = new JButton("Filter");
        JLabel browseTitle = new JLabel("Browse by: ", SwingConstants.LEFT);

        String[] years = {"All Years", "2025", "2024", "2023", "2022"};
        yearDropdown = new JComboBox<>(years);

        String[] ratings = {"All Ratings", "4.5+", "4.0+", "3.5+", "3.0+", "2.5+", "2.0+", "1.5+", "1.0+"};
        ratingDropdown = new JComboBox<>(ratings);

        String[] genres = {"All Genres", "Action", "Comedy", "Drama", "Sci-Fi", "Romance"};
        genreDropdown = new JComboBox<>(genres);

        searchField = new JTextField(10);
        JLabel findFilm = new JLabel("Find a Film: ");

        filterPanel.add(browseTitle);
        filterPanel.add(yearDropdown);
        filterPanel.add(ratingDropdown);
        filterPanel.add(genreDropdown);
        filterPanel.add(filterButton);
        filterPanel.add(findFilm);
        filterPanel.add(searchField);

        filterButton.addActionListener(e -> {
            currentPage = 1;
            callFilter();
        });

        JPanel filteredByPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filteredByPanel.setBackground(new Color(255, 255, 224));
        filteredByPanel.setPreferredSize(new Dimension(800, 30));
        filteredByPanel.setMaximumSize(new Dimension(800, 30));
        filteredByPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        filteredByLabel = new JLabel();
        filteredByPanel.add(filteredByLabel);

        gridPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        gridPanel.setBackground(new Color(255, 255, 224));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gridPanel.setPreferredSize(new Dimension(850, 650));
        gridPanel.setMaximumSize(new Dimension(850, 650));
        gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel pagingPanel = new JPanel(new BorderLayout());
        pagingPanel.setBackground(new Color(255, 255, 224));
        pagingPanel.setPreferredSize(new Dimension(800, 40));
        pagingPanel.setMaximumSize(new Dimension(800, 40));
        pagingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton prevButton = new JButton("<<");
        JButton nextButton = new JButton(">>");
        pageLabel = new JLabel("", SwingConstants.CENTER);

        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                callFilter();
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage < filterMoviesViewModel.getTotalPages()) {
                currentPage++;
                callFilter();
            }
        });

        pagingPanel.add(prevButton, BorderLayout.WEST);
        pagingPanel.add(pageLabel, BorderLayout.CENTER);
        pagingPanel.add(nextButton, BorderLayout.EAST);

        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        backgroundPanel.add(buttonPanel);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        backgroundPanel.add(filterPanel);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        backgroundPanel.add(filteredByPanel);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        backgroundPanel.add(gridPanel);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        backgroundPanel.add(pagingPanel);

        add(backgroundPanel);
    }

    private void callFilter() {
        String year = (String) yearDropdown.getSelectedItem();
        String rating = (String) ratingDropdown.getSelectedItem();
        String genreText = (String) genreDropdown.getSelectedItem();
        String search = searchField.getText().trim();

        updateFilteredByLabel();

        Integer genreId = GENRE_MAP.getOrDefault(genreText, null);

        filterMoviesController.execute(
                year,
                rating,
                genreId == null ? null : String.valueOf(genreId),
                search,
                currentPage
        );

        updateGrid();
    }


    private void updateFilteredByLabel() {
        String year = (String) yearDropdown.getSelectedItem();
        String rating = (String) ratingDropdown.getSelectedItem();
        String genre = (String) genreDropdown.getSelectedItem();
        String search = searchField.getText().trim();

        String text = "Filtered by: Year = " + year +
                "   Rating = " + rating +
                "   Genre = " + genre +
                (search.isEmpty() ? "" : "   Search = \"" + search + "\"");

        filteredByLabel.setText(text);
    }


    private void updateGrid() {
        gridPanel.removeAll();

        List<String> posters = filterMoviesViewModel.getPosters();
        List<Integer> filmIds = filterMoviesViewModel.getFilmIds();

        int count = Math.min(PAGE_SIZE, posters.size());

        for (int i = 0; i < count; i++) {
            gridPanel.add(createPosterButton(posters.get(i), filmIds.get(i)));
        }

        for (int i = count; i < PAGE_SIZE; i++) {
            JPanel empty = new JPanel();
            empty.setBackground(new Color(255, 255, 224));
            empty.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
            gridPanel.add(empty);
        }

        pageLabel.setText(currentPage + " / " + filterMoviesViewModel.getTotalPages());

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JButton createPosterButton(String urlString, int filmId) {
        JButton button = new JButton();
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        // Set Image
        try {
            ImageIcon cached = iconCache.get(urlString);
            if (cached == null) {
                ImageIcon original = new ImageIcon(new java.net.URL(urlString));
                Image scaled = original.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                cached = new ImageIcon(scaled);
                iconCache.put(urlString, cached);
            }
            button.setIcon(cached);
        } catch (Exception e) {
            button.setText("No Image");
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

    public void setMovieDetailsDependencies() {
        movieDetailsViewModel = new MovieDetailsViewModel();
        MovieDetailsPresenter presenter = new MovieDetailsPresenter(movieDetailsViewModel);
        MovieDetailsDataAccessInterface api = new MovieDetailsDataAccessObject();
        MovieDetailsInputBoundary interactor = new MovieDetailsInteractor(api, presenter);
        movieDetailsController = new MovieDetailsController(interactor);
        movieDetailsView = new MovieDetailsView(movieDetailsViewModel);
    }

}
