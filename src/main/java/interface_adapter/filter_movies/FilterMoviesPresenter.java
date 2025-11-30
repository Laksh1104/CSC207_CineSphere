package interface_adapter.filter_movies;

import use_case.movie_filter.FilterMoviesOutputBoundary;
import use_case.movie_filter.FilterMoviesOutputData;

public class FilterMoviesPresenter implements FilterMoviesOutputBoundary {

    private final FilterMoviesViewModel viewModel;

    public FilterMoviesPresenter(FilterMoviesViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(FilterMoviesOutputData data) {
        viewModel.setPosters(data.getPosters());
        viewModel.setFilmIds(data.getFilmIds());
        viewModel.setPage(data.getPage());
        viewModel.setTotalPages(data.getTotalPages());
        viewModel.setGenres(data.getGenres());   // NEW
    }
}
