package use_case.movie_filter;

import entity.Movie;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory fake DAO for FilterMoviesInteractor tests.
 * Captures last call parameters so tests can verify interactor → DAO wiring.
 */
public class InMemoryFilterMoviesTestDAO implements FilterMoviesDataAccessInterface {

    // what the DAO should return
    public List<Movie> moviesToReturn = new ArrayList<>();
    public List<String> postersToReturn = new ArrayList<>();
    public Map<String, Integer> genresToReturn = new LinkedHashMap<>();
    public int lastTotalPages = 1;

    // diagnostics: what was called
    public int getMovieGenresCalls = 0;

    public String lastYear;
    public String lastRating;
    public String lastGenreId;
    public String lastSearch;
    public int lastPage;

    @Override
    public List<Movie> getFilteredMovies(String year, String rating, String genreId, String search, int page) {
        this.lastYear = year;
        this.lastRating = rating;
        this.lastGenreId = genreId;
        this.lastSearch = search;
        this.lastPage = page;
        return moviesToReturn;
    }

    @Override
    public List<String> getPosterUrls(List<Movie> movies) {
        return postersToReturn;
    }

    @Override
    public Map<String, Integer> getMovieGenres() {
        getMovieGenresCalls++;
        return genresToReturn;
    }

    @Override
    public int getLastTotalPages() {
        return lastTotalPages;
    }
}
