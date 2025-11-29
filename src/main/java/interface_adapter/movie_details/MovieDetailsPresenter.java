package interface_adapter.movie_details;

import use_case.movie_details.MovieDetailsOutputBoundary;
import use_case.movie_details.MovieDetailsOutputData;

public class MovieDetailsPresenter implements MovieDetailsOutputBoundary {
    private final MovieDetailsViewModel viewModel;

    public MovieDetailsPresenter(MovieDetailsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentMovieDetails(MovieDetailsOutputData outputData) {
        MovieDetailsState newState = new MovieDetailsState(
            outputData.filmName(),
            outputData.director(),
            outputData.releaseDate(),
            outputData.ratingOutOf5(),
            outputData.genres(),
            outputData.description(),
            outputData.reviews(),
            outputData.posterUrl()
        );

        viewModel.setState(newState);
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
    }
}
