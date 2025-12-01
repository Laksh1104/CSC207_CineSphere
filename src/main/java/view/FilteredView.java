package view;

import data_access.MovieDetailsDataAccessObject;
import interface_adapter.filter_movies.FilterMoviesController;
import interface_adapter.filter_movies.FilterMoviesPresenter;
import interface_adapter.filter_movies.FilterMoviesViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.movie_details.MovieDetailsController;
import interface_adapter.movie_details.MovieDetailsPresenter;
import interface_adapter.movie_details.MovieDetailsViewModel;
import interface_adapter.watchlist.WatchlistController;
import use_case.movie_details.MovieDetailsDataAccessInterface;
import use_case.movie_details.MovieDetailsInputBoundary;
import use_case.movie_details.MovieDetailsInteractor;
import view.components.FilterPanel;
import view.components.Flyweight.PosterFlyweightFactory;
import view.components.HeaderPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilteredView extends JPanel {

    private final FilterMoviesController filterMoviesController;
    private final FilterMoviesViewModel filterMoviesViewModel;

    private MovieDetailsController movieDetailsController;
    private MovieDetailsView movieDetailsView;
    private MovieDetailsViewModel movieDetailsViewModel;

    private ScreenSwitchListener listener;
    private LogoutController logoutController;

    private final Color COLOR = new Color(255, 255, 224);

    private JLabel filteredByLabel;
    private JLabel noMoviesLabel;
    private JPanel gridPanel;
    private JLabel pageLabel;

    private FilterPanel filterPanel;
    private JPanel pagingPanel;

    private int currentPage = 1;
    private static final int PAGE_SIZE = 8;

    private final Map<String, String> genreNameToId = new HashMap<>();

    public FilteredView(FilterMoviesController filterMoviesController,
                        FilterMoviesViewModel filterMoviesViewModel,
                        WatchlistController watchlistController) {
        this.filterMoviesController = filterMoviesController;
        this.filterMoviesViewModel = filterMoviesViewModel;

        setLayout(new BorderLayout());
        buildUI();
        setMovieDetailsDependencies(watchlistController);

        callFilter();
    }

    public void setScreenSwitchListener(ScreenSwitchListener listener) {
        this.listener = listener;
    }

    public void setLogoutDependencies(LogoutController controller) {
        this.logoutController = controller;
    }

    private void buildUI() {
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(COLOR);
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));

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
            else JOptionPane.showMessageDialog(this, "Logout is not wired yet.");
        });


        headerPanel.setMaximumSize(new Dimension(900, 50));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(headerPanel);

        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        filterPanel = new FilterPanel();
        filterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        filterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        filterPanel.setOnFilter(() -> {
            currentPage = 1;
            callFilter();
        });

        filterPanel.setOnSearch(q -> {
            currentPage = 1;
            callFilter();
        });

        backgroundPanel.add(filterPanel);

        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(COLOR);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        filteredByLabel = new JLabel();
        filteredByLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        noMoviesLabel = new JLabel("No movies found for the selected filters.");
        noMoviesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        noMoviesLabel.setVisible(false);

        infoPanel.add(filteredByLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        infoPanel.add(noMoviesLabel);

        backgroundPanel.add(infoPanel);

        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        gridPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        gridPanel.setBackground(COLOR);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gridPanel.setPreferredSize(new Dimension(900, 650));
        gridPanel.setMaximumSize(new Dimension(900, 650));
        gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        backgroundPanel.add(gridPanel);

        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        pagingPanel = new JPanel(new BorderLayout());
        pagingPanel.setBackground(COLOR);
        pagingPanel.setPreferredSize(new Dimension(900, 40));
        pagingPanel.setMaximumSize(new Dimension(900, 40));
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

        backgroundPanel.add(pagingPanel);

        add(backgroundPanel, BorderLayout.CENTER);
    }

    private void callFilter() {
        Integer y = filterPanel.getValidatedYearOrShowError();
        if (y == null) {
            clearGridToEmpty();
            return;
        }

        String year = String.valueOf(y);
        String rating = filterPanel.getRatingString();
        String genreText = filterPanel.getSelectedGenre();
        String search = filterPanel.getSearchQuery();

        updateFilteredByLabel(year, rating, genreText, search);

        String genreId = null;
        if (!"All Genres".equalsIgnoreCase(genreText)) {
            genreId = genreNameToId.get(genreText);
        }

        filterMoviesController.execute(year, rating, genreId, search, currentPage);
        updateGrid();
    }

    private void updateFilteredByLabel(String year, String rating, String genre, String search) {
        String text = "Filtered by: Year = " + year +
                "   Rating = " + (rating.equals("All Ratings") ? "Any" : rating) +
                "   Genre = " + genre +
                (search.isEmpty() ? "" : "   Search = \"" + search + "\"");
        filteredByLabel.setText(text);
    }

    private void syncGenresFromViewModel() {
        Map<String, Integer> genres = filterMoviesViewModel.getGenres();
        if (genres == null || genres.isEmpty()) return;

        genreNameToId.clear();
        for (Map.Entry<String, Integer> e : genres.entrySet()) {
            if (e.getKey() != null && !e.getKey().isBlank() && e.getValue() != null) {
                genreNameToId.put(e.getKey(), String.valueOf(e.getValue()));
            }
        }

        filterPanel.setGenres(new ArrayList<>(genreNameToId.keySet()));
    }

    private void clearGridToEmpty() {
        noMoviesLabel.setVisible(false);

        if (gridPanel != null) gridPanel.setVisible(true);
        if (pagingPanel != null) pagingPanel.setVisible(true);

        gridPanel.removeAll();

        for (int i = 0; i < PAGE_SIZE; i++) {
            JPanel empty = new JPanel();
            empty.setBackground(COLOR);
            empty.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
            gridPanel.add(empty);
        }

        pageLabel.setText("0 / 0");
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void updateGrid() {
        syncGenresFromViewModel();

        List<String> posters = filterMoviesViewModel.getPosters();
        List<Integer> filmIds = filterMoviesViewModel.getFilmIds();

        int resultsCount = (filmIds == null) ? 0 : filmIds.size();

        if (resultsCount == 0) {
            noMoviesLabel.setVisible(true);

            gridPanel.removeAll();
            gridPanel.setVisible(false);

            if (pagingPanel != null) pagingPanel.setVisible(false);

            pageLabel.setText(currentPage + " / " + filterMoviesViewModel.getTotalPages());

            gridPanel.revalidate();
            gridPanel.repaint();
            if (pagingPanel != null) {
                pagingPanel.revalidate();
                pagingPanel.repaint();
            }
            return;
        }

        noMoviesLabel.setVisible(false);

        gridPanel.setVisible(true);
        if (pagingPanel != null) pagingPanel.setVisible(true);

        gridPanel.removeAll();

        int count = Math.min(PAGE_SIZE, resultsCount);

        for (int i = 0; i < count; i++) {
            String url = (posters != null && i < posters.size()) ? posters.get(i) : null;
            int filmId = filmIds.get(i);
            gridPanel.add(createPosterButton(url, filmId));
        }

        for (int i = count; i < PAGE_SIZE; i++) {
            JPanel empty = new JPanel();
            empty.setBackground(COLOR);
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

        if (urlString == null || urlString.isBlank()) {
            button.setText("No Image");
        } else {
            ImageIcon icon = PosterFlyweightFactory.getPoster(
                    urlString,
                    200,
                    300,
                    () -> {
                        button.setIcon(PosterFlyweightFactory.getPoster(urlString, 200, 300, null));
                        button.revalidate();
                        button.repaint();
                    }
            );
            button.setIcon(icon);
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

    public void setMovieDetailsDependencies(WatchlistController watchlistController) {
        movieDetailsViewModel = new MovieDetailsViewModel();
        MovieDetailsPresenter presenter = new MovieDetailsPresenter(movieDetailsViewModel);
        MovieDetailsDataAccessInterface api = new MovieDetailsDataAccessObject();
        MovieDetailsInputBoundary interactor = new MovieDetailsInteractor(api, presenter);
        movieDetailsController = new MovieDetailsController(interactor);
        movieDetailsView = new MovieDetailsView(movieDetailsViewModel, watchlistController);
    }
}
