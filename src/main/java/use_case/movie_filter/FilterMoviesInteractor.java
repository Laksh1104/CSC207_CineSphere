package use_case.movie_filter;

import entity.Movie;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FilterMoviesInteractor implements FilterMoviesInputBoundary {

    private final FilterMoviesDataAccessInterface movieDao;
    private final FilterMoviesOutputBoundary presenter;

    private Map<String, Integer> cachedGenres;

    public FilterMoviesInteractor(FilterMoviesDataAccessInterface movieDao,
                                  FilterMoviesOutputBoundary presenter) {
        this.movieDao = movieDao;
        this.presenter = presenter;
    }

    @Override
    public void execute(FilterMoviesInputData inputData) {

        List<Movie> movies = movieDao.getFilteredMovies(
                inputData.getYear(),
                inputData.getRating(),
                inputData.getGenre(),
                inputData.getSearch(),
                inputData.getPage()
        );

        List<String> posters = movieDao.getPosterUrls(movies);

        List<Integer> filmIds = movies.stream()
                .map(Movie::getId)
                .collect(Collectors.toList());

        int totalPages = movieDao.getLastTotalPages();

        if (cachedGenres == null || cachedGenres.isEmpty()) {
            cachedGenres = movieDao.getMovieGenres();
        }

        presenter.present(new FilterMoviesOutputData(
                posters,
                filmIds,
                inputData.getPage(),
                totalPages,
                cachedGenres
        ));
    }
}
