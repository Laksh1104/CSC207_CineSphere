package use_case.book_movie;

import entity.Cinema;
import java.util.List;

/**
 * Data access interface for retrieving cinema information related to movie showings.
 *
 * <p>This interface allows the Book Movie use case (and related UI queries)
 * to obtain the list of cinemas where a specific film is playing on a given date.
 */
public interface CinemaDataAccessInterface {

    /**
     * Returns all cinemas showing the specified film on the given date.
     *
     * @param filmId the TMDB or MovieGlu film identifier
     * @param date   the date of the showing (format: yyyy-MM-dd)
     * @return a list of {@link Cinema} objects; never {@code null}.
     *         An empty list indicates that the film is not playing on that date.
     */
    List<Cinema> getCinemasForFilm(int filmId, String date);
}
