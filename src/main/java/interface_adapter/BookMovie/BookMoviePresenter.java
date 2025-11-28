package interface_adapter.BookMovie;

import use_case.book_movie.BookMovieOutputBoundary;
import use_case.book_movie.BookMovieOutputData;

/**
 * The Presenter for the Book Movie Use Case.
 */
public class BookMoviePresenter implements BookMovieOutputBoundary {

    private final BookMovieViewModel bookMovieViewModel;


    public BookMoviePresenter (BookMovieViewModel bookMovieViewModel) {
        this.bookMovieViewModel = bookMovieViewModel;
    }

    @Override
    public void prepareSuccessView(BookMovieOutputData response) {
        BookMovieState state = bookMovieViewModel.getState();

        // Update ViewModel state
        state.setMovieName(response.getMovieName());
        state.setCinemaName(response.getCinemaName());
        state.setDate(response.getDate());
        state.setStartTime(response.getStartTime());
        state.setEndTime(response.getEndTime());
        state.setSeats(response.getSeats());
        state.setTotalCost(response.getTotalCost());

        String msg = "Booking confirmed on "
                + response.getDate()
                + " from " + response.getStartTime()
                + " to " + response.getEndTime()
                + "\nSeats: " + response.getSeatNumbers()
                + "\nPrice: $" + response.getTotalCost();

        state.setBookingSuccessMessage(msg);
        state.setBookingError(null);

        bookMovieViewModel.setState(state);
        bookMovieViewModel.firePropertyChange();


    }

    @Override
    public void prepareFailView(String error) {
        BookMovieState state = bookMovieViewModel.getState();
        state.setBookingError(error);
        bookMovieViewModel.firePropertyChange();
    }
}