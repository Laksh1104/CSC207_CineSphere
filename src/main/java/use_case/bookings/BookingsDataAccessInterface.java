package use_case.bookings;

import entity.MovieTicket;

import java.util.List;

/**
 * Data access interface for retrieving a user's bookings.
 */
public interface BookingsDataAccessInterface {

    /**
     * Returns all bookings for the given username.
     *
     * @param username the username
     * @return list of MovieTicket; never null
     */
    List<MovieTicket> getBookings(String username);
}
