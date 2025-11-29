package use_case.book_movie;

import entity.Seat;

import java.util.List;
import java.util.Set;

/**
 * Input Boundary for actions related to booking a movie.
 */
public interface BookMovieInputBoundary {

    /**
     * Executes the Book Ticket use case.
     */
    void execute(BookMovieInputData inputData);

    /**
     * Load seat layout for a given show.
     */
    List<Seat> loadSeatLayout(String movieName, String cinemaName, String date, String startTime, String endTime);

    /**
     * Get already booked seats for a given show.
     */
    Set<String> getBookedSeats(String movieName, String cinemaName, String date, String startTime, String endTime);
}
