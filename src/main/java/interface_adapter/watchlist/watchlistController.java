package interface_adapter.watchlist;

import use_case.watchlist.WatchlistInputBoundary;
import use_case.watchlist.WatchlistInputData;
import use_case.watchlist.WatchlistInteractor;

import java.util.List;

public class watchlistController {

    private static WatchlistInputBoundary interactor = new WatchlistInputBoundary() {
        @Override
        public void addMovie(WatchlistInputData data) {

        }

        @Override
        public void removeMovie(WatchlistInputData data) {
        }

        @Override
        public List<String> loadPage() {
            return List.of();
        }

        @Override
        public boolean isInWatchlist(String posterURL) {
            return false;
        }
    };

    public static void removeFromWatchlist(String movieUrl){
        interactor.removeMovie(new WatchlistInputData(movieUrl));
    }

    public static void addToWatchlist(String movieUrl) {
        interactor.addMovie(new WatchlistInputData(movieUrl));
    }

    public static boolean isInWatchlist(String posterURL) {
        return interactor.isInWatchlist(posterURL);
    }

    public List<String> loadPage() {
        if (interactor instanceof WatchlistInteractor inter) {
            return inter.loadPage();
        }
        return List.of();
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

