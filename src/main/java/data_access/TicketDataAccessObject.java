package data_access;

import entity.Cinema;
import entity.Movie;
import entity.MovieTicket;
import entity.ShowTime;
import use_case.book_movie.BookTicketDataAccessInterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TicketDataAccessObject implements BookTicketDataAccessInterface {

    private final Map<String, Set<String>> bookedSeatsMap = new HashMap<>();

    private String key(Movie m, Cinema c, String date, ShowTime st) {
        return m.getFilmId() + "|" +
                c.getCinemaId() + "|" +
                date + "|" +
                st.getStartTime();
    }
    @Override
    public Set<String> getBookedSeats(Movie m, Cinema c, String date, ShowTime st) {
        return bookedSeatsMap.getOrDefault(key(m, c, date, st), new HashSet<>());
    }

    @Override
    public void saveBooking(MovieTicket movieTicket) {
        String mapKey = key(movieTicket.getMovie(), movieTicket.getCinema(),
                movieTicket.getDate(), movieTicket.getTime());
        bookedSeatsMap.putIfAbsent(mapKey, new HashSet<>());
        bookedSeatsMap.get(mapKey).addAll(movieTicket.getSeats());
    }
}
