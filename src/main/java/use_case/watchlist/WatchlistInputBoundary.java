package use_case.watchlist;

import java.util.List;

public interface WatchlistInputBoundary {
    void addMovie(WatchlistInputData movieURL);
    void removeMovie(WatchlistInputData movieURL);
    List<String> loadPage();
    boolean isInWatchlist(String movieURL);

    void forward();

    void back();
}
