package use_case.book_movie;

import entity.Seat;

import java.util.List;
import java.util.Set;

/**
 * Input Boundary for the Book Movie use case.
 *
 * <p>This interface defines the actions that the Booking View (Controller)
 * may request from the Book Movie Interactor. It follows the Clean Architecture
 * principle of isolating the use case from external frameworks, UI, and data sources.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Triggering the booking workflow</li>
 *   <li>Retrieving the seat layout for a specific show</li>
 *   <li>Retrieving the set of seats already booked</li>
 * </ul>
 */
public interface BookMovieInputBoundary {

    /**
     * Executes the Book Movie use case using the provided user input.
     *
     * <p>This method triggers the full booking workflow:
     * <ul>
     *   <li>validates the input data</li>
     *   <li>checks seat availability</li>
     *   <li>calculates the total cost</li>
     *   <li>creates a {@code MovieTicket}</li>
     *   <li>persists the booking</li>
     *   <li>passes the result to the presenter</li>
     * </ul>
     *
     * @param inputData the structured booking information submitted by the user
     */
    void execute(BookMovieInputData inputData);

    /**
     * Loads the complete seat layout (booked + available) for a specific show.
     *
     * <p>This method is used by the UI when the user selects a movie, cinema,
     * date, and showtime, allowing the view to render the seating grid.
     *
     * @param movieName the movie title
     * @param cinemaName the cinema name
     * @param date the date of the showing (yyyy-MM-dd)
     * @param startTime the start time of the show (HH:mm)
     * @param endTime the end time of the show (HH:mm)
     * @return a list of {@link Seat} objects representing the entire seat matrix
     */
    List<Seat> loadSeatLayout(
            String movieName,
            String cinemaName,
            String date,
            String startTime,
            String endTime
    );

    /**
     * Returns the set of seat identifiers already booked for the given show.
     *
     * <p>This method allows the UI to disable these seats in the seat
     * selection panel before the user makes their selection.
     *
     * @param movieName the movie title
     * @param cinemaName the cinema name
     * @param date the date of the showing
     * @param startTime the start time of the show
     * @param endTime the end time of the show
     * @return a set of seat IDs (e.g., "A1", "B5") that are already booked
     */
    Set<String> getBookedSeats(
            String movieName,
            String cinemaName,
            String date,
            String startTime,
            String endTime
    );
}
