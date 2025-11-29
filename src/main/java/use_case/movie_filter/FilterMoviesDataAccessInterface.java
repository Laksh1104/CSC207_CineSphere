package use_case.movie_filter;

import entity.Movie;

import java.util.List;
import java.util.Map;

public interface FilterMoviesDataAccessInterface {

    /** Returns filtered movies as Movie entities */
    List<Movie> getFilteredMovies(
            String year,
            String rating,
            String genreId,
            String search,
            int page
    );

    /** Builds poster URLs for Movie list */
    List<String> getPosterUrls(List<Movie> movies);

    /** Returns all TMDB movie genres as a map: name -> id */
    Map<String, Integer> getMovieGenres();

    /** Total pages from the most recent TMDB request */
    int getLastTotalPages();
}
