package interface_adapter.watchlist;

import use_case.watchlist.WatchlistInputBoundary;
import use_case.watchlist.WatchlistInputData;
import use_case.watchlist.WatchlistInteractor;

public class watchlistController {

    private final WatchlistInputBoundary interactor;

    public watchlistController(WatchlistInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void addToWatchlist(String movieUrl) {
        interactor.addMovie(new WatchlistInputData(movieUrl));
    }

    public void loadPage() {
        interactor.loadPage();
    }

    public void next() {
        if (interactor instanceof WatchlistInteractor inter)
            inter.forward();
    }

    public void previous() {
        if (interactor instanceof WatchlistInteractor inter)
            inter.back();
    }
}

