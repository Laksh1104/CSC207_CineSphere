package view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import interface_adapter.SearchFilm.*;
import interface_adapter.popular_movies.PopularMoviesController;
import interface_adapter.popular_movies.PopularMoviesViewModel;

public class LoggedInView extends JPanel {

    private SearchFilmController searchFilmController;
    private SearchFilmViewModel searchFilmViewModel;

    private PopularMoviesController popularMoviesController;
    private PopularMoviesViewModel popularMoviesViewModel;

    private JPanel moviePanel;

    // ✅ Reusable filter panel (like HeaderPanel)
    private final FilterPanel filterPanel;

    public LoggedInView() {

        setBackground(new Color(255, 255, 224));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Header
        HeaderPanel headerPanel = new HeaderPanel();
        headerPanel.setMaximumSize(new Dimension(800, 50));

        // ✅ Filter (no duplicated UI code)
        filterPanel = new FilterPanel();
        filterPanel.setMaximumSize(new Dimension(860, 70));

        // Posters
        JScrollPane scrollPane = buildPosterScrollPane();

        add(Box.createRigidArea(new Dimension(0, 20)));
        add(headerPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(filterPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(scrollPane);
    }

    public void setSearchDependencies(SearchFilmController controller, SearchFilmViewModel viewModel) {
        this.searchFilmController = controller;
        this.searchFilmViewModel = viewModel;

        // ✅ Wire FilterPanel search to SearchFilmController
        filterPanel.setOnSearch(query -> {
            if (searchFilmController != null) {
                searchFilmController.execute(query);
            }
        });

        // Filter button isn't used on LoggedInView (FilteredView uses it),
        // but we keep it harmless.
        filterPanel.setOnFilter(() -> {
            // Optional: do nothing or show message
            // JOptionPane.showMessageDialog(this, "Use the Browse page to filter movies.");
        });

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

        for (String url : popularMoviesViewModel.getPosterUrls()) {
            try {
                ImageIcon icon = new ImageIcon(new URL(url));
                Image scaled = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                moviePanel.add(new JLabel(new ImageIcon(scaled)));
            } catch (Exception e) {
                System.err.println("Failed to load poster from URL: " + url);
                e.printStackTrace();
            }
        }
        moviePanel.revalidate();
        moviePanel.repaint();
    }
}
