package use_case.watchlist;

/**
 * Output boundary for the Watchlist use case.
 *
 * Implemented by a presenter in the interface_adapter layer.
 */
public interface WatchlistOutputBoundary {

    void present(WatchlistOutputData data);

    void presentError(String errorMessage);
}
