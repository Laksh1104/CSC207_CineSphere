package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import interface_adapter.movie_details.MovieDetailsState;
import interface_adapter.movie_details.MovieDetailsViewModel;
import interface_adapter.watchlist.watchlistController;
import use_case.movie_details.MovieDetailsOutputData.MovieReviewData;

/**
 * View for displaying movie details.
 */
public class MovieDetailsView extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(255, 255, 224);

    private static final int GAP_SMALL = 6;
    private static final int GAP_MEDIUM = 12;
    private static final int GAP_LARGE = 16;

    private static final int POSTER_WIDTH = 300;
    private static final int POSTER_HEIGHT = 420;

    private static final int DESCRIPTION_HEIGHT = 120;
    private static final int DESCRIPTION_WIDTH = 400;

    private static final int REVIEWS_HEIGHT = 120;
    private static final int REVIEWS_WIDTH = 400;

    private static final int MAX_REVIEWS = 2;

    /**
     * Constructs a MovieDetailsView.
     *
     * @param viewModel the view model for this view
     */
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

    /**
     * Displays the movie details.
     *
     * @param state the movie details state
     */
    public void displayMovieDetails(MovieDetailsState state) {
        buildUi(state);
        revalidate();
        repaint();
    }

    /**
     * Displays an error message.
     *
     * @param errorMessage the error message to display
     */
    public void displayError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void initializePanel() {
        setLayout(new BorderLayout(GAP_LARGE, GAP_LARGE));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(GAP_LARGE, GAP_LARGE, GAP_LARGE, GAP_LARGE));
    }

    private void buildUi(MovieDetailsState state) {
        removeAll();
        add(createHeaderPanel(state), BorderLayout.NORTH);
        add(createCenterPanel(state), BorderLayout.CENTER);
        add(createBottomPanel(state), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel(MovieDetailsState state) {
        final JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP_MEDIUM, 0));
        header.setOpaque(false);

        final JLabel title = bold(new JLabel(state.filmName()));
        final JLabel date = new JLabel(" - %s - ".formatted(state.releaseDate()));
        final JLabel director = new JLabel("Directed by: %s".formatted(state.director()));

        header.add(title);
        header.add(date);
        header.add(director);

        return header;
    }

    private JPanel createCenterPanel(MovieDetailsState state) {
        final JPanel center = new JPanel(new BorderLayout(GAP_LARGE, 0));
        center.setOpaque(false);

        center.add(createPosterLabel(state), BorderLayout.WEST);
        center.add(createFactsPanel(state), BorderLayout.CENTER);

        return center;
    }

    private JLabel createPosterLabel(MovieDetailsState state) {
        final JLabel poster = new JLabel("Loading...", SwingConstants.CENTER);
        poster.setPreferredSize(new Dimension(POSTER_WIDTH, POSTER_HEIGHT));
        poster.setBorder(BorderFactory.createEmptyBorder(GAP_SMALL, GAP_SMALL, GAP_SMALL, GAP_SMALL));

        loadPosterImage(poster, state);

        return poster;
    }

    private Box createFactsPanel(MovieDetailsState state) {
        final Box facts = Box.createVerticalBox();
        facts.setOpaque(false);

        facts.add(new JLabel("Rating: %s".formatted(state.ratingOutOf5())));
        facts.add(Box.createVerticalStrut(GAP_SMALL));
        facts.add(new JLabel("Genres: %s".formatted(String.join(", ", state.genres()))));
        facts.add(Box.createVerticalStrut(GAP_MEDIUM));
        facts.add(createWatchlistButton(state));

        return facts;
    }

    private JButton createWatchlistButton(MovieDetailsState state) {
        boolean isWatchlisted = watchlistController.isInWatchlist(state.posterUrl());
        final JButton watchlistBtn = new JButton("Add To Watchlist");
        if (isWatchlisted) {
            watchlistBtn.setText("Already Watchlisted");
        }
        watchlistBtn.addActionListener(Ae -> {
            if (isWatchlisted) {
                watchlistBtn.setText("Add to Watchlist");
                watchlistController.removeFromWatchlist(state.posterUrl());
            }
            else {
                watchlistBtn.setText("Already Watchlisted");
                watchlistController.addToWatchlist(state.posterUrl());
            }
        });
        return watchlistBtn;
    }

    private JComponent createBottomPanel(MovieDetailsState state) {
        final Box bottom = Box.createVerticalBox();
        bottom.setOpaque(false);

        bottom.add(createDescriptionPanel(state));
        bottom.add(Box.createVerticalStrut(GAP_MEDIUM));
        bottom.add(createReviewsPanel(state));

        return bottom;
    }

    private JScrollPane createDescriptionPanel(MovieDetailsState state) {
        final JTextArea desc = new JTextArea(Objects.requireNonNullElse(state.description(), ""));
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setBorder(BorderFactory.createEmptyBorder(GAP_SMALL, GAP_SMALL, GAP_SMALL, GAP_SMALL));

        final JScrollPane descScroll = new JScrollPane(desc,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        descScroll.setBorder(BorderFactory.createTitledBorder("Description"));
        descScroll.setPreferredSize(new Dimension(DESCRIPTION_WIDTH, DESCRIPTION_HEIGHT));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, DESCRIPTION_HEIGHT));

        return descScroll;
    }

    private JScrollPane createReviewsPanel(MovieDetailsState state) {
        final String reviewsText = formatReviews(state.reviews(), MAX_REVIEWS);
        final JEditorPane reviews = new JEditorPane("text/html", "<html>%s</html>".formatted(reviewsText));
        reviews.setEditable(false);
        reviews.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        reviews.setFont(new JLabel().getFont());
        reviews.setBorder(BorderFactory.createEmptyBorder(GAP_SMALL, GAP_SMALL, GAP_SMALL, GAP_SMALL));

        final JScrollPane reviewsScroll = new JScrollPane(reviews,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        reviewsScroll.setBorder(BorderFactory.createTitledBorder("Popular Reviews"));
        reviewsScroll.setPreferredSize(new Dimension(REVIEWS_WIDTH, REVIEWS_HEIGHT));
        reviewsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, REVIEWS_HEIGHT));

        return reviewsScroll;
    }

    private JLabel bold(JLabel label) {
        label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize2D()));
        return label;
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
        final Thread thread = new Thread(() -> {
            try {
                final String posterPath = state.posterUrl();
                if (posterPath != null && !posterPath.isEmpty()) {
                    final BufferedImage originalImage = downloadImage(posterPath);

                    if (originalImage != null) {
                        final Image scaledImage = scaleImage(originalImage);
                        updatePosterLabel(posterLabel, scaledImage);
                    }
                    else {
                        updatePosterLabelText(posterLabel, "Image not available");
                    }
                }
                else {
                    updatePosterLabelText(posterLabel, "No poster available");
                }
            }
            catch (final IOException exception) {
                updatePosterLabelText(posterLabel, "Failed to load image");
            }
        });

        thread.start();
    }

    private BufferedImage downloadImage(String urlString) throws IOException {
        final URL url = new URL(urlString);
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
