package interface_adapter.watchlist;

import java.util.ArrayList;
import java.util.List;

public class watchlistViewModel {
    private List<String> movies;
    public watchlistViewModel() {
        this.movies = new ArrayList<>();
    }

    public List<String> getMovies() {
        return movies;
    }

    public void setMovies(List<String> movies) {
        this.movies = movies;
    }
}
