package use_case.book_movie;

import entity.Cinema;
import entity.Movie;
import entity.ShowTime;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BookMovieOutputData {
    private final Movie movie;
    private final String date;
    private final Cinema cinema;
    private final ShowTime showtime;
    private final Set<String> seats;
    private final int totalCost;


    public BookMovieOutputData(Movie movie, String date, Cinema cinema, ShowTime showtime, Set<String> seats, int totalCost
    ) {
        this.movie = movie;
        this.date = date;
        this.cinema = cinema;
        this.showtime = showtime;
        this.seats = new HashSet<>(seats);
        this.totalCost = totalCost;
    }

    public Movie getMovie() { return movie; }
    public String getDate() { return date; }
    public Cinema getCinema() { return cinema; }
    public ShowTime getShowtime() { return showtime; }
    public Set<String> getSeats() { return seats; }
    public int getTotalCost() { return totalCost; }

    public String getSeatNumbers(){
        String seating = "";
        for(String seat : seats){
            seating= seating + " " + seat;
        }
        return seating;
    }
}

