package use_case.book_movie;

import entity.Cinema;
import entity.Movie;
import entity.ShowTime;

import java.util.HashSet;
import java.util.Set;

public class BookMovieInputData {

    private final String movieName;
    private final String cinemaName;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final Set<String> seats;

    public BookMovieInputData(String movieName, String cinemaName, String date, String timeRange, Set<String> seats) {
        this.movieName = movieName;
        this.cinemaName = cinemaName;
        this.date = date;
        this.seats = seats;

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
