package interface_adapter.bookings;

import entity.MovieTicket;
import use_case.bookings.BookingsDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;

import java.util.Collections;
import java.util.List;

/**
 * Simple controller to fetch bookings for the currently logged-in user.
 * Mirrors the pattern used in WatchlistController.
 */
public class BookingsController {

    private final BookingsDataAccessInterface bookingsDAO;
    private final LoginUserDataAccessInterface userDAO;

    public BookingsController(BookingsDataAccessInterface bookingsDAO,
                              LoginUserDataAccessInterface userDAO) {
        this.bookingsDAO = bookingsDAO;
        this.userDAO = userDAO;
    }

    /**
     * Returns all bookings for the current logged-in user.
     */
    public List<MovieTicket> getBookingsForCurrentUser() {
        String username = userDAO.getCurrentUsername();
        if (username == null || username.isBlank()) {
            return Collections.emptyList();
        }
        return bookingsDAO.getBookings(username);
    }
}
