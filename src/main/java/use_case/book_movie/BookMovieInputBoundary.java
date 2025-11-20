package use_case.book_movie;

import entity.Cinema;
import entity.Movie;
import entity.ShowTime;

import java.util.Set;

/**
 * Input Boundary for actions related to booking a movie.
 */
public interface BookMovieInputBoundary {
    /**
     * Executes the Book Ticket use case.
     * @param inputData the input data for booking a ticket
     */
    void execute(BookMovieInputData inputData);
    Set<String> getBookedSeats(Movie m, Cinema c, String date, ShowTime st);
}
