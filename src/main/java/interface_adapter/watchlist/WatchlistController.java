package interface_adapter.watchlist;

import use_case.login.LoginUserDataAccessInterface;
import use_case.watchlist.WatchlistDataAccessInterface;

import java.util.List;

/**
 * Coordinates watchlist operations for the current logged-in user.
 */
public class WatchlistController {

    private final WatchlistDataAccessInterface watchlistDAO;
    private final LoginUserDataAccessInterface userDAO;

    public WatchlistController(WatchlistDataAccessInterface watchlistDAO,
                               LoginUserDataAccessInterface userDAO) {
        this.watchlistDAO = watchlistDAO;
        this.userDAO = userDAO;
    }

    // ----------- Helpers -----------

    /** Returns username if valid, otherwise null */
    private String validUsername() {
        String username = userDAO.getCurrentUsername();
        return (username == null || username.isBlank()) ? null : username;
    }

    /** Checks if username or poster URL is invalid */
    private boolean invalidInput(String posterUrl) {
        return validUsername() == null ||
                posterUrl == null || posterUrl.isBlank();
    }

    // ----------- Controller Actions -----------

    public boolean isInWatchlist(String posterUrl) {
        if (invalidInput(posterUrl)) return false;
        return watchlistDAO.isInWatchlist(validUsername(), posterUrl);
    }

    public void addToWatchlist(String posterUrl) {
        if (invalidInput(posterUrl)) return;
        watchlistDAO.addToWatchlist(validUsername(), posterUrl);
    }

    public void removeFromWatchlist(String posterUrl) {
        if (invalidInput(posterUrl)) return;
        watchlistDAO.removeFromWatchlist(validUsername(), posterUrl);
    }

    public List<String> getWatchlistForCurrentUser() {
        String username = validUsername();
        return (username == null) ? List.of() : watchlistDAO.getWatchlist(username);
    }
}
