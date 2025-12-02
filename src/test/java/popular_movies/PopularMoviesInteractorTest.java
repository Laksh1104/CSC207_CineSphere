package popular_movies;

import entity.Movie;
import org.junit.jupiter.api.Test;
import use_case.popular_movies.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PopularMoviesInteractorTest {

    // Fake DAO
    private static class FakePopularMoviesDAO implements PopularMoviesDataAccessInterface {

        // when everything is normal, fake DAO would return:
        List<Movie> moviesToReturn = List.of();
        List<String> posterUrlsToReturn = List.of();

        // default: no error, so it is false here
        boolean forceError = false;

        @Override
        public List<Movie> getPopularMovies() {
            // if forceError is true, meaning we have error, throw an exception
            if (forceError) {
                throw new RuntimeException("failed to load popular movies");
            }
            return moviesToReturn;
        }

        @Override
        public List<String> getPosterUrls(List<Movie> movies) {
            return posterUrlsToReturn;
        }
    }

    // fake presenter
    private static class FakePopularMoviesPresenter implements PopularMoviesOutputBoundary {

        PopularMoviesOutputData lastOutput;
        String lastError;

        @Override
        public void present(PopularMoviesOutputData outputData) {
            lastOutput = outputData;
            lastError = null;
        }

        @Override
        public void presentError(String errorMessage) {
            this.lastError = errorMessage;
            lastOutput = null;
        }
    }

    // test the normal situation
    @Test
    void execute_success_callsPresenterWithOutputData() {
        FakePopularMoviesDAO dao = new FakePopularMoviesDAO();
        FakePopularMoviesPresenter presenter = new FakePopularMoviesPresenter();

        // return
        dao.moviesToReturn = List.of();
        dao.posterUrlsToReturn = List.of("url1", "url2");

        PopularMoviesInteractor interactor = new PopularMoviesInteractor(dao, presenter);

        PopularMoviesInputData inputData = new PopularMoviesInputData(1);

        interactor.execute(inputData);

        // assert, presenter would receive success output
        assertNull(presenter.lastError); // no error
        assertNotNull(presenter.lastOutput); // output is not null

        // return should be matched
        assertEquals(dao.posterUrlsToReturn, presenter.lastOutput.getPosterUrls()); // urls matched

        // list is empty
        assertEquals(List.of(), presenter.lastOutput.getFilmIds());

        // page should be matched
        assertEquals(1, presenter.lastOutput.getPage());

        // no need movietoreturn coz presenter does not do with it
    }

    // test the error situation
    @Test
    void execute_whenDaoThrows_callsPresenterError() {
        FakePopularMoviesDAO dao = new FakePopularMoviesDAO();
        FakePopularMoviesPresenter presenter = new FakePopularMoviesPresenter();

        // force error, so now it is true. when interactor calls .getPopularMovies() it would throw an exception
        dao.forceError = true;

        PopularMoviesInteractor interactor = new PopularMoviesInteractor(dao, presenter);

        PopularMoviesInputData inputData = new PopularMoviesInputData(1);

        interactor.execute(inputData);

        // assert, success output must be null in this situation
        assertNull(presenter.lastOutput); // error situation so no output, output is null

        // assert, error message presented
        assertNotNull(presenter.lastError); // we have error so error not null
        assertTrue(presenter.lastError.contains("failed"));
    }
}
