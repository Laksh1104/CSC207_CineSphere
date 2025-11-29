import java.util.*;

// ====================== VIEW MODEL ======================
class WatchlistViewModel {
    public String message = "";
}

// ====================== PRESENTER ======================
interface WatchlistPresenter {
    void presentAdded(String movie);
    void presentError(String error);
}

class WatchlistPresenterImpl implements WatchlistPresenter {
    private final WatchlistViewModel viewModel;

    public WatchlistPresenterImpl(WatchlistViewModel vm) {
        this.viewModel = vm;
    }

    @Override
    public void presentAdded(String movie) {
        viewModel.message = "Added to watchlist: " + movie;
    }

    @Override
    public void presentError(String error) {
        viewModel.message = "Error: " + error;
    }
}

// ====================== USE CASE ======================
interface AddToWatchlistInputBoundary {
    void execute(String movie);
}

class AddToWatchlistInteractor implements AddToWatchlistInputBoundary {
    private final WatchlistPresenter presenter;
    private final List<String> watchlist = new ArrayList<>();

    public AddToWatchlistInteractor(WatchlistPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(String movie) {
        if (movie == null || movie.isEmpty()) {
            presenter.presentError("Movie name is empty");
            return;
        }
        watchlist.add(movie);
        presenter.presentAdded(movie);
    }
}

// ====================== CONTROLLER ======================
class WatchlistController {
    private final AddToWatchlistInputBoundary interactor;

    public WatchlistController(AddToWatchlistInputBoundary i) {
        this.interactor = i;
    }

    public void addMovie(String movie) {
        interactor.execute(movie);
    }
}

// ====================== VIEW ======================
class WatchlistView {
    private final WatchlistController controller;
    private final WatchlistViewModel viewModel;

    public WatchlistView(WatchlistController c, WatchlistViewModel vm) {
        this.controller = c;
        this.viewModel = vm;
    }

    public void userClicksAdd(String movie) {
        controller.addMovie(movie);
        render();
    }

    public void render() {
        System.out.println("VIEW OUTPUT: " + viewModel.message);
    }
}

// ====================== MAIN (WIRING EVERYTHING) ======================
public class TestWatchlist {
    public static void main(String[] args) {
        WatchlistViewModel vm = new WatchlistViewModel();
        WatchlistPresenter presenter = new WatchlistPresenterImpl(vm);
        AddToWatchlistInputBoundary interactor = new AddToWatchlistInteractor(presenter);
        WatchlistController controller = new WatchlistController(interactor);
        WatchlistView view = new WatchlistView(controller, vm);

        view.userClicksAdd("Interstellar");
        view.userClicksAdd("");
        view.userClicksAdd("Inception");
    }
}
