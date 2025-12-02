package use_case.watchlist;

import use_case.login.LoginUserDataAccessInterface;

import java.util.List;

/**
 * Interactor for the Watchlist use case.
 *
 * Application/business rules:
 * - Requires a logged-in user
 * - Delegates persistence to WatchlistDataAccessInterface
 * - Sends results through WatchlistOutputBoundary
 */
public class WatchlistInteractor implements WatchlistInputBoundary {

    private final WatchlistDataAccessInterface watchlistGateway;
    private final LoginUserDataAccessInterface loginGateway;
    private final WatchlistOutputBoundary presenter;

    public WatchlistInteractor(WatchlistDataAccessInterface watchlistGateway,
                               LoginUserDataAccessInterface loginGateway,
                               WatchlistOutputBoundary presenter) {
        this.watchlistGateway = watchlistGateway;
        this.loginGateway = loginGateway;
        this.presenter = presenter;
    }

    @Override
    public void addMovie(WatchlistInputData data) {
        String username = currentUserOrFail();
        if (username == null) return;

        String posterUrl = data.getPosterUrl();
        if (isNullOrBlank(posterUrl)) {
            presenter.presentError("Poster URL is required.");
            return;
        }

        if (!watchlistGateway.isInWatchlist(username, posterUrl)) {
            watchlistGateway.addToWatchlist(username, posterUrl);
        }

        List<String> urls = watchlistGateway.getWatchlist(username);
        presenter.present(new WatchlistOutputData(urls));
    }

    @Override
    public void removeMovie(WatchlistInputData data) {
        String username = currentUserOrFail();
        if (username == null) return;

        String posterUrl = data.getPosterUrl();
        if (isNullOrBlank(posterUrl)) {
            presenter.presentError("Poster URL is required.");
            return;
        }

        if (watchlistGateway.isInWatchlist(username, posterUrl)) {
            watchlistGateway.removeFromWatchlist(username, posterUrl);
        }

        List<String> urls = watchlistGateway.getWatchlist(username);
        presenter.present(new WatchlistOutputData(urls));
    }

    @Override
    public void loadWatchlist() {
        String username = currentUserOrFail();
        if (username == null) return;

        List<String> urls = watchlistGateway.getWatchlist(username);
        presenter.present(new WatchlistOutputData(urls));
    }

    private String currentUserOrFail() {
        String username = loginGateway.getCurrentUsername();
        if (isNullOrBlank(username)) {
            presenter.presentError("No user is currently logged in.");
            return null;
        }
        return username;
    }

    private boolean isNullOrBlank(String s) {
        return s == null || s.isBlank();
    }
}
