package interface_adapter.BookMovie;

import java.util.HashSet;
import java.util.Set;

/**
 * The state for the Book Movie View Model.
 */
public class BookMovieState {

    private  String movieName;
    private  String cinemaName;
    private String date;
    private String startTime;
    private String endTime;
    private Set<String> seats = new HashSet<>();
    private Integer totalCost;
    private String bookingError;
    private String bookingSuccessMessage;

    public String movieName()            { return movieName; }
    public String cinemaName()           { return cinemaName; }
    public String getDate()                { return date; }
    public String getStartTime()           { return startTime; }
    public String getEndTime()             { return endTime; }
    public Set<String> getSeats()          { return seats; }
    public Integer getTotalCost()          { return totalCost; }
    public String getBookingError()        { return bookingError; }
    public String getBookingSuccessMessage(){ return bookingSuccessMessage; }

    public void setMovieName(String movieName)            { this.movieName = movieName; }
    public void setCinemaName(String cinemaName)          { this.cinemaName = cinemaName; }
    public void setDate(String date)                   { this.date = date; }
    public void setStartTime(String startTime)         { this.startTime = startTime; }
    public void setEndTime(String endTime)             { this.endTime = endTime; }
    public void setSeats(Set<String> seats)            { this.seats = seats; }
    public void setTotalCost(Integer totalCost)        { this.totalCost = totalCost; }
    public void setBookingError(String bookingError)   { this.bookingError = bookingError; }
    public void setBookingSuccessMessage(String msg)   { this.bookingSuccessMessage = msg; }
}
