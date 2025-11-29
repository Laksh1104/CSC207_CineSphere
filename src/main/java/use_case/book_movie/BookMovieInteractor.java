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

    public BookMovieInteractor(BookTicketDataAccessInterface ticketdao,
                               BookMovieOutputBoundary presenter) {
        this.ticketDataAccessObject = ticketdao;
        this.bookingPresenter = presenter;
    }

    @Override
    public void execute(BookMovieInputData inputData) {
        if (inputData == null) {
            bookingPresenter.prepareFailView("Missing booking information");
            return;
        }

        Movie movie = inputData.getMovie();
        Cinema cinema = inputData.getCinema();
        String date = inputData.getDate();
        ShowTime showtime = inputData.getShowtime();
        int seatCount = inputData.getSeats().size();

        // Each seat costs $20
        int costPerSeat = 20;
        int totalCost = seatCount * costPerSeat;

        // Create ticket entity
        MovieTicket ticket = new MovieTicket(movie, cinema, date, showtime, inputData.getSeats(), totalCost
        );

        // save booking
        ticketDataAccessObject.saveBooking(ticket);

        // get updated booked seats for this showtime
        Set<String> newlyBooked = inputData.getSeats();

        BookMovieOutputData outputData = new BookMovieOutputData(
                movie,
                date,
                cinema,
                showtime,
                newlyBooked,
                totalCost
        );

        bookingPresenter.prepareSuccessView(outputData);
    }

    @Override
    public Set<String> getBookedSeats(Movie m, Cinema c, String date, ShowTime st) {
        return ticketDataAccessObject.getBookedSeats(m, c, date, st);
    }

    @Override
    public List<Seat> loadSeatLayout(Movie m, Cinema c, String date, ShowTime st) {
        return ticketDataAccessObject.getSeatLayout(m, c, date, st);
    }
}