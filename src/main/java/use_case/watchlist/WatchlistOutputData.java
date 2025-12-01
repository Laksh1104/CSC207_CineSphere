package use_case.watchlist;

import java.util.List;

public class WatchlistOutputData {
    private final List<String> movies;

    public WatchlistOutputData(List<String> movieUrls) {
        this.movies = movieUrls;
    }

    public List<String> getMovieUrls() {
        return movies;
    }
}