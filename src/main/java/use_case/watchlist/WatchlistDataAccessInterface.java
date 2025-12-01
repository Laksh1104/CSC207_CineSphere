package use_case.watchlist;

import java.util.List;

public interface WatchlistDataAccessInterface {

    boolean isInWatchlist(String username, String posterUrl);

    void addToWatchlist(String username, String posterUrl);

    void removeFromWatchlist(String username, String posterUrl);

    List<String> getWatchlist(String username);
}
