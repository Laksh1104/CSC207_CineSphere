package interface_adapter.BookMovie;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import entity.Movie;
import entity.Cinema;
import entity.ShowTime;

/**
 * The state for the Book Movie View Model.
 */
public class    BookMovieState {
    private Movie movie;
    private String date;
    private Cinema cinema;
    private ShowTime showtime;
    private Set<String> seats = new HashSet<>();
    private Integer totalCost;
    private String bookingError;
    private String bookingSuccessMessage;

    public Movie getMovie() { return movie; }
    public String getDate() { return date; }
    public Cinema getCinema() { return cinema; }
    public ShowTime getShowtime() { return showtime; }
    public Set<String> getSeats() { return seats; }
    public Integer getTotalCost() { return totalCost; }
    public String getBookingError() { return bookingError; }
    public String getBookingSuccessMessage() { return bookingSuccessMessage; }

    public void setMovie(Movie movie) { this.movie = movie; }
    public void setDate(String date) { this.date = date; }
    public void setCinema(Cinema cinema) { this.cinema = cinema; }
    public void setShowtime(ShowTime showtime) { this.showtime = showtime; }
    public void setSeats(Set<String> seats) { this.seats = seats; }
    public void setTotalCost(Integer totalCost) { this.totalCost = totalCost; }
    public void setBookingError(String bookingError) { this.bookingError = bookingError; }
    public void setBookingSuccessMessage(String msg) { this.bookingSuccessMessage = msg; }
}