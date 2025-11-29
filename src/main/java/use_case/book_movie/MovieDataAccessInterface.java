package use_case.book_movie;

import entity.Movie;
import java.util.List;

/**
 * Data access interface for retrieving movies that are currently playing in the cinema.
 *
 * <p>This interface provides the Book Movie use case (and UI loaders)
 * with the list of films that are "now showing" at cinemas.
 */
public interface MovieDataAccessInterface {

    /**
     * Retrieves the list of movies currently available for booking.
     *
     * @return a list of {@link Movie} entities; never {@code null}.
     *         An empty list indicates that no movies are currently available.
     */
    List<Movie> getNowShowingMovies();
}
