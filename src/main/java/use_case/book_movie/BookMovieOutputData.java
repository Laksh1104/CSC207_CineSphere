package use_case.book_movie;

import java.util.HashSet;
import java.util.Set;

/**
 * Output data for the Book Movie use case.
 *
 * <p>This class is a simple immutable data structure that contains
 * the final details of a successful booking, including movie,
 * cinema, date, seats, and total cost.
 *
 * <p>It is passed from the interactor to the presenter, which updates
 * the view model for display in the user interface.
 */
public class BookMovieOutputData {

    private final String movieName;
    private final String cinemaName;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final Set<String> seats;
    private final int totalCost;

    /**
     * Constructs a new output data object for a successful booking.
     *
     * @param movieName  the booked movie name
     * @param cinemaName the cinema where the movie is showing
     * @param date       the date of the showing (yyyy-MM-dd)
     * @param startTime  the start time of the showing
     * @param endTime    the end time of the showing
     * @param seats      the selected seat identifiers (e.g., "A5", "C10")
     * @param totalCost  the total computed price of the booking
     */
    public BookMovieOutputData(
            String movieName,
            String cinemaName,
            String date,
            String startTime,
            String endTime,
            Set<String> seats,
            int totalCost
    ) {
        this.movieName = movieName;
        this.cinemaName = cinemaName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.seats = new HashSet<>(seats);
        this.totalCost = totalCost;
    }

    public String getMovieName()   { return movieName; }
    public String getCinemaName()  { return cinemaName; }
    public String getDate()        { return date; }
    public String getStartTime()   { return startTime; }
    public String getEndTime()     { return endTime; }
    public Set<String> getSeats()  { return seats; }
    public int getTotalCost()      { return totalCost; }

    /**
     * Returns all selected seats concatenated in a display-friendly format.
     *
     * @return seat numbers joined by spaces, e.g. "A1 A2 A3"
     */
    public String getSeatNumbers() {
        return String.join(" ", seats);
    }
}
