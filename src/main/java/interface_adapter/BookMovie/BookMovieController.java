package interface_adapter.BookMovie;

import entity.Cinema;
import entity.Movie;
import entity.ShowTime;
import use_case.book_movie.BookMovieInputBoundary;
import use_case.book_movie.BookMovieInputData;

import java.util.Set;

/**
 * The controller for the Book Movie Use Case.
 * It receives input from the view, constructs the input data, and calls the interactor.
 */
public class BookMovieController {

    private final BookMovieInputBoundary BookMovieUseCaseInteractor;

    public BookMovieController(BookMovieInputBoundary BookMovieUseCaseInteractor) {
        this.BookMovieUseCaseInteractor = BookMovieUseCaseInteractor;
    }

    /**
     * Executes the Book Movie Use Case.
     *
     * @param movie     the selected movie
     * @param cinema    the selected cinema
     * @param showtime  the selected showtime
     * @param seats     the set of selected seats
     * @param date      the date of the show (from the view's date picker)
     */
    public void execute(Movie movie, String date, Cinema cinema, ShowTime showtime, Set<String> seats) {
        if (movie == null || cinema == null || showtime == null || seats == null || seats.isEmpty() || date == null) {
            BookMovieUseCaseInteractor.execute(null);
            return;
        }

        // Build InputData for the interactor
        BookMovieInputData inputData = new BookMovieInputData(movie, date, cinema, showtime, seats);

        // Call the interactor
        BookMovieUseCaseInteractor.execute(inputData);
    }

    public Set<String> getBookedSeats(Movie m, Cinema c, String date, ShowTime st) {
        return BookMovieUseCaseInteractor.getBookedSeats(m, c, date, st);
    }
}
