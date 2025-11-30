package book_movie;

import entity.MovieTicket;
import entity.Seat;
import use_case.book_movie.BookTicketDataAccessInterface;

import java.util.*;

public class InMemoryTicketTestDAO implements BookTicketDataAccessInterface {

    public Set<String> bookedSeats = new HashSet<>();
    public List<Seat> seatLayout = new ArrayList<>();

    public InMemoryTicketTestDAO() {
        // default layout (A1–A3, B1 only for testing)
        seatLayout = List.of(
                new Seat("A1"), new Seat("A2"), new Seat("A3"), new Seat("B1")
        );
    }

    @Override
    public Set<String> getBookedSeats(String movie, String cinema, String date, String start, String end) {
        return bookedSeats;
    }

    @Override
    public List<Seat> getSeatLayout(String movie, String cinema, String date, String start, String end) {
        return seatLayout;
    }

    @Override
    public void saveBooking(MovieTicket ticket) {
        bookedSeats.addAll(ticket.getSeats());
    }

    @Override
    public List<Seat> getSeatLayout(String movieName, String cinemaName, String date, String startTime) {
        return List.of();
    }
}
