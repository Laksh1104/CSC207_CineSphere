package interface_adapter.watchlist;
import use_case.watchlist.WatchlistOutputBoundary;
import use_case.watchlist.WatchlistOutputData;


public class watchlistPresenter implements WatchlistOutputBoundary {

    private final watchlistViewModel viewModel;

    public watchlistPresenter(watchlistViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentWatchlistPage(WatchlistOutputData data) {
        viewModel.setMovies(data.getMovieUrls());
    }

}
