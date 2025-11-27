package use_case.movie_filter;

import entity.Movie;
import java.util.List;
import java.util.stream.Collectors;

public class FilterMoviesInteractor implements FilterMoviesInputBoundary {

    private final FilterMoviesDataAccessInterface movieDao;
    private final FilterMoviesOutputBoundary presenter;

    public FilterMoviesInteractor(FilterMoviesDataAccessInterface movieDao,
                                  FilterMoviesOutputBoundary presenter) {
        this.movieDao = movieDao;
        this.presenter = presenter;
    }

    @Override
    public void execute(FilterMoviesInputData inputData) {

        // Call DAO directly — NO Local Filtering anymore
        List<Movie> movies = movieDao.getFilteredMovies(
                inputData.getYear(),
                inputData.getRating(),
                inputData.getGenre(),
                inputData.getSearch(),
                inputData.getPage()
        );

        // Convert to poster URLs
        List<String> posters = movieDao.getPosterUrls(movies);

        // Send to presenter
        presenter.present(new FilterMoviesOutputData(
                posters,
                inputData.getPage(),
                10   // total pages
        ));
    }
}
