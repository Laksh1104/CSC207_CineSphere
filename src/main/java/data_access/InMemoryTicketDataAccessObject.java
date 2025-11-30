package data_access;

import entity.*;
import use_case.book_movie.BookTicketDataAccessInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Bookings;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class InMemoryTicketDataAccessObject implements BookTicketDataAccessInterface {

    private final Map<String, Set<String>> bookedSeatsMap = new HashMap<>();
    private final Map<String, List<Seat>> seatLayoutMap = new HashMap<>();
    private final Gson gson = new Gson();
    private final String folder = "data/prevbookings/";

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
        Bookings.getAllBookings().add(ticket);
    }

    @Override
    public List<Seat> getSeatLayout(String movieName, String cinemaName, String date, String startTime) {
        return List.of();
    }

    public Bookings loadBookings(String username) {
        try {
            FileReader reader = new FileReader(folder + username + ".json");
            Type type = new TypeToken<Bookings>() {
            }.getType();
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            return new Bookings();
        }

    }

    public void saveBookings(String username, Bookings bookings) {
        try(FileWriter writer = new FileWriter(folder + username + ".json")) {
            gson.toJson(bookings, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
