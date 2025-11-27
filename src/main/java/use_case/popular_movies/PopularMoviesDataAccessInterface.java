package use_case.popular_movies;

import entity.Movie;

import java.util.List;

public interface PopularMoviesDataAccessInterface {
    List<Movie> getPopularMovies();
    List<String> getPosterUrls(List<Movie> movies);
}
