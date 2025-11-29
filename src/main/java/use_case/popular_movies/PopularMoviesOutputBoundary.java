package use_case.popular_movies;

public interface PopularMoviesOutputBoundary {
    void present(PopularMoviesOutputData outputData);

    void presentError(String errorMessage);
}
