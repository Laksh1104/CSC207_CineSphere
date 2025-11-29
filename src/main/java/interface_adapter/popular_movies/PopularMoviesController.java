package interface_adapter.popular_movies;

import use_case.popular_movies.PopularMoviesInputBoundary;
import use_case.popular_movies.PopularMoviesInputData;

public class PopularMoviesController {
    private final PopularMoviesInputBoundary interactor;

    public PopularMoviesController(PopularMoviesInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void loadPopularMovies() {
        PopularMoviesInputData inputData = new PopularMoviesInputData(1);
        interactor.execute(inputData);
    }
}
