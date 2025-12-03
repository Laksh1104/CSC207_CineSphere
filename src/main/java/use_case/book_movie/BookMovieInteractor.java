package use_case.book_movie;

import entity.*;
import java.util.List;
import java.util.Set;

public class BookMovieInteractor implements BookMovieInputBoundary {

    private final BookTicketDataAccessInterface ticketDAO;
    private final BookMovieOutputBoundary presenter;

    private static final int COST_PER_SEAT = 20;

    public BookMovieInteractor(BookTicketDataAccessInterface ticketDAO,
                               BookMovieOutputBoundary presenter) {
        this.ticketDAO = ticketDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(BookMovieInputData in) {

        // Extract fields
        String movie = in.getMovieName();
        String cinema = in.getCinemaName();
        String date = in.getDate();
        String start = in.getStartTime();
        String end = in.getEndTime();
        Set<String> seats = in.getSeats();

        // Validate basic input
        String error = validateInputs(movie, cinema, date, start, end, seats);
        if (error != null) {
            presenter.prepareFailView(error);
            return;
        }

        // Validate seat availability
        error = validateSeatAvailability(movie, cinema, date, start, end, seats);
        if (error != null) {
            presenter.prepareFailView(error);
            return;
        }

        // Calculate cost
        int totalCost = seats.size() * COST_PER_SEAT;

        // Create and save ticket
        MovieTicket ticket = new MovieTicket(movie, cinema, date, start, end, seats, totalCost);
        ticketDAO.saveBooking(ticket);

        // Send success response
        BookMovieOutputData out = new BookMovieOutputData(
                movie, cinema, date, start, end, seats, totalCost
        );
        presenter.prepareSuccessView(out);
    }

    // ------------------ Validation Helpers ------------------

    private String validateInputs(String movie, String cinema, String date,
                                  String start, String end, Set<String> seats) {

        if (isBlank(movie) || isBlank(cinema) || isBlank(date)
                || isBlank(start)) {
            return "Some booking details are missing.";
        }

        if (seats.isEmpty()) {
            return "No seats were selected.";
        }

        return null;
    }

    private String validateSeatAvailability(String movie, String cinema, String date,
                                            String start, String end, Set<String> seats) {

        Set<String> booked = ticketDAO.getBookedSeats(movie, cinema, date, start, end);

        for (String seat : seats) {
            if (booked.contains(seat)) {
                return "Seat " + seat + " is already booked.";
            }
        }

        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // Passthrough methods

    @Override
    public Set<String> getBookedSeats(String movie, String cinema, String date,
                                      String start, String end) {
        return ticketDAO.getBookedSeats(movie, cinema, date, start, end);
    }

    @Override
    public List<Seat> loadSeatLayout(String movie, String cinema, String date,
                                     String start, String end) {
        return ticketDAO.getSeatLayout(movie, cinema, date, start, end);
    }
}
