package interface_adapter.watchlist;

import use_case.watchlist.WatchlistInputBoundary;
import use_case.watchlist.WatchlistInputData;

import java.util.List;

public class watchlistController {

    private final WatchlistInputBoundary watchlistInputBoundary;
    public watchlistController(WatchlistInputBoundary watchlistInputBoundary){
        this.watchlistInputBoundary = watchlistInputBoundary;
    }

    public void removeFromWatchlist(String movieUrl){
        watchlistInputBoundary.removeMovie(new WatchlistInputData(movieUrl));
    }

    public void addToWatchlist(String movieUrl) {
        watchlistInputBoundary.addMovie(new WatchlistInputData(movieUrl));
    }

    public boolean isInWatchlist(String movieUrl) {
        return watchlistInputBoundary.isInWatchlist(new WatchlistInputData(movieUrl).getMovieUrl());
    }

    public List<String> loadPage() {
            return watchlistInputBoundary.loadPage();
    }


    public void next() {
        watchlistInputBoundary.forward();
    }

    public void previous() {
            watchlistInputBoundary.back();
}}

