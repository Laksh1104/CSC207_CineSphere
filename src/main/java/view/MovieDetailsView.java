package view;

import entity.MovieDetails;
import entity.MovieReview;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
import java.util.stream.Collectors;

public class MovieDetailsView extends JFrame {

    private static final Color BACKGROUND_COLOR = new Color(0xFFFBE6);
    private static final int POSTER_WIDTH = 300;
    private static final int POSTER_HEIGHT = 420;
    private static final int REVIEWS_HEIGHT = 120;
    private static final int MAX_REVIEWS = 2;

    public MovieDetailsView(MovieDetails movie) {
        super(movie.filmName());
        initializeFrame();
        buildUI(movie);
        finalizeFrame();
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(16, 16));
        getContentPane().setBackground(BACKGROUND_COLOR);
        getRootPane().setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    private void buildUI(MovieDetails movie) {
        add(createHeaderPanel(movie), BorderLayout.NORTH);
        add(createCenterPanel(movie), BorderLayout.CENTER);
        add(createBottomPanel(movie), BorderLayout.SOUTH);
    }

    private void finalizeFrame() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHeaderPanel(MovieDetails movie) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        header.setOpaque(false);

        JLabel title = bold(new JLabel(movie.filmName()));
        JLabel date = new JLabel(" –  %s  –  ".formatted(movie.releaseDate()));
        JLabel director = new JLabel("Directed by: %s".formatted(movie.director()));

        header.add(title);
        header.add(date);
        header.add(director);

        return header;
    }

    private JPanel createCenterPanel(MovieDetails movie) {
        JPanel center = new JPanel(new BorderLayout(16, 0));
        center.setOpaque(false);

        center.add(createPosterLabel(movie), BorderLayout.WEST);
        center.add(createFactsPanel(movie), BorderLayout.CENTER);

        return center;
    }

    private JLabel createPosterLabel(MovieDetails movie) {
        JLabel poster = new JLabel("Loading...", SwingConstants.CENTER);
        poster.setPreferredSize(new Dimension(POSTER_WIDTH, POSTER_HEIGHT));
        poster.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        loadPosterImage(poster, movie);

        return poster;
    }

    private Box createFactsPanel(MovieDetails movie) {
        Box facts = Box.createVerticalBox();
        facts.setOpaque(false);

        facts.add(new JLabel("Rating: %s".formatted(movie.ratingOutOf5())));
        facts.add(Box.createVerticalStrut(6));
        facts.add(new JLabel("Genres: %s".formatted(String.join(", ", movie.genres()))));
        facts.add(Box.createVerticalStrut(12));
        facts.add(createWatchlistButton());

        return facts;
    }

    private JButton createWatchlistButton() {
        JButton watchlistBtn = new JButton("Add To Watchlist");
        watchlistBtn.addActionListener(e -> watchlistBtn.setText("Already Watchlisted"));
        return watchlistBtn;
    }

    private JScrollPane createBottomPanel(MovieDetails movie) {
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        bottom.add(createDescriptionArea(movie));
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(bold(new JLabel("Popular Reviews:")));
        bottom.add(createReviewsScrollPane(movie));

        JScrollPane scroll = new JScrollPane(bottom,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);

        return scroll;
    }

    private JTextArea createDescriptionArea(MovieDetails movie) {
        JTextArea desc = new JTextArea("Description: " + movie.description());
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setBorder(new EmptyBorder(8, 8, 8, 8));
        return desc;
    }

    private JScrollPane createReviewsScrollPane(MovieDetails movie) {
        String reviewsText = formatReviews(movie.reviews(), MAX_REVIEWS);
        JTextArea reviews = new JTextArea(reviewsText);
        reviews.setLineWrap(true);
        reviews.setWrapStyleWord(true);
        reviews.setEditable(false);
        reviews.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane reviewsScroll = new JScrollPane(reviews,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        reviewsScroll.setPreferredSize(new Dimension(0, REVIEWS_HEIGHT));
        reviewsScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        return reviewsScroll;
    }

    private static JLabel bold(JLabel l) {
        l.setFont(l.getFont().deriveFont(Font.BOLD, l.getFont().getSize2D() + 1f));
        return l;
    }

    private static String formatReviews(final List<MovieReview> reviews, int maxReviews) {
        if (reviews == null || reviews.isEmpty()) {
            return "No reviews available.";
        }

        return reviews
                .stream()
                .limit(maxReviews)
                .map(review -> "%s - \"%s\"".formatted(review.author(), review.content()))
                .collect(Collectors.joining("\n\n"));
    }

    private void loadPosterImage(JLabel posterLabel, MovieDetails movie) {
        new Thread(() -> {
            try {
                String posterPath = movie.posterUrl();
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
                e.printStackTrace();
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