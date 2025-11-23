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

    public LoggedInView() {


        setBackground(new Color(255, 255, 224));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Header
        HeaderPanel headerPanel = new HeaderPanel();
        headerPanel.setMaximumSize(new Dimension(800, 50));

        // Filter
        JPanel filterPanel = buildFilterPanel();

        // Popular film title
        JPanel popularFilmPanel = buildPopularFilmPanel();

        // Posters
        JScrollPane scrollPane = buildPosterScrollPane();

        add(Box.createRigidArea(new Dimension(0, 20)));
        add(headerPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(filterPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(popularFilmPanel);
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
    }


    private JPanel buildPopularFilmPanel() {
        JPanel popularFilmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        popularFilmPanel.setBackground(new Color(255, 255, 224));
        popularFilmPanel.setPreferredSize(new Dimension(300, 30));
        popularFilmPanel.setMaximumSize(new Dimension(300, 30));
        popularFilmPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel popularFilmLabel = new JLabel("Popular Films This Week:");
        popularFilmLabel.setFont(new Font("Open Sans", Font.BOLD, 15));

        popularFilmPanel.add(popularFilmLabel);
        return popularFilmPanel;
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

        String[] moviePosters = {
                "https://image.tmdb.org/t/p/original/xUWUODKPIilQoFUzjHM6wKJkP3Y.jpg",
                "https://image.tmdb.org/t/p/original/v9NLaLBbrkDwq44qG51v8T6sPuI.jpg",
                "https://image.tmdb.org/t/p/original/pHpq9yNUIo6aDoCXEBzjSolywgz.jpg",
                "https://image.tmdb.org/t/p/original/xR0IhVBjbNU34b8erhJCgRbjXo3.jpg",
                "https://image.tmdb.org/t/p/original/c4QA1rFQcyBZKaOOdUrDeL1G9Er.jpg",
                "https://image.tmdb.org/t/p/original/yvirUYrva23IudARHn3mMGVxWqM.jpg",
                "https://image.tmdb.org/t/p/original/fWVSwgjpT2D78VUh6X8UBd2rorW.jpg",
                "https://image.tmdb.org/t/p/original/bcP7FtskwsNp1ikpMQJzDPjofP5.jpg",
                "https://image.tmdb.org/t/p/original/bYe2ZjUhb4Kje0BpWE6kN34u2hv.jpg"
        };

        JPanel moviePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        moviePanel.setBackground(new Color(255, 255, 224));

        for (String url : moviePosters) {
            try {
                ImageIcon icon = new ImageIcon(new URL(url));
                Image scaled = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                moviePanel.add(new JLabel(new ImageIcon(scaled)));
            } catch (Exception ignored) {}
        }

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

}
