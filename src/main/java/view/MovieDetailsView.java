package view;

import interface_adapter.movie_details.MovieDetailsState;
import interface_adapter.movie_details.MovieDetailsViewModel;
import use_case.movie_details.MovieDetailsOutputData.MovieReviewData;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class MovieDetailsView extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(255, 255, 224);

    private static final int POSTER_WIDTH = 300;
    private static final int POSTER_HEIGHT = 420;

    private static final int DESCRIPTION_HEIGHT = 120;
    private static final int DESCRIPTION_WIDTH = 400;

    private static final int REVIEWS_HEIGHT = 120;
    private static final int REVIEWS_WIDTH = 400;

    private static final int MAX_REVIEWS = 2;


    public MovieDetailsView(MovieDetailsViewModel viewModel) {
        initializePanel();

        viewModel.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case "state" -> displayMovieDetails((MovieDetailsState) evt.getNewValue());
                case "errorMessage" -> displayError((String) evt.getNewValue());
                default -> throw new IllegalStateException("Unknown event " + evt.getPropertyName());
            }
        });
    }

    public void displayMovieDetails(MovieDetailsState state) {
        buildUI(state);
        revalidate();
        repaint();
    }

    public void displayError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void initializePanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    private void buildUI(MovieDetailsState state) {
        removeAll();
        add(createHeaderPanel(state), BorderLayout.NORTH);
        add(createCenterPanel(state), BorderLayout.CENTER);
        add(createBottomPanel(state), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel(MovieDetailsState state) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        header.setOpaque(false);

        JLabel title = bold(new JLabel(state.filmName()));
        JLabel date = new JLabel(" –  %s  –  ".formatted(state.releaseDate()));
        JLabel director = new JLabel("Directed by: %s".formatted(state.director()));

        header.add(title);
        header.add(date);
        header.add(director);

        return header;
    }

    private JPanel createCenterPanel(MovieDetailsState state) {
        JPanel center = new JPanel(new BorderLayout(16, 0));
        center.setOpaque(false);

        center.add(createPosterLabel(state), BorderLayout.WEST);
        center.add(createFactsPanel(state), BorderLayout.CENTER);

        return center;
    }

    private JLabel createPosterLabel(MovieDetailsState state) {
        JLabel poster = new JLabel("Loading...", SwingConstants.CENTER);
        poster.setPreferredSize(new Dimension(POSTER_WIDTH, POSTER_HEIGHT));
        poster.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        loadPosterImage(poster, state);

        return poster;
    }

    private Box createFactsPanel(MovieDetailsState state) {
        Box facts = Box.createVerticalBox();
        facts.setOpaque(false);

        facts.add(new JLabel("Rating: %s".formatted(state.ratingOutOf5())));
        facts.add(Box.createVerticalStrut(6));
        facts.add(new JLabel("Genres: %s".formatted(String.join(", ", state.genres()))));
        facts.add(Box.createVerticalStrut(12));
        facts.add(createWatchlistButton());

        return facts;
    }

    private JButton createWatchlistButton() {
        JButton watchlistBtn = new JButton("Add To Watchlist");
        watchlistBtn.addActionListener(e -> watchlistBtn.setText("Already Watchlisted"));
        return watchlistBtn;
    }

    private JComponent createBottomPanel(MovieDetailsState state) {
        Box bottom = Box.createVerticalBox();
        bottom.setOpaque(false);

        bottom.add(createDescriptionPanel(state));
        bottom.add(Box.createVerticalStrut(12));
        bottom.add(createReviewsPanel(state));

        return bottom;
    }

    private JScrollPane createDescriptionPanel(MovieDetailsState state) {
        JTextArea desc = new JTextArea(state.description() == null ? "" : state.description());
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane descScroll = new JScrollPane(desc,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        descScroll.setBorder(BorderFactory.createTitledBorder("Description"));
        descScroll.setPreferredSize(new Dimension(DESCRIPTION_WIDTH, DESCRIPTION_HEIGHT));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, DESCRIPTION_HEIGHT));

        return descScroll;
    }

    private JScrollPane createReviewsPanel(MovieDetailsState state) {
        String reviewsText = formatReviews(state.reviews(), MAX_REVIEWS);
        JEditorPane reviews = new JEditorPane("text/html", "<html>%s</html>".formatted(reviewsText));
        reviews.setEditable(false);
        reviews.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        reviews.setFont(new JLabel().getFont());
        reviews.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane reviewsScroll = new JScrollPane(reviews,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        reviewsScroll.setBorder(BorderFactory.createTitledBorder("Popular Reviews"));
        reviewsScroll.setPreferredSize(new Dimension(REVIEWS_WIDTH, REVIEWS_HEIGHT));
        reviewsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, REVIEWS_HEIGHT));

        return reviewsScroll;
    }

    private JLabel bold(JLabel l) {
        l.setFont(l.getFont().deriveFont(Font.BOLD, l.getFont().getSize2D() + 1f));
        return l;
    }

    private String formatReviews(final List<MovieReviewData> reviews, int maxReviews) {
        if (reviews == null || reviews.isEmpty()) {
            return "No reviews available.";
        }

        return reviews
                .stream()
                .limit(maxReviews)
                .map(this::formatReview)
                .collect(Collectors.joining("<br><br>"));
    }

    private String formatReview(final MovieReviewData review) {
        return "<b>%s</b>: <blockquote>%s</blockquote>".formatted(
                review.author(),
                review.content().replace("\n", "<br>")
        );
    }

    private void loadPosterImage(JLabel posterLabel, MovieDetailsState state) {
        new Thread(() -> {
            try {
                String posterPath = state.posterUrl();
                if (posterPath != null && !posterPath.isEmpty()) {
                    BufferedImage originalImage = downloadImage(posterPath);

                    if (originalImage != null) {
                        Image scaledImage = scaleImage(originalImage);
                        updatePosterLabel(posterLabel, scaledImage);
                    } else {
                        updatePosterLabelText(posterLabel, "Image not available");
                    }
                } else {
                    updatePosterLabelText(posterLabel, "No poster available");
                }
            } catch (IOException e) {
                updatePosterLabelText(posterLabel, "Failed to load image");
            }
        }).start();
    }

    private BufferedImage downloadImage(String urlString) throws IOException {
        URL url = new URL(urlString);
        return ImageIO.read(url);
    }

    private Image scaleImage(BufferedImage originalImage) {
        return originalImage.getScaledInstance(POSTER_WIDTH, POSTER_HEIGHT, Image.SCALE_SMOOTH);
    }

    private void updatePosterLabel(JLabel posterLabel, Image scaledImage) {
        SwingUtilities.invokeLater(() -> {
            posterLabel.setIcon(new ImageIcon(scaledImage));
            posterLabel.setText(null);
        });
    }

    private void updatePosterLabelText(JLabel posterLabel, String text) {
        SwingUtilities.invokeLater(() -> posterLabel.setText(text));
    }
}