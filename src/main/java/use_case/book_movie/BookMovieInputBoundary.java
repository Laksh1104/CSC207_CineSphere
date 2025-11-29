package use_case.book_movie;

import entity.Cinema;
import entity.Movie;
import entity.Seat;
import entity.ShowTime;

import java.util.List;
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
    List<Seat> loadSeatLayout(Movie m, Cinema c, String date, ShowTime st);
    Set<String> getBookedSeats(Movie m, Cinema c, String date, ShowTime st);
}
