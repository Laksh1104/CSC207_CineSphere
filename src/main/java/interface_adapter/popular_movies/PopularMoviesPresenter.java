package interface_adapter.popular_movies;

import use_case.popular_movies.PopularMoviesOutputBoundary;
import use_case.popular_movies.PopularMoviesOutputData;

import java.util.List;

public class PopularMoviesPresenter implements PopularMoviesOutputBoundary {
    private final PopularMoviesViewModel viewModel;

    public PopularMoviesPresenter(PopularMoviesViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(PopularMoviesOutputData outputData) {
        viewModel.setPosterUrls(outputData.getPosterUrls());
        viewModel.setErrorMessage(null);
    }

    public void presentError(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
        viewModel.setPosterUrls(List.of());
    }
}
