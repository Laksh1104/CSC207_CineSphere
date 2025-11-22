package interface_adapter.filter_movies;

import use_case.movie_filter.FilterMoviesInputBoundary;
import use_case.movie_filter.FilterMoviesInputData;

public class FilterMoviesController {

    private final FilterMoviesInputBoundary interactor;

    public FilterMoviesController(FilterMoviesInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String year, String rating, String genre, String search, int page) {
        FilterMoviesInputData data = new FilterMoviesInputData(year, rating, genre, search, page);
        interactor.execute(data);
    }
}
