package entity;

import java.util.HashSet;
import java.util.Set;

public class MovieTicket {
    private final Movie movie;
    private final Cinema cinema;
    private final String date;
    private final ShowTime time;
    private final Set<String> seats;
    private final int cost;
    public  final int SEAT_PRICE = 20;

    public MovieTicket(Movie movie, Cinema cinema, String date, ShowTime time, Set<String> seats,  int cost) {
        this.movie = movie;
        this.cinema = cinema;
        this.date = date;
        this.time = time;
        this.seats = new HashSet<>(seats);
        this.cost = cost;
    }


    public Movie getMovie() {
        return movie;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public String getDate() {
        return date;
    }

    public ShowTime getTime() {
        return time;
    }

    public Set<String> getSeats() {
        return seats;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        String seatList = seats == null ? "[]" : String.join(", ", seats);
        String movieName = movie != null ? movie.getFilmName() : "null";
        String cinemaName = cinema != null ? cinema.getCinemaName() : "null";
        String costFormatted = String.format("$%.2f", cost);

        return String.format(
                "Movie: %s\nDate: %s\nCinema: %s\nTime: %s\nSeats: %s\nTotal Paid: %s",
                movieName,
                date,
                cinemaName,
                time,
                seatList,
                costFormatted
        );
    }
}