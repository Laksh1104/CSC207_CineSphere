package use_case.book_movie;

import entity.*;

import java.util.List;
import java.util.Set;

/**
 * The Book Movie Interactor.
 */
public class BookMovieInteractor implements BookMovieInputBoundary {

    private final BookTicketDataAccessInterface ticketDataAccessObject;
    private final BookMovieOutputBoundary bookingPresenter;

    private static final int COST_PER_SEAT = 20;

    public BookMovieInteractor(BookTicketDataAccessInterface ticketDAO, BookMovieOutputBoundary presenter) {
        this.ticketDataAccessObject = ticketDAO;
        this.bookingPresenter = presenter;
    }

    @Override
    public void execute(BookMovieInputData inputData) {

       String movieName = inputData.getMovieName();
       String cinemaName = inputData.getCinemaName();
        String date = inputData.getDate();
        String startTime = inputData.getStartTime();
        String endTime = inputData.getEndTime();
        Set<String> seats = inputData.getSeats();

        // Missing fields
        if (isNullOrBlank(movieName) || isNullOrBlank(cinemaName) || isNullOrBlank(date) || isNullOrBlank(startTime) || isNullOrBlank(endTime)) {
            bookingPresenter.prepareFailView("Some booking details are missing.");
            return;
        }

        // No seats selected
        if (seats.isEmpty()) {
            bookingPresenter.prepareFailView("No seats were selected.");
            return;
        }

        // Check if seats are already booked
        Set<String> alreadyBooked = ticketDataAccessObject.getBookedSeats(
                movieName, cinemaName, date, startTime, endTime
        );

        for (String seat : seats) {
            if (alreadyBooked.contains(seat)) {
                bookingPresenter.prepareFailView("Seat " + seat + " is already booked.");
                return;
            }
        }

        int seatsCount = seats.size();
        // Compute cost
        int totalCost = seatsCount * COST_PER_SEAT;

        // Create a movie ticket
        MovieTicket movieTicket = new MovieTicket(movieName, cinemaName, date, startTime, endTime, seats, totalCost);

        // Save booking
        ticketDataAccessObject.saveBooking(movieTicket);

        // Build Output Data
        BookMovieOutputData outputData = new BookMovieOutputData(movieName, cinemaName, date, startTime, endTime, seats, totalCost);

        bookingPresenter.prepareSuccessView(outputData);
    }

    @Override
    public Set<String> getBookedSeats(String movieName, String cinemaName, String date, String startTime, String endTime) {
        return ticketDataAccessObject.getBookedSeats(movieName, cinemaName, date, startTime, endTime);
    }

    @Override
    public List<Seat> loadSeatLayout(String movieName, String cinemaName, String date, String startTime, String endTime) {
        return ticketDataAccessObject.getSeatLayout(movieName, cinemaName, date, startTime, endTime);
    }

    private boolean isNullOrBlank(String s) {
        return s == null || s.isBlank();
    }
}