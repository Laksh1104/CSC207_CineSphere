package use_case.movie_details;

import entity.MovieDetails;

public interface MovieDetailsAccessInterface {
    MovieDetails getMovieDetails(int filmId);
}
