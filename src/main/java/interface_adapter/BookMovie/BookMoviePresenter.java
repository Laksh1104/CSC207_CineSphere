package interface_adapter.BookMovie;

import interface_adapter.ViewManagerModel;
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
        state.setMovie(response.getMovie());
        state.setCinema(response.getCinema());
        state.setShowtime(response.getShowtime());
        state.setSeats(response.getSeats());
        state.setTotalCost(response.getTotalCost());

        String msg = response.getMovie().getFilmName() + " booked on "
                + response.getDate() + " at " + response.getCinema().getCinemaName()
                + " from " + response.getShowtime().getStartTime()
                + " to " + response.getShowtime().getEndTime()
                + "\nSeats: " + response.getSeatNumbers()
                + "\nPrice: $" + response.getTotalCost();

        state.setBookingSuccessMessage(msg);

        // clear error
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