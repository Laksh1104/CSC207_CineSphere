package use_case.watchlist;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryWatchlistTestDAO implements WatchlistDataAccessInterface {

    public Map<String, Set<String>> storage = new HashMap<>();

    @Override
    public void addToWatchlist(String username, String posterUrl) {
        storage.putIfAbsent(username, new HashSet<>());
        storage.get(username).add(posterUrl);
    }

    @Override
    public void removeFromWatchlist(String username, String posterUrl) {
        storage.putIfAbsent(username, new HashSet<>());
        storage.get(username).remove(posterUrl);
    }

    @Override
    public boolean isInWatchlist(String username, String posterUrl) {
        return storage.getOrDefault(username, Set.of()).contains(posterUrl);
    }

    @Override
    public List<String> getWatchlist(String username) {
        return new ArrayList<>(storage.getOrDefault(username, Set.of()));
    }
}

class WatchlistInteractorTest {

    private WatchlistOutputBoundary successPresenter(List<String> expected) {
        return new WatchlistOutputBoundary() {
            @Override
            public void present(WatchlistOutputData data) {
                assertEquals(expected, data.getPosterUrls());
            }

            @Override
            public void presentError(String error) {
                fail("Unexpected failure: " + error);
            }
        };
    }

    private WatchlistOutputBoundary failPresenter(String expectedError) {
        return new WatchlistOutputBoundary() {
            @Override
            public void present(WatchlistOutputData data) {
                fail("Unexpected success");
            }

            @Override
            public void presentError(String error) {
                assertEquals(expectedError, error);
            }
        };
    }

    // -------------------------------------------------------------------------
    // TESTS
    // -------------------------------------------------------------------------

    @Test
    void addMovieSuccess() {
        InMemoryWatchlistTestDAO dao = new InMemoryWatchlistTestDAO();
        FakeLoginDAO login = new FakeLoginDAO();
        login.setUser("alice");

        WatchlistInteractor interactor = new WatchlistInteractor(
                dao, login, successPresenter(List.of("url1"))
        );

        interactor.addMovie(new WatchlistInputData("url1"));

        assertTrue(dao.isInWatchlist("alice", "url1"));
    }

    @Test
    void addMovieBlankUrl() {
        InMemoryWatchlistTestDAO dao = new InMemoryWatchlistTestDAO();
        FakeLoginDAO login = new FakeLoginDAO();
        login.setUser("alice");

        WatchlistInteractor interactor = new WatchlistInteractor(
                dao, login, failPresenter("Poster URL is required.")
        );

        interactor.addMovie(new WatchlistInputData(" "));
    }

    @Test
    void addMovieNoUser() {
        InMemoryWatchlistTestDAO dao = new InMemoryWatchlistTestDAO();
        FakeLoginDAO login = new FakeLoginDAO(); // no user

        WatchlistInteractor interactor = new WatchlistInteractor(
                dao, login, failPresenter("No user is currently logged in.")
        );

        interactor.addMovie(new WatchlistInputData("url1"));
    }

    @Test
    void addMovieAlreadyExists() {
        InMemoryWatchlistTestDAO dao = new InMemoryWatchlistTestDAO();
        FakeLoginDAO login = new FakeLoginDAO();
        login.setUser("alice");

        dao.addToWatchlist("alice", "url1");

        WatchlistInteractor interactor = new WatchlistInteractor(
                dao, login, successPresenter(List.of("url1"))
        );

        interactor.addMovie(new WatchlistInputData("url1"));

        assertEquals(List.of("url1"), dao.getWatchlist("alice"));
    }

    @Test
    void removeMovieSuccess() {
        InMemoryWatchlistTestDAO dao = new InMemoryWatchlistTestDAO();
        FakeLoginDAO login = new FakeLoginDAO();
        login.setUser("alice");

        dao.addToWatchlist("alice", "url1");

        WatchlistInteractor interactor = new WatchlistInteractor(
                dao, login, successPresenter(List.of())
        );

        interactor.removeMovie(new WatchlistInputData("url1"));

        assertFalse(dao.isInWatchlist("alice", "url1"));
    }

    @Test
    void removeMovieNotInList() {
        InMemoryWatchlistTestDAO dao = new InMemoryWatchlistTestDAO();
        FakeLoginDAO login = new FakeLoginDAO();
        login.setUser("alice");

        WatchlistInteractor interactor = new WatchlistInteractor(
                dao, login, successPresenter(List.of())
        );

        interactor.removeMovie(new WatchlistInputData("missing"));

        assertTrue(dao.getWatchlist("alice").isEmpty());
    }

    @Test
    void removeMovieBlankUrl() {
        InMemoryWatchlistTestDAO dao = new InMemoryWatchlistTestDAO();
        FakeLoginDAO login = new FakeLoginDAO();
        login.setUser("alice");

        WatchlistInteractor interactor = new WatchlistInteractor(
                dao, login, failPresenter("Poster URL is required.")
        );

        interactor.removeMovie(new WatchlistInputData(" "));
    }

    @Test
    void removeMovieNoUser() {
        InMemoryWatchlistTestDAO dao = new InMemoryWatchlistTestDAO();
        FakeLoginDAO login = new FakeLoginDAO(); // no user

        WatchlistInteractor interactor = new WatchlistInteractor(
                dao, login, failPresenter("No user is currently logged in.")
        );

        interactor.removeMovie(new WatchlistInputData("url1"));
    }

    @Test
    void loadWatchlistSuccess() {
        InMemoryWatchlistTestDAO dao = new InMemoryWatchlistTestDAO();
        FakeLoginDAO login = new FakeLoginDAO();
        login.setUser("alice");

        dao.addToWatchlist("alice", "x");
        dao.addToWatchlist("alice", "y");

        WatchlistInteractor interactor = new WatchlistInteractor(
                dao, login, successPresenter(List.of("x", "y"))
        );

        interactor.loadWatchlist();
    }

    @Test
    void loadWatchlistNoUser() {
        InMemoryWatchlistTestDAO dao = new InMemoryWatchlistTestDAO();
        FakeLoginDAO login = new FakeLoginDAO();

        WatchlistInteractor interactor = new WatchlistInteractor(
                dao, login, failPresenter("No user is currently logged in.")
        );

        interactor.loadWatchlist();
    }
}
