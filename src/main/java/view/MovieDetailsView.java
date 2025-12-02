package view;

import interface_adapter.movie_details.MovieDetailsState;
import interface_adapter.movie_details.MovieDetailsViewModel;
import interface_adapter.watchlist.WatchlistController;
import use_case.movie_details.MovieDetailsOutputData.MovieReviewData;
import view.components.ClickableButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * View for displaying movie details.
 */
public class MovieDetailsView extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(255, 255, 224);
    private static final int GAP_SMALL = 6, GAP_MEDIUM = 12, GAP_LARGE = 16;
    private static final int POSTER_WIDTH = 300, POSTER_HEIGHT = 420;
    private static final int DESCRIPTION_WIDTH = 400, DESCRIPTION_HEIGHT = 120;
    private static final int REVIEWS_WIDTH = 400, REVIEWS_HEIGHT = 120;
    private static final int MAX_REVIEWS = 2;

    private static final Map<String, MovieDetailsState> STATE_CACHE = new ConcurrentHashMap<>();

    private final WatchlistController watchlistController;
    private Runnable onWatchlistChanged = () -> {};

    public MovieDetailsView(MovieDetailsViewModel viewModel, WatchlistController watchlistController) {
        this.watchlistController = watchlistController;
        initializePanel();

        viewModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) displayMovieDetails((MovieDetailsState) evt.getNewValue());
            else if ("errorMessage".equals(evt.getPropertyName())) displayError((String) evt.getNewValue());
        });
    }

    public void setOnWatchlistChanged(Runnable callback) {
        this.onWatchlistChanged = (callback == null) ? () -> {} : callback;
    }

    public static MovieDetailsState getCachedState(String posterUrl) {
        return (posterUrl == null) ? null : STATE_CACHE.get(posterUrl);
    }

    public void displayMovieDetails(MovieDetailsState state) {
        if (state != null && state.posterUrl() != null && !state.posterUrl().isBlank())
            STATE_CACHE.put(state.posterUrl(), state);

        removeAll();
        add(createHeader(state), BorderLayout.NORTH);
        add(createCenter(state), BorderLayout.CENTER);
        add(createBottom(state), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void initializePanel() {
        setLayout(new BorderLayout(GAP_LARGE, GAP_LARGE));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(GAP_LARGE, GAP_LARGE, GAP_LARGE, GAP_LARGE));
    }

    // -------------------- UI BUILDERS --------------------

    private JPanel createHeader(MovieDetailsState state) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP_MEDIUM, 0));
        header.setOpaque(false);
        header.add(bold(new JLabel(state.filmName())));
        header.add(new JLabel(" - %s - ".formatted(state.releaseDate())));
        header.add(new JLabel("Directed by: %s".formatted(state.director())));
        return header;
    }

    private JPanel createCenter(MovieDetailsState state) {
        JPanel center = new JPanel(new BorderLayout(GAP_LARGE, 0));
        center.setOpaque(false);
        center.add(createPoster(state), BorderLayout.WEST);
        center.add(createFacts(state), BorderLayout.CENTER);
        return center;
    }

    private JLabel createPoster(MovieDetailsState state) {
        JLabel poster = new JLabel("Loading...", SwingConstants.CENTER);
        poster.setPreferredSize(new Dimension(POSTER_WIDTH, POSTER_HEIGHT));
        poster.setBorder(BorderFactory.createEmptyBorder(GAP_SMALL, GAP_SMALL, GAP_SMALL, GAP_SMALL));

        new Thread(() -> loadPoster(poster, state.posterUrl())).start();
        return poster;
    }

    private Box createFacts(MovieDetailsState state) {
        Box facts = Box.createVerticalBox();
        facts.setOpaque(false);
        facts.add(new JLabel("Rating: %s".formatted(state.ratingOutOf5())));
        facts.add(Box.createVerticalStrut(GAP_SMALL));
        facts.add(new JLabel("Genres: %s".formatted(String.join(", ", state.genres()))));
        facts.add(Box.createVerticalStrut(GAP_MEDIUM));
        facts.add(createWatchlistButton(state));
        return facts;
    }

    private JButton createWatchlistButton(MovieDetailsState state) {
        final String posterUrl = state.posterUrl();
        final boolean[] inWatchlist = {watchlistController.isInWatchlist(posterUrl)};
        JButton btn = new ClickableButton();
        updateWatchlistText(btn, inWatchlist[0]);
        btn.addActionListener(e -> {
            if (inWatchlist[0]) watchlistController.removeFromWatchlist(posterUrl);
            else watchlistController.addToWatchlist(posterUrl);
            inWatchlist[0] = !inWatchlist[0];
            updateWatchlistText(btn, inWatchlist[0]);
            onWatchlistChanged.run();
        });
        return btn;
    }

    private void updateWatchlistText(JButton btn, boolean added) {
        btn.setText(added ? "Remove from Watchlist" : "Add to Watchlist");
    }

    private Box createBottom(MovieDetailsState state) {
        Box bottom = Box.createVerticalBox();
        bottom.setOpaque(false);
        bottom.add(createScrollPane(state.description(), "Description", DESCRIPTION_WIDTH, DESCRIPTION_HEIGHT));
        bottom.add(Box.createVerticalStrut(GAP_MEDIUM));
        bottom.add(createScrollPane(formatReviews(state.reviews(), MAX_REVIEWS), "Popular Reviews", REVIEWS_WIDTH, REVIEWS_HEIGHT));
        return bottom;
    }

    private JScrollPane createScrollPane(String text, String title, int width, int height) {
        JComponent comp;
        if ("Description".equals(title)) {
            JTextArea area = new JTextArea(Objects.requireNonNullElse(text, ""));
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setEditable(false);
            comp = area;
        } else {
            JEditorPane editor = new JEditorPane("text/html", "<html>%s</html>".formatted(text));
            editor.setEditable(false);
            editor.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
            editor.setFont(new JLabel().getFont());
            comp = editor;
        }
        JScrollPane scroll = new JScrollPane(comp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createTitledBorder(title));
        scroll.setPreferredSize(new Dimension(width, height));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return scroll;
    }

    private JLabel bold(JLabel label) {
        label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize2D()));
        return label;
    }

    private String formatReviews(List<MovieReviewData> reviews, int max) {
        if (reviews == null || reviews.isEmpty()) return "No reviews available.";
        return reviews.stream().limit(max).map(r -> "<b>%s</b>: <blockquote>%s</blockquote>".formatted(
                r.author(), r.content().replace("\n", "<br>")
        )).collect(Collectors.joining("<br><br>"));
    }

    // -------------------- POSTER IMAGE --------------------

    private void loadPoster(JLabel label, String url) {
        try {
            if (url == null || url.isBlank()) {
                setLabelText(label, "No poster available");
                return;
            }
            BufferedImage img = ImageIO.read(new URL(url));
            if (img != null) setLabelIcon(label, img.getScaledInstance(POSTER_WIDTH, POSTER_HEIGHT, Image.SCALE_SMOOTH));
            else setLabelText(label, "Image not available");
        } catch (IOException e) {
            setLabelText(label, "Failed to load image");
        }
    }

    private void setLabelIcon(JLabel label, Image img) {
        SwingUtilities.invokeLater(() -> {
            label.setIcon(new ImageIcon(img));
            label.setText(null);
        });
    }

    private void setLabelText(JLabel label, String text) {
        SwingUtilities.invokeLater(() -> {
            label.setIcon(null);
            label.setText(text);
        });
    }
}
