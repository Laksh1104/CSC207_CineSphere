package use_case.movie_filter;

import entity.Movie;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FilterMoviesInteractor.
 */
class FilterMoviesInteractorTest {

    private static class FakeFilterMoviesDAO implements FilterMoviesDataAccessInterface {

        List<Movie> moviesToReturn = Collections.emptyList();
        List<String> postersToReturn = List.of("p1", "p2");
        int lastTotalPages = 4;
        Map<String, Integer> genresToReturn = Map.of("Action", 28, "Drama", 18);

        AtomicInteger getFilteredMoviesCalls = new AtomicInteger();
        AtomicInteger getPosterUrlsCalls = new AtomicInteger();
        AtomicInteger getLastTotalPagesCalls = new AtomicInteger();
        AtomicInteger getMovieGenresCalls = new AtomicInteger();

        @Override
        public List<Movie> getFilteredMovies(String year, String rating, String genreId, String search, int page) {
            getFilteredMoviesCalls.incrementAndGet();
            return moviesToReturn;
        }

        @Override
        public List<String> getPosterUrls(List<Movie> movies) {
            getPosterUrlsCalls.incrementAndGet();
            return postersToReturn;
        }

        @Override
        public Map<String, Integer> getMovieGenres() {
            getMovieGenresCalls.incrementAndGet();
            return genresToReturn;
        }

        @Override
        public int getLastTotalPages() {
            getLastTotalPagesCalls.incrementAndGet();
            return lastTotalPages;
        }
    }

    private static class CapturingPresenter implements FilterMoviesOutputBoundary {
        FilterMoviesOutputData last;

        @Override
        public void present(FilterMoviesOutputData data) {
            last = data;
        }
    }

    @Test
    void execute_buildsOutputDataAndFetchesGenresFirstTime() {
        FakeFilterMoviesDAO dao = new FakeFilterMoviesDAO();
        CapturingPresenter presenter = new CapturingPresenter();

        FilterMoviesInteractor interactor = new FilterMoviesInteractor(dao, presenter);

        FilterMoviesInputData input =
                new FilterMoviesInputData("2020", "7-9", "28", "Nolan", 2);

        interactor.execute(input);

        assertNotNull(presenter.last);
        assertEquals(dao.postersToReturn, presenter.last.getPosters());
        assertTrue(presenter.last.getFilmIds().isEmpty());  // moviesToReturn is empty
        assertEquals(2, presenter.last.getPage());
        assertEquals(dao.lastTotalPages, presenter.last.getTotalPages());
        assertEquals(dao.genresToReturn, presenter.last.getGenres());

        assertEquals(1, dao.getFilteredMoviesCalls.get());
        assertEquals(1, dao.getPosterUrlsCalls.get());
        assertEquals(1, dao.getLastTotalPagesCalls.get());
        assertEquals(1, dao.getMovieGenresCalls.get());
    }

    @Test
    void execute_usesCachedGenresOnSecondCall() {
        FakeFilterMoviesDAO dao = new FakeFilterMoviesDAO();
        CapturingPresenter presenter = new CapturingPresenter();

        FilterMoviesInteractor interactor = new FilterMoviesInteractor(dao, presenter);

        interactor.execute(new FilterMoviesInputData("2020", "All Ratings", "All Genres", "", 1));
        Map<String, Integer> firstGenres = presenter.last.getGenres();
        assertEquals(dao.genresToReturn, firstGenres);

        dao.genresToReturn = Map.of("Changed", 99);

        interactor.execute(new FilterMoviesInputData("2021", "5+", "All Genres", "", 3));
        Map<String, Integer> secondGenres = presenter.last.getGenres();

        assertEquals(firstGenres, secondGenres);
        assertEquals(1, dao.getMovieGenresCalls.get(), "DAO.getMovieGenres should only be called once");
    }

    @Test
    void execute_refetchesGenresWhenCachedIsEmpty() {
        FakeFilterMoviesDAO dao = new FakeFilterMoviesDAO();
        CapturingPresenter presenter = new CapturingPresenter();

        dao.genresToReturn = Collections.emptyMap();

        FilterMoviesInteractor interactor = new FilterMoviesInteractor(dao, presenter);

        interactor.execute(new FilterMoviesInputData("2020", "All Ratings", "All Genres", "", 1));
        assertEquals(1, dao.getMovieGenresCalls.get());

        dao.genresToReturn = Map.of("Comedy", 35);

        interactor.execute(new FilterMoviesInputData("2020", "All Ratings", "All Genres", "", 2));
        assertEquals(2, dao.getMovieGenresCalls.get());
        assertEquals(dao.genresToReturn, presenter.last.getGenres());
    }

    @Test
    void filterMoviesOutputData_gettersWork() {
        Map<String, Integer> genres = Map.of("Action", 28);
        FilterMoviesOutputData data =
                new FilterMoviesOutputData(List.of("p1"), List.of(1), 2, 5, genres);

        assertEquals(List.of("p1"), data.getPosters());
        assertEquals(List.of(1), data.getFilmIds());
        assertEquals(2, data.getPage());
        assertEquals(5, data.getTotalPages());
        assertEquals(genres, data.getGenres());
    }

    @Test
    void filterMoviesInputData_gettersWork() {
        FilterMoviesInputData data =
                new FilterMoviesInputData("2020", "7-9", "28", "test", 3);

        assertEquals("2020", data.getYear());
        assertEquals("7-9", data.getRating());
        assertEquals("28", data.getGenre());
        assertEquals("test", data.getSearch());
        assertEquals(3, data.getPage());
    }
}
