package interface_adapter.watchlist;

import use_case.watchlist.WatchlistInputBoundary;
import use_case.watchlist.WatchlistInputData;

/**
 * Controller for the Watchlist use case.
 *
 * Adapts UI events into WatchlistInputBoundary calls.
 */
public class WatchlistController {

    private final WatchlistInputBoundary interactor;
    private final WatchlistViewModel viewModel;

    public WatchlistController(WatchlistInputBoundary interactor,
                               WatchlistViewModel viewModel) {
        this.interactor = interactor;
        this.viewModel = viewModel;
    }

    public void addToWatchlist(String posterUrl) {
        interactor.addMovie(new WatchlistInputData(posterUrl));
    }

    public void removeFromWatchlist(String posterUrl) {
        interactor.removeMovie(new WatchlistInputData(posterUrl));
    }

    /**
     * Triggers a reload of the current user's watchlist.
     * UI should react to the ViewModel's state change.
     */
    public void loadWatchlist() {
        interactor.loadWatchlist();
    }

    /**
     * Synchronous helper used by MovieDetailsView to set the initial
     * button text based on the current ViewModel state.
     */
    public boolean isInWatchlist(String posterUrl) {
        return viewModel.isInWatchlist(posterUrl);
    }
}
