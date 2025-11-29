package entity;

import java.util.ArrayList;
import java.util.List;

public class Watchlist {
    private final List<String> movies = new ArrayList<>();

    public void add(String movieUrl) {
        movies.add(movieUrl);
    }

    public List<String> getMovies() {
        return movies;
    }
}