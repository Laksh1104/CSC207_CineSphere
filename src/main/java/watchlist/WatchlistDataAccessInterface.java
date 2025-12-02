package use_case.watchlist;

import java.util.List;

/**
 * Data access interface for persisting and loading a user's watchlist.
 *
 * Implemented by an outer-layer gateway (e.g. UserProfileJsonDataAccessObject).
 */
public interface WatchlistDataAccessInterface {

    boolean isInWatchlist(String username, String posterUrl);

    void addToWatchlist(String username, String posterUrl);

    void removeFromWatchlist(String username, String posterUrl);

    /**
     * Returns all poster URLs for the given user.
     *
     * @param username current logged-in username
     * @return list of poster URLs (never null)
     */
    List<String> getWatchlist(String username);
}
