import entity.Watchlist;
import interface_adapter.watchlist.watchlistController;
import interface_adapter.watchlist.watchlistViewModel;
import use_case.watchlist.*;
import view.WatchlistView;

public class TestWatchlistUI {

    public static void main(String[] args) {
        final Watchlist watchlist = new Watchlist();
        final WatchlistOutputBoundary output = new WatchlistOutputBoundary() {
            @Override
            public void present(WatchlistOutputData data) {

            }
        };
        final WatchlistInteractor inter = new WatchlistInteractor(watchlist, output);
        final watchlistController controller = new watchlistController(inter);
        final watchlistViewModel viewModel = new watchlistViewModel();
        WatchlistView view = new WatchlistView(controller, viewModel, watchlist, inter);
    }}