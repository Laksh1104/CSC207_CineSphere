package use_case.watchlist;

import java.util.List;

public interface WatchlistInputBoundary {
    void addMovie(WatchlistInputData data);
    List<String> loadPage();
}
