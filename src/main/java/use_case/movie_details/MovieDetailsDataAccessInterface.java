package use_case.movie_details;

import entity.MovieDetails;

/**
 * Data access interface for retrieving movie details from the data source.
 * Provides methods to access movie information from the underlying data layer.
 */
public interface MovieDetailsDataAccessInterface {

    /**
     * Retrieves detailed information about a specific movie.
     * 
     * @param filmId the ID of the movie to retrieve
     * @return a MovieDetails object containing comprehensive information about the movie,
     *         or null if no movie with the specified ID is found
     */
    MovieDetails getMovieDetails(int filmId);
}
