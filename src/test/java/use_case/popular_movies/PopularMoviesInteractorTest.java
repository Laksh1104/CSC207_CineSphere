package use_case.popular_movies;

import entity.Movie;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PopularMoviesInteractorTest {

    // Fake DAO
    private static class FakePopularMoviesDAO implements PopularMoviesDataAccessInterface {

        List<Movie> moviesToReturn = List.of();
        List<String> posterUrlsToReturn = List.of();
        boolean throwOnGetPopularMovies = false;
        List<Movie> lastMoviesPassedToGetPosterUrls;

        @Override
        public List<Movie> getPopularMovies() {
            if (throwOnGetPopularMovies) {
                throw new RuntimeException("boom");
            }
            return moviesToReturn;
        }

        @Override
        public List<String> getPosterUrls(List<Movie> movies) {
            lastMoviesPassedToGetPosterUrls = movies;
            return posterUrlsToReturn;
        }
    }

    private static class FakePopularMoviesPresenter implements PopularMoviesOutputBoundary {

        PopularMoviesOutputData lastOutput;
        String lastError;

        @Override
        public void present(PopularMoviesOutputData outputData) {
            this.lastOutput = outputData;
        }

        @Override
        public void presentError(String errorMessage) {
            this.lastError = errorMessage;
        }
    }

    @Test
    void execute_success_callsPresenterWithOutputData() {
        FakePopularMoviesDAO dao = new FakePopularMoviesDAO();
        FakePopularMoviesPresenter presenter = new FakePopularMoviesPresenter();

        dao.moviesToReturn = List.of();
        dao.posterUrlsToReturn = List.of("url1", "url2");

        PopularMoviesInteractor interactor =
                new PopularMoviesInteractor(dao, presenter);

        PopularMoviesInputData inputData = new PopularMoviesInputData(3);

        interactor.execute(inputData);

        // Assert
        assertNull(presenter.lastError, "No error should be presented");
        assertNotNull(presenter.lastOutput, "Output data should be presented");

        assertEquals(dao.posterUrlsToReturn, presenter.lastOutput.getPosterUrls());

        assertEquals(List.of(), presenter.lastOutput.getFilmIds());

        assertEquals(3, presenter.lastOutput.getPage());

        assertSame(dao.moviesToReturn, dao.lastMoviesPassedToGetPosterUrls);
    }

    @Test
    void execute_whenDaoThrows_callsPresenterError() {

        FakePopularMoviesDAO dao = new FakePopularMoviesDAO();
        dao.throwOnGetPopularMovies = true;

        FakePopularMoviesPresenter presenter = new FakePopularMoviesPresenter();

        PopularMoviesInteractor interactor =
                new PopularMoviesInteractor(dao, presenter);

        PopularMoviesInputData inputData = new PopularMoviesInputData(1);

        interactor.execute(inputData);

        // Assert
        assertNull(presenter.lastOutput, "When error happens, no normal output should be presented");
        assertNotNull(presenter.lastError, "Error message should be presented");
        assertEquals("boom", presenter.lastError);
    }
}
