package interface_adapter.movie_details;

import use_case.movie_details.MovieDetailsOutputBoundary;
import use_case.movie_details.MovieDetailsOutputData;
import view.MovieDetailsView;

public class MovieDetailsPresenter implements MovieDetailsOutputBoundary {
    private final MovieDetailsView view;

    public MovieDetailsPresenter(MovieDetailsView view) {
        this.view = view;
    }

    @Override
    public void presentMovieDetails(MovieDetailsOutputData outputData) {
        MovieDetailsViewModel viewModel = new MovieDetailsViewModel(
            outputData.filmName(),
            outputData.director(),
            outputData.releaseDate(),
            outputData.ratingOutOf5(),
            outputData.genres(),
            outputData.description(),
            outputData.reviews(),
            outputData.posterUrl()
        );
        view.displayMovieDetails(viewModel);
    }

    @Override
    public void presentError(String errorMessage) {
        view.displayError(errorMessage);
    }
}
