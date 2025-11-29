package data_access;

import entity.*;
import use_case.book_movie.BookTicketDataAccessInterface;

import java.util.*;

public class InMemoryTicketDataAccessObject implements BookTicketDataAccessInterface {

    private final Map<String, Set<String>> bookedSeatsMap = new HashMap<>();
    private final Map<String, List<Seat>> seatLayoutMap = new HashMap<>();

    private String key(Movie m, Cinema c, String date, ShowTime st) {
        return m.getId() + "|" +
                c.getCinemaId() + "|" +
                date + "|" +
                st.getStartTime();
    }
    @Override
    public Set<String> getBookedSeats(Movie m, Cinema c, String date, ShowTime st) {
        String k = key(m, c, date, st);
        return bookedSeatsMap.getOrDefault(k, new HashSet<>());
    }

    public List<Seat> getSeatLayout(Movie m, Cinema c, String date, ShowTime st) {
        String k = key(m, c, date, st);

        if (!seatLayoutMap.containsKey(k)) {
            List<Seat> seats = new ArrayList<>();
            for (char row = 'A'; row <= 'J'; row++) {
                for (int col = 1; col <= 20; col++) {
                    seats.add(new Seat(row + "" + col));  // all empty initially
                }
            }
            seatLayoutMap.put(k, seats);
        }

        return seatLayoutMap.get(k);
    }

    @Override
    public void saveBooking(MovieTicket movieTicket) {
        String mapKey = key(movieTicket.getMovie(), movieTicket.getCinema(),
                movieTicket.getDate(), movieTicket.getTime());

        // Save booked seat names
        bookedSeatsMap.putIfAbsent(mapKey, new HashSet<>());
        bookedSeatsMap.get(mapKey).addAll(movieTicket.getSeats());

        // Update seat entities
        List<Seat> seats = seatLayoutMap.get(mapKey);
        for (Seat s : seats) {
            if (movieTicket.getSeats().contains(s.getSeatName())) {
                s.book();
            }
        }
    }
}
