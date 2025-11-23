package view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import interface_adapter.popular_movies.PopularMoviesController;
import interface_adapter.popular_movies.PopularMoviesViewModel;
import use_case.search_film.*;

public class LoggedInView extends JPanel {

    private SearchFilmController searchFilmController;
    private SearchFilmViewModel searchFilmViewModel;
    private PopularMoviesController popularMoviesController;
    private PopularMoviesViewModel popularMoviesViewModel;
    private JPanel moviePanel;

    public LoggedInView() {


        setBackground(new Color(255, 255, 224));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Header
        HeaderPanel headerPanel = new HeaderPanel();
        headerPanel.setMaximumSize(new Dimension(800, 50));

        // Filter
        JPanel filterPanel = buildFilterPanel();

        // Posters
        JScrollPane scrollPane = buildPosterScrollPane();

        add(Box.createRigidArea(new Dimension(0, 20)));
        add(headerPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(filterPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        // add(popularFilmPanel);
        add(scrollPane);
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

        if (popularMoviesController != null) {
            popularMoviesController.loadPopularMovies();
        }
        refreshPopularMovies();
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

        searchField.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a film name");
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

        for (String url: popularMoviesViewModel.getPosterUrls()){
            try {
                ImageIcon icon = new ImageIcon(new URL(url));
                Image scaled = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                moviePanel.add(new JLabel(new ImageIcon(scaled)));
            } catch (Exception ignored) {}
        }
        moviePanel.revalidate();
        moviePanel.repaint();
    }
}
