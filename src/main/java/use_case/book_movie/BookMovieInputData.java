package use_case.book_movie;

import entity.Cinema;
import entity.Movie;
import entity.ShowTime;

import java.util.HashSet;
import java.util.Set;

public class BookMovieInputData {
    private final Movie movie;
    private final Cinema cinema;
    private final String date;
    private final ShowTime showtime; // or could be date + time combined
    private final Set<String> seats;

    public BookMovieInputData(Movie movie, String date, Cinema cinema, ShowTime showtime, Set<String> seats) {
        this.movie = movie;
        this.cinema = cinema;
        this.date = date;
        this.showtime = showtime;
        this.seats = new HashSet<>(seats); // defensive copy
    }

    public Movie getMovie() { return movie; }
    public Cinema getCinema() { return cinema; }
    public String getDate() { return date; }
    public ShowTime getShowtime() { return showtime; }
    public Set<String> getSeats() { return seats; }
}
