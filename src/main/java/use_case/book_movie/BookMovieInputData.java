package use_case.book_movie;

import java.util.HashSet;
import java.util.Set;

/**
 * Input data for the Book Movie use case.
 *
 * <p>This class is a simple immutable structure holding the user's booking selection.
 * It contains movie, cinema, date, time, and selected seats.
 *
 * <p>The time range string ("HH:mm - HH:mm") is parsed into start and end times.
 */

public class BookMovieInputData {

    private final String movieName;
    private final String cinemaName;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final Set<String> seats;

    /**
     * Constructs a new input data object.
     *
     * @param movieName the movie title
     * @param cinemaName the cinema name
     * @param date the selected date (yyyy-MM-dd)
     * @param timeRange the full time range, formatted "HH:mm - HH:mm"
     * @param seats the selected seat identifiers
     */
    public BookMovieInputData(String movieName, String cinemaName, String date, String timeRange, Set<String> seats) {
        this.movieName = movieName;
        this.cinemaName = cinemaName;
        this.date = date;
        this.seats = new HashSet<>(seats);

        // Parse "HH:mm - HH:mm"
        String[] split = timeRange.split(" - ");
        this.startTime = split[0];
        this.endTime = split[1];
    }

    public String getMovieName()     { return movieName; }
    public String getCinemaName()    { return cinemaName; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public Set<String> getSeats() { return seats; }
}
