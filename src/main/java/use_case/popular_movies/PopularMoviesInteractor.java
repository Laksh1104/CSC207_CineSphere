package use_case.popular_movies;

import entity.Movie;

import java.util.List;

public class PopularMoviesInteractor implements PopularMoviesInputBoundary{

    private final PopularMoviesDataAccessInterface movieDataAccess;
    private final PopularMoviesOutputBoundary presenter;

    public PopularMoviesInteractor(PopularMoviesDataAccessInterface movieDataAccess,
                                   PopularMoviesOutputBoundary presenter) {
        this.movieDataAccess = movieDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(PopularMoviesInputData inputData) {
        try {
            List<Movie> movies = movieDataAccess.getPopularMovies();
            List<String> posterUrls = movieDataAccess.getPosterUrls(movies);

            PopularMoviesOutputData outputData = new PopularMoviesOutputData(posterUrls, inputData.getPage());

            presenter.present(outputData);
        } catch (Exception e) {
            presenter.presentError(e.getMessage());
        }
    }
}
