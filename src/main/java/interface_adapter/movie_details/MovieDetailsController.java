package interface_adapter.movie_details;

import use_case.movie_details.MovieDetailsInputBoundary;
import use_case.movie_details.MovieDetailsInputData;

public class MovieDetailsController {
    private final MovieDetailsInputBoundary interactor;

    public MovieDetailsController(MovieDetailsInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void showMovieDetails(int filmId) {
        MovieDetailsInputData inputData = new MovieDetailsInputData(filmId);
        interactor.execute(inputData);
    }
}
