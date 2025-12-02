package use_case.watchlist;

/**
 * Simple input DTO for watchlist operations that target a single poster URL.
 */
public class WatchlistInputData {

    private final String posterUrl;

    public WatchlistInputData(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
}
