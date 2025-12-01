package data_access;

import entity.MovieTicket;
import entity.Seat;
import use_case.book_movie.BookTicketDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;

import java.util.List;
import java.util.Set;

/**
 * Decorates another BookTicketDataAccessInterface (e.g., InMemoryTicketDataAccessObject)
 * to persist bookings per user into UserProfileJsonDataAccessObject.
 */
public class PersistentTicketDataAccessObject implements BookTicketDataAccessInterface {

    private final BookTicketDataAccessInterface delegate;
    private final LoginUserDataAccessInterface userDAO;
    private final UserProfileJsonDataAccessObject userProfileDAO;

    public PersistentTicketDataAccessObject(BookTicketDataAccessInterface delegate,
                                            LoginUserDataAccessInterface userDAO,
                                            UserProfileJsonDataAccessObject userProfileDAO) {
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
        delegate.saveBooking(movieTicket);

        String username = userDAO.getCurrentUsername();
        if (username != null && !username.isBlank()) {
            userProfileDAO.addBooking(username, movieTicket);
        }
    }
}
