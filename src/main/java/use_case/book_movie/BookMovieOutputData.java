package use_case.book_movie;

import java.util.HashSet;
import java.util.Set;

public class BookMovieOutputData {

    private final String movieName;
    private final String cinemaName;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final Set<String> seats;
    private final int totalCost;

    public BookMovieOutputData(String movieName, String cinemaName, String date, String startTime, String endTime, Set<String> seats, int totalCost) {

        this.movieName = movieName;
        this.cinemaName = cinemaName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.seats = new HashSet<>(seats);
        this.totalCost = totalCost;
    }

    public String getMovieName()     { return movieName; }
    public String getCinemaName()    { return cinemaName; }
    public String getDate()     { return date; }
    public String getStartTime(){ return startTime; }
    public String getEndTime()  { return endTime; }
    public Set<String> getSeats(){ return seats; }
    public int getTotalCost()   { return totalCost; }

    public String getSeatNumbers() {
        return String.join(" ", seats);
    }
}
