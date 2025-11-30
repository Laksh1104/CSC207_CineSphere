package search_film;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import use_case.search_film.*;

class SearchFilmInteractorTest {
    static class FakeDAOSuccess implements SearchFilmDataAccessInterface {
        @Override
        public int searchFilmId(String query) {
            return 12345;
        }
    }

    static class FakeDAONotFound implements SearchFilmDataAccessInterface {
        @Override
        public int searchFilmId(String query) {
            return -1;
        }
    }

    static class FakePresenter implements SearchFilmOutputBoundary {
        String errorMessage = null;
        Integer filmId = null;

        @Override
        public void prepareSuccessView(SearchFilmOutputData data) {
            filmId = data.getFilmId();
        }

        @Override
        public void prepareFailureView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    @Test
    void testSuccess() {
        FakePresenter presenter = new FakePresenter();
        SearchFilmInteractor interactor =
                new SearchFilmInteractor(new FakeDAOSuccess(), presenter);

        interactor.execute(new SearchFilmInputData("Moana"));

        Assertions.assertEquals(12345, presenter.filmId);
    }

    @Test
    void testFilmNotFound() {
        FakePresenter presenter = new FakePresenter();
        SearchFilmInteractor interactor =
                new SearchFilmInteractor(new FakeDAONotFound(), presenter);

        interactor.execute(new SearchFilmInputData("UnknownMovie"));

        Assertions.assertEquals("Film not found", presenter.errorMessage);
    }

    @Test
    void testEmptyInput() {
        FakePresenter presenter = new FakePresenter();
        SearchFilmInteractor interactor =
                new SearchFilmInteractor(new FakeDAOSuccess(), presenter);

        interactor.execute(new SearchFilmInputData(""));

        Assertions.assertEquals("Query cannot be empty", presenter.errorMessage);
    }
}
