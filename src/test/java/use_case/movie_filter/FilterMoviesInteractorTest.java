package use_case.movie_filter;

import entity.Movie;
import entity.MovieFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FilterMoviesInteractorTest {

    /** Presenter that records what the interactor outputs (works across multiple execute calls). */
    static class CapturingPresenter implements FilterMoviesOutputBoundary {
        FilterMoviesOutputData last;
        int calls = 0;

        @Override
        public void present(FilterMoviesOutputData data) {
            this.last = data;
            this.calls++;
        }
    }

    @Test
    void success_outputsPostersFilmIdsPageTotalPagesAndGenres_andDelegatesInputsToDao() {
        InMemoryFilterMoviesTestDAO dao = new InMemoryFilterMoviesTestDAO();
        CapturingPresenter presenter = new CapturingPresenter();
        FilterMoviesInteractor interactor = new FilterMoviesInteractor(dao, presenter);

        MovieFactory mf = new MovieFactory();
        Movie m1 = mf.fromTMDB(10, "Movie A", "/a.jpg");
        Movie m2 = mf.fromTMDB(20, "Movie B", "/b.jpg");

        dao.moviesToReturn = List.of(m1, m2);
        dao.postersToReturn = List.of("/a.jpg", "/b.jpg");
        dao.genresToReturn = Map.of("Action", 28, "Comedy", 35);
        dao.lastTotalPages = 7;

        interactor.execute(new FilterMoviesInputData("2024", "All Ratings", "28", "", 3));

        // Interactor -> DAO call wiring
        assertEquals("2024", dao.lastYear);
        assertEquals("All Ratings", dao.lastRating);
        assertEquals("28", dao.lastGenreId);
        assertEquals("", dao.lastSearch);
        assertEquals(3, dao.lastPage);

        // Interactor output correctness
        assertNotNull(presenter.last);
        assertEquals(List.of("/a.jpg", "/b.jpg"), presenter.last.getPosters());
        assertEquals(List.of(10, 20), presenter.last.getFilmIds());
        assertEquals(3, presenter.last.getPage());
        assertEquals(7, presenter.last.getTotalPages());
        assertEquals(dao.genresToReturn, presenter.last.getGenres());

        // Genres loaded once on first execution
        assertEquals(1, dao.getMovieGenresCalls);
    }

    @Test
    void caching_genresFetchedOnce_onSameInteractorInstance_andReusedOnLaterCalls() {
        InMemoryFilterMoviesTestDAO dao = new InMemoryFilterMoviesTestDAO();
        CapturingPresenter presenter = new CapturingPresenter();
        FilterMoviesInteractor interactor = new FilterMoviesInteractor(dao, presenter);

        MovieFactory mf = new MovieFactory();
        dao.moviesToReturn = List.of(mf.fromTMDB(1, "X", "/x.jpg"));
        dao.postersToReturn = List.of("/x.jpg");
        dao.lastTotalPages = 2;

        Map<String, Integer> firstGenres = Map.of("Drama", 18);
        Map<String, Integer> secondGenres = Map.of("Horror", 27);

        // First call: genres = Drama
        dao.genresToReturn = firstGenres;
        interactor.execute(new FilterMoviesInputData("2020", "All Ratings", null, "", 1));

        assertEquals(1, dao.getMovieGenresCalls);
        assertEquals(firstGenres, presenter.last.getGenres());
        assertEquals(1, presenter.last.getPage());

        // Second call: DAO would return Horror if asked again, but interactor should reuse cached genres
        dao.genresToReturn = secondGenres;
        interactor.execute(new FilterMoviesInputData("2020", "All Ratings", null, "", 2));

        // Still only fetched once
        assertEquals(1, dao.getMovieGenresCalls);
        // Presenter still receives cached (first) genres
        assertEquals(firstGenres, presenter.last.getGenres());
        assertEquals(2, presenter.last.getPage());
    }

    @Test
    void caching_ifFirstGenresEmpty_thenInteractorRefetchesLater() {
        InMemoryFilterMoviesTestDAO dao = new InMemoryFilterMoviesTestDAO();
        CapturingPresenter presenter = new CapturingPresenter();
        FilterMoviesInteractor interactor = new FilterMoviesInteractor(dao, presenter);

        MovieFactory mf = new MovieFactory();
        dao.moviesToReturn = List.of(mf.fromTMDB(1, "X", "/x.jpg"));
        dao.postersToReturn = List.of("/x.jpg");
        dao.lastTotalPages = 1;

        // First call returns empty genres => interactor should try again later
        dao.genresToReturn = Map.of();
        interactor.execute(new FilterMoviesInputData("2020", "All Ratings", null, "", 1));

        assertEquals(1, dao.getMovieGenresCalls);
        assertEquals(Map.of(), presenter.last.getGenres());

        // Second call: now genres exist, and since cache is empty, interactor should refetch
        Map<String, Integer> nowGenres = Map.of("Action", 28);
        dao.genresToReturn = nowGenres;
        interactor.execute(new FilterMoviesInputData("2020", "All Ratings", null, "", 2));

        assertEquals(2, dao.getMovieGenresCalls);
        assertEquals(nowGenres, presenter.last.getGenres());
    }

    @Test
    void edge_emptyMovies_stillOutputsEmptyLists_andDoesNotCrash() {
        InMemoryFilterMoviesTestDAO dao = new InMemoryFilterMoviesTestDAO();
        CapturingPresenter presenter = new CapturingPresenter();
        FilterMoviesInteractor interactor = new FilterMoviesInteractor(dao, presenter);

        dao.moviesToReturn = List.of();
        dao.postersToReturn = List.of();
        dao.genresToReturn = Map.of("Action", 28);
        dao.lastTotalPages = 1;

        interactor.execute(new FilterMoviesInputData("2024", "All Ratings", null, "", 1));

        assertNotNull(presenter.last);
        assertEquals(List.of(), presenter.last.getPosters());
        assertEquals(List.of(), presenter.last.getFilmIds());
        assertEquals(1, presenter.last.getPage());
        assertEquals(1, presenter.last.getTotalPages());
        assertEquals(dao.genresToReturn, presenter.last.getGenres());
    }
}
