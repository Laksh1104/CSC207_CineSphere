package use_case.book_movie;

import entity.MovieTicket;
import entity.Seat;

import java.util.List;
import java.util.Set;

/**
 * Data access interface for managing ticket bookings and seat availability.
 *
 * <p>All methods are side-effect free except {@link #saveBooking(MovieTicket)},
 * which should persist the booking and update seat availability.
 */
public interface BookTicketDataAccessInterface {

    /**
     * Retrieves the set of seat names that are already booked for the specified show.
     *
     * @param movieName   the name of the movie
     * @param cinemaName  the name of the cinema
     * @param date        the date of the screening (format: yyyy-MM-dd)
     * @param startTime   the show start time (e.g., "18:00")
     * @param endTime     the show end time (e.g., "20:30")
     * @return a set of seat identifiers (e.g., "A1", "C5") that are already booked;
     *         never {@code null}. If no seats are booked, returns an empty set.
     */
    Set<String> getBookedSeats(
            String movieName,
            String cinemaName,
            String date,
            String startTime,
            String endTime
    );

    /**
     * Retrieves the full seat layout for the specified show.
     *
     * <p>The returned list contains all seats for the cinema room, typically
     * including their availability status (booked or unbooked).
     *
     * @param movieName   the name of the movie
     * @param cinemaName  the name of the cinema
     * @param date        the date of the screening
     * @param startTime   the show start time
     * @param endTime     the show end time
     * @return a list of {@link Seat} objects representing the seating layout;
     *         never {@code null}. Implementations should always return the
     *         same layout for the same show combination.
     */
    List<Seat> getSeatLayout(
            String movieName,
            String cinemaName,
            String date,
            String startTime,
            String endTime
    );

    /**
     * Saves a confirmed booking by marking the selected seats as booked.
     *
     * <p>The implementation must update its storage so that subsequent calls to
     * {@link #getBookedSeats(String, String, String, String, String)} and
     * {@link #getSeatLayout(String, String, String, String, String)}
     * reflect the newly booked seats.
     *
     * @param movieTicket the ticket object representing the booking details,
     *                    including movie, cinema, date, time, and selected seats;
     *                    must not be {@code null}.
     */
    void saveBooking(MovieTicket movieTicket);
}
