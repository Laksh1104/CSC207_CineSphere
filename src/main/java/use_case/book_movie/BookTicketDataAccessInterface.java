package use_case.book_movie;

import entity.Cinema;
import entity.Movie;
import entity.MovieTicket;
import entity.ShowTime;

import java.util.Set;

public interface BookTicketDataAccessInterface {
    Set<String> getBookedSeats(Movie m , Cinema c, String date, ShowTime st);
    void saveBooking(MovieTicket movieTicket);
}
