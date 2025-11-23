package interface_adapter.popular_movies;

import use_case.popular_movies.PopularMoviesOutputBoundary;
import use_case.popular_movies.PopularMoviesOutputData;

public class PopularMoviesPresenter implements PopularMoviesOutputBoundary {
    private final PopularMoviesViewModel viewModel;

    public PopularMoviesPresenter(PopularMoviesViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(PopularMoviesOutputData outputData) {
        viewModel.setPosterUrls(outputData.getPosterUrls());
    }

}
