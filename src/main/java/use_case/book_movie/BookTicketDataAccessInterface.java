package use_case.book_movie;

import entity.*;

import java.util.List;
import java.util.Set;

public interface BookTicketDataAccessInterface {
    Set<String> getBookedSeats(Movie m , Cinema c, String date, ShowTime st);
    void saveBooking(MovieTicket movieTicket);
    List<Seat> getSeatLayout(Movie m, Cinema c, String date, ShowTime st);
}
