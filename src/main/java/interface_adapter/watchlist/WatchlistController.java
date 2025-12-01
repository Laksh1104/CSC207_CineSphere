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

    public boolean isInWatchlist(String posterUrl) {
        String username = userDAO.getCurrentUsername();
        if (username == null || username.isBlank()
                || posterUrl == null || posterUrl.isBlank()) {
            return false;
        }
        return watchlistDAO.isInWatchlist(username, posterUrl);
    }

    public void addToWatchlist(String posterUrl) {
        String username = userDAO.getCurrentUsername();
        if (username == null || username.isBlank()
                || posterUrl == null || posterUrl.isBlank()) {
            return;
        }
        watchlistDAO.addToWatchlist(username, posterUrl);
    }

    public void removeFromWatchlist(String posterUrl) {
        String username = userDAO.getCurrentUsername();
        if (username == null || username.isBlank()
                || posterUrl == null || posterUrl.isBlank()) {
            return;
        }
        watchlistDAO.removeFromWatchlist(username, posterUrl);
    }

    public List<String> getWatchlistForCurrentUser() {
        String username = userDAO.getCurrentUsername();
        if (username == null || username.isBlank()) {
            return List.of();
        }
        return watchlistDAO.getWatchlist(username);
    }
}
