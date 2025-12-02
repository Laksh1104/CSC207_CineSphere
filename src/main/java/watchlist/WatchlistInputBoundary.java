package use_case.watchlist;

/**
 * Input boundary for the Watchlist use case.
 *
 * The controller talks only to this interface.
 */
public interface WatchlistInputBoundary {

    /**
     * Adds a movie (poster URL) to the current user's watchlist.
     */
    void addMovie(WatchlistInputData data);

    /**
     * Removes a movie (poster URL) from the current user's watchlist.
     */
    void removeMovie(WatchlistInputData data);

    /**
     * Loads the full watchlist for the current user.
     */
    void loadWatchlist();
}
