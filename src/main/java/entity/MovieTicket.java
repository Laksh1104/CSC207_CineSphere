package entity;

import java.util.HashSet;
import java.util.Set;

public class MovieTicket {

    private final String movieName;
    private final String cinemaName;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final Set<String> seats;
    private final int cost;

    public MovieTicket(String movieName, String cinemaName, String date, String startTime, String endTime, Set<String> seats, int cost) {

        this.movieName = movieName;
        this.cinemaName = cinemaName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.seats = new HashSet<>(seats);
        this.cost = cost;
    }

    public String getMovieName() { return movieName; }
    public String getCinemaName() { return cinemaName; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public Set<String> getSeats() { return seats; }
    public int getCost() { return cost; }
}
