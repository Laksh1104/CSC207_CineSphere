package view;

import data_access.MovieDetailsDataAccessObject;
import interface_adapter.filter_movies.FilterMoviesController;
import interface_adapter.filter_movies.FilterMoviesViewModel;
import interface_adapter.movie_details.*;
import use_case.movie_details.*;
import view.components.FilterPanel;
import view.components.HeaderPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FilteredView:
 * - Uses HeaderPanel + reusable FilterPanel
 * - Filters by year, rating range (0..10), genre, and search
 * - Shows posters in a 2x4 grid with paging
 * - Shows "No movies available" + popup when no results
 */
public class FilteredView extends JFrame {

    private final FilterMoviesController filterMoviesController;
    private final FilterMoviesViewModel filterMoviesViewModel;

    private MovieDetailsController movieDetailsController;
    private MovieDetailsView movieDetailsView;
    private MovieDetailsViewModel movieDetailsViewModel;

    private final Color COLOR = new Color(255, 255, 224);

    private FilterPanel filterPanel;

    private JLabel filteredByLabel;
    private JPanel gridPanel;

    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageLabel;

    private final Map<String, ImageIcon> iconCache = new HashMap<>();

    private int currentPage = 1;
    private static final int PAGE_SIZE = 8;

    private boolean lastResultWasEmpty = false;

    public FilteredView(FilterMoviesController filterMoviesController,
                        FilterMoviesViewModel filterMoviesViewModel) {
        this.filterMoviesController = filterMoviesController;
        this.filterMoviesViewModel = filterMoviesViewModel;

        setTitle("CineSphere - Filtered Results");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);

        buildUI();
        setMovieDetailsDependencies();

        callFilter();
    }

    private void buildUI() {
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(COLOR);
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        HeaderPanel headerPanel = new HeaderPanel();
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        backgroundPanel.add(headerPanel);

        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        filterPanel = new FilterPanel();
        filterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        refreshGenresIntoFilterPanel();

        filterPanel.setOnFilter(() -> {
            currentPage = 1;
            callFilter();
        });

        filterPanel.setOnSearch(query -> {
            currentPage = 1;
            callFilter();
        });

        // ---- Filtered-by label row ----
        JPanel filteredByPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filteredByPanel.setBackground(COLOR);
        filteredByPanel.setPreferredSize(new Dimension(860, 30));
        filteredByPanel.setMaximumSize(new Dimension(860, 30));
        filteredByPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        filteredByLabel = new JLabel();
        filteredByPanel.add(filteredByLabel);

        // ---- Grid panel ----
        gridPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        gridPanel.setBackground(COLOR);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gridPanel.setPreferredSize(new Dimension(850, 650));
        gridPanel.setMaximumSize(new Dimension(850, 650));
        gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ---- Paging panel ----
        JPanel pagingPanel = new JPanel(new BorderLayout());
        pagingPanel.setBackground(COLOR);
        pagingPanel.setPreferredSize(new Dimension(860, 40));
        pagingPanel.setMaximumSize(new Dimension(860, 40));
        pagingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        prevButton = new JButton("<<");
        nextButton = new JButton(">>");
        pageLabel = new JLabel("", SwingConstants.CENTER);

        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                callFilter();
            }
        });

        nextButton.addActionListener(e -> {
            int totalPages = filterMoviesViewModel.getTotalPages();
            if (currentPage < totalPages) {
                currentPage++;
                callFilter();
            }
        });

        pagingPanel.add(prevButton, BorderLayout.WEST);
        pagingPanel.add(pageLabel, BorderLayout.CENTER);
        pagingPanel.add(nextButton, BorderLayout.EAST);

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

    private void refreshGenresIntoFilterPanel() {
        Map<String, Integer> genres = filterMoviesViewModel.getGenres();
        if (genres == null || genres.isEmpty()) return;

        filterPanel.setGenres(new ArrayList<>(genres.keySet()));
    }

    private void callFilter() {
        refreshGenresIntoFilterPanel();

        String year = String.valueOf(filterPanel.getSelectedYear());
        String rating = filterPanel.getRatingString(); // "All Ratings" or "x.y-a.b"
        String genreText = filterPanel.getSelectedGenre();
        String search = filterPanel.getSearchQuery();

        String genreId = null;
        Map<String, Integer> genreMap = filterMoviesViewModel.getGenres();
        if (genreMap != null && genreText != null && !"All Genres".equals(genreText)) {
            Integer id = genreMap.get(genreText);
            if (id != null) genreId = String.valueOf(id);
        }

        updateFilteredByLabel(year, rating, genreText, search);

        filterMoviesController.execute(
                year,
                rating,
                genreId,
                search,
                currentPage
        );

        updateGrid();
        updatePagingControls();
    }

    private void updateFilteredByLabel(String year, String rating, String genre, String search) {
        String ratingText = rating.equalsIgnoreCase("All Ratings")
                ? "Any"
                : rating.replace("-", "–");

        String text = "Filtered by: Year = " + year +
                "   Rating = " + ratingText +
                "   Genre = " + genre +
                (search == null || search.isEmpty() ? "" : "   Search = \"" + search + "\"");

        filteredByLabel.setText(text);
    }

    private void updatePagingControls() {
        List<String> posters = filterMoviesViewModel.getPosters();
        List<Integer> filmIds = filterMoviesViewModel.getFilmIds();

        boolean noResults = posters == null || filmIds == null || posters.isEmpty() || filmIds.isEmpty();
        int totalPages = filterMoviesViewModel.getTotalPages();

        if (noResults) {
            pageLabel.setText("0 / 0");
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            return;
        }

        if (totalPages < 1) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        pageLabel.setText(currentPage + " / " + totalPages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
    }

    private void updateGrid() {
        gridPanel.removeAll();

        List<String> posters = filterMoviesViewModel.getPosters();
        List<Integer> filmIds = filterMoviesViewModel.getFilmIds();

        boolean noResults = posters == null || filmIds == null || posters.isEmpty() || filmIds.isEmpty();

        if (noResults) {
            gridPanel.setLayout(new BorderLayout());
            JLabel msg = new JLabel("No movies available", SwingConstants.CENTER);
            msg.setFont(msg.getFont().deriveFont(Font.BOLD, 22f));
            gridPanel.add(msg, BorderLayout.CENTER);

            if (!lastResultWasEmpty) {
                JOptionPane.showMessageDialog(
                        this,
                        "No movies available for the selected filters.",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            lastResultWasEmpty = true;

            gridPanel.revalidate();
            gridPanel.repaint();
            return;
        }

        lastResultWasEmpty = false;

        gridPanel.setLayout(new GridLayout(2, 4, 10, 10));

        int count = Math.min(PAGE_SIZE, Math.min(posters.size(), filmIds.size()));
        for (int i = 0; i < count; i++) {
            gridPanel.add(createPosterButton(posters.get(i), filmIds.get(i)));
        }

        for (int i = count; i < PAGE_SIZE; i++) {
            JPanel empty = new JPanel();
            empty.setBackground(COLOR);
            empty.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
            gridPanel.add(empty);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JButton createPosterButton(String urlString, int filmId) {
        JButton button = new JButton();
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

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
