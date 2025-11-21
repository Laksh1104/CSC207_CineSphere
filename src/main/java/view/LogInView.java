package view;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LogInView {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Background Display");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(900,800);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(255, 255, 224));
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        HeaderPanel headerPanel = new HeaderPanel();
        headerPanel.setMaximumSize(new Dimension(800,50));

        JPanel filterPanel = getFilterPanel();

        JPanel searchPanel = new JPanel();
        searchPanel.setPreferredSize(new Dimension(800, 50));

        JPanel popularFilmPanel = getPopularFilmPanel();

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

        JPanel moviePanel = new JPanel();
        moviePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        moviePanel.setBackground(new Color(255, 255, 224));

        for (String movie: moviePosters) {
            try {
                ImageIcon icon = new ImageIcon(new URL(movie));
                Image scaled = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                JLabel movieLabel = new JLabel(new ImageIcon(scaled));
                moviePanel.add(movieLabel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JScrollPane scrollPane = new JScrollPane(
                moviePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setPreferredSize(new Dimension(850, 340));
        scrollPane.setMaximumSize(new Dimension(850, 340));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);

        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        backgroundPanel.add(headerPanel);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        backgroundPanel.add(filterPanel);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        backgroundPanel.add(popularFilmPanel);
        backgroundPanel.add(scrollPane);

        frame.add(backgroundPanel);
        frame.setVisible(true);
    }

    @NotNull
    private static JPanel getPopularFilmPanel() {
        JPanel popularFilmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        popularFilmPanel.setBackground(new Color(255, 255, 224));
        popularFilmPanel.setPreferredSize(new Dimension(300, 30));
        popularFilmPanel.setMaximumSize(new Dimension(300, 30));
        popularFilmPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel popularFilmLabel = new JLabel("Popular Films This Week: ");
        popularFilmLabel.setFont(new Font("Open Sans", Font.BOLD, 15));
        popularFilmPanel.add(popularFilmLabel);
        return popularFilmPanel;
    }

    @NotNull
    private static JPanel getFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setPreferredSize(new Dimension(800, 40));
        filterPanel.setMaximumSize(new Dimension(800, 40));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton filterButton = new JButton("Filter");
        JLabel browseTitle = new JLabel("Browse Films By:", SwingConstants.LEFT);
        String[] years = {"All Years", "2025", "2024", "2023", "2022"};
        JComboBox<String> yearDropdown = new JComboBox<>(years);

        String[] genres = {"All Genres", "Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Romance"};
        JComboBox<String> genreDropdown = new JComboBox<>(genres);

        String[] ratings = {"All ratings", "4.5+", "4.0+", "3.5+", "3.0+", "2.5+", "2.0+", "1.5+", "1.0+"};
        JComboBox<String> ratingDropdown = new JComboBox<>(ratings);

        JTextField searchField = new JTextField(10);

        JLabel findFilmLabel = new JLabel("Find Film: ");

        filterPanel.add(browseTitle);
        filterPanel.add(yearDropdown);
        filterPanel.add(genreDropdown);
        filterPanel.add(ratingDropdown);
        filterPanel.add(filterButton);
        filterPanel.add(findFilmLabel);
        filterPanel.add(searchField);
        return filterPanel;
    }
}
