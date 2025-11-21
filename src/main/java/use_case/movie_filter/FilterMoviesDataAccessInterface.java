package use_case.movie_filter;

import entity.Movie;
import java.util.List;

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
}
