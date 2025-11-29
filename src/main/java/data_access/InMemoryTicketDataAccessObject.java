package data_access;

import entity.*;
import use_case.book_movie.BookTicketDataAccessInterface;

import java.util.*;

public class InMemoryTicketDataAccessObject implements BookTicketDataAccessInterface {

    private final Map<String, Set<String>> bookedSeatsMap = new HashMap<>();
    private final Map<String, List<Seat>> seatLayoutMap = new HashMap<>();

    private String key(String movieName, String cinemaName, String date, String startTime, String endTime) {
        return movieName + "|" + cinemaName + "|" + date + "|" + startTime + "|" + endTime;
    }

    @Override
    public Set<String> getBookedSeats(String movieName, String cinemaName, String date, String startTime, String endTime) {
        String k = key(movieName, cinemaName, date, startTime, endTime);
        return bookedSeatsMap.getOrDefault(k, new HashSet<>());
    }

    public List<Seat> getSeatLayout(String movieName, String cinemaName, String date, String startTime, String endTime) {
        String k = key(movieName, cinemaName, date, startTime, endTime);

        if (!seatLayoutMap.containsKey(k)) {
            List<Seat> seats = new ArrayList<>();
            for (char row = 'A'; row <= 'J'; row++) {
                for (int col = 1; col <= 20; col++) {
                    seats.add(new Seat(row + "" + col));
                }
            }
            seatLayoutMap.put(k, seats);
        }

        return seatLayoutMap.get(k);
    }

    @Override
    public void saveBooking(MovieTicket ticket) {
        String k = key(ticket.getMovieName(), ticket.getCinemaName(), ticket.getDate(), ticket.getStartTime(), ticket.getEndTime()
        );

        // Save booked seat names
        bookedSeatsMap.putIfAbsent(k, new HashSet<>());
        bookedSeatsMap.get(k).addAll(ticket.getSeats());

        // Update seat entities
        List<Seat> seats = getSeatLayout(ticket.getMovieName(), ticket.getCinemaName(), ticket.getDate(), ticket.getStartTime(), ticket.getEndTime());
        for (Seat s : seats) {
            if (ticket.getSeats().contains(s.getSeatName())) {
                s.book();
            }
        }
    }
}
