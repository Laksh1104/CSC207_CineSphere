package entity;

import java.util.ArrayList;
import java.util.List;

public class Bookings {

    private static final List<MovieTicket> allBookings = new ArrayList<>();
    public static List<MovieTicket> getAllBookings() {return allBookings;}
}
