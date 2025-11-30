package interface_adapter.movie_details;

import use_case.movie_details.MovieDetailsInputBoundary;
import use_case.movie_details.MovieDetailsInputData;

public class MovieDetailsController {
    private final MovieDetailsInputBoundary interactor;

    public MovieDetailsController(MovieDetailsInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Shows the movie details for the specified film ID.
     *
     * @param filmId the ID of the film to display
     */
    public void showMovieDetails(int filmId) {
        final MovieDetailsInputData inputData = new MovieDetailsInputData(filmId);
        interactor.execute(inputData);
    }
}
