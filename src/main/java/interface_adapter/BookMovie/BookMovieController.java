package interface_adapter.BookMovie;

import entity.Cinema;
import entity.Movie;
import entity.Seat;
import entity.ShowTime;
import use_case.book_movie.BookMovieInputBoundary;
import use_case.book_movie.BookMovieInputData;

import java.util.List;
import java.util.Set;

/**
 * The controller for the Book Movie Use Case.
 * It receives input from the view, constructs the input data, and calls the interactor.
 */
public class BookMovieController {

    private final BookMovieInputBoundary bookMovieUseCaseInteractor;

    public BookMovieController(BookMovieInputBoundary bookMovieUseCaseInteractor) {
        this.bookMovieUseCaseInteractor = bookMovieUseCaseInteractor;
    }

    /**
     * Executes the Book Movie Use Case.
     *
     * @param movieName   name of selected movie
     * @param cinemaName  name of selected cinema
     * @param date      selected date yyyy-MM-dd
     * @param timeRange "HH:mm - HH:mm"
     * @param seats     set of seat names ("A1", "B3", ...)
     */
    public void execute(String movieName, String cinemaName, String date, String timeRange, Set<String> seats) {

        // Build InputData for the interactor
        BookMovieInputData inputData = new BookMovieInputData(movieName, cinemaName, date, timeRange, seats);

        // Call the interactor
        bookMovieUseCaseInteractor.execute(inputData);
    }

    public List<Seat> loadSeatLayout(String movieName, String cinemaName, String date, String startTime, String endTime) {
        return bookMovieUseCaseInteractor.loadSeatLayout(movieName, cinemaName, date, startTime, endTime);
    }
}
