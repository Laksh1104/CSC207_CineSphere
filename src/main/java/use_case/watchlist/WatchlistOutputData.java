package use_case.watchlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Output DTO for the Watchlist use case.
 *
 * Contains the complete list of poster URLs after an operation
 * (add, remove, or load).
 */
public class WatchlistOutputData {

    private final List<String> posterUrls;

    public WatchlistOutputData(List<String> posterUrls) {
        this.posterUrls = (posterUrls == null)
                ? new ArrayList<>()
                : new ArrayList<>(posterUrls);
    }

    public List<String> getPosterUrls() {
        return new ArrayList<>(posterUrls);
    }
}
