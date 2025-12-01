package use_case.watchlist;

public class WatchlistInputData {
    private final String movieUrl;
    public WatchlistInputData(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public String getMovieUrl() {
        return movieUrl;
    }
}
