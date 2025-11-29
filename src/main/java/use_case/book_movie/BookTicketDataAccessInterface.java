package use_case.book_movie;

import entity.MovieTicket;
import entity.Seat;

import java.util.List;
import java.util.Set;

public interface BookTicketDataAccessInterface {

    Set<String> getBookedSeats(String movieName, String cinemaName, String date,  String startTime,  String endTime);

    List<Seat> getSeatLayout(String movieName, String cinemaName, String date, String startTime, String endTime);

    void saveBooking(MovieTicket movieTicket);
}
