package data_access;

import entity.MovieTicket;
import entity.Seat;
import use_case.book_movie.BookTicketDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;

import java.util.List;
import java.util.Set;

/**
 * Decorates another BookTicketDataAccessInterface (e.g., InMemoryTicketDataAccessObject)
 * to also persist each confirmed booking into the current user's profile in
 * UserProfileJsonDataAccessObject.
 *
 * Responsibilities:
 *  - delegate getBookedSeats / getSeatLayout to the underlying DAO
 *  - on saveBooking:
 *      * delegate to the underlying DAO
 *      * append the booking under the logged-in user's JSON profile
 */
public class PersistentTicketDataAccessObject implements BookTicketDataAccessInterface {

    private final BookTicketDataAccessInterface delegate;
    private final LoginUserDataAccessInterface userDAO;
    private final UserProfileJsonDataAccessObject userProfileDAO;

    public PersistentTicketDataAccessObject(BookTicketDataAccessInterface delegate,
                                            LoginUserDataAccessInterface userDAO,
                                            UserProfileJsonDataAccessObject userProfileDAO) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate BookTicketDataAccessInterface cannot be null");
        }
        if (userDAO == null) {
            throw new IllegalArgumentException("LoginUserDataAccessInterface cannot be null");
        }
        if (userProfileDAO == null) {
            throw new IllegalArgumentException("UserProfileJsonDataAccessObject cannot be null");
        }

        this.delegate = delegate;
        this.userDAO = userDAO;
        this.userProfileDAO = userProfileDAO;
    }

    @Override
    public Set<String> getBookedSeats(String movieName,
                                      String cinemaName,
                                      String date,
                                      String startTime,
                                      String endTime) {
        return delegate.getBookedSeats(movieName, cinemaName, date, startTime, endTime);
    }

    @Override
    public List<Seat> getSeatLayout(String movieName,
                                    String cinemaName,
                                    String date,
                                    String startTime,
                                    String endTime) {
        return delegate.getSeatLayout(movieName, cinemaName, date, startTime, endTime);
    }

    @Override
    public void saveBooking(MovieTicket movieTicket) {
        // 1) Persist in the underlying DAO (so seats become unavailable, etc.)
        delegate.saveBooking(movieTicket);

        // 2) Also save into the current user's JSON profile (if we have a user)
        String username = getCurrentUsernameSafely();
        if (username != null && !username.isBlank()) {
            userProfileDAO.addBooking(username, movieTicket);
        }
        // If there is no logged-in user, we just skip writing to JSON.
        // (You can change this to throw if you want stricter behaviour.)
    }

    /**
     * Helper to guard against any unexpected issues when querying the userDAO.
     */
    private String getCurrentUsernameSafely() {
        try {
            return userDAO.getCurrentUsername();
        } catch (Exception e) {
            // swallow and return null so booking still succeeds in delegate
            return null;
        }
    }
}
