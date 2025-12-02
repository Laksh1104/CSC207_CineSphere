package interface_adapter.watchlist;

import use_case.watchlist.WatchlistOutputBoundary;
import use_case.watchlist.WatchlistOutputData;

import java.util.ArrayList;

/**
 * Presenter for the Watchlist use case.
 *
 * Translates OutputData into WatchlistState for the ViewModel.
 */
public class WatchlistPresenter implements WatchlistOutputBoundary {

    private final WatchlistViewModel viewModel;

    public WatchlistPresenter(WatchlistViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(WatchlistOutputData data) {
        WatchlistState newState = new WatchlistState();
        newState.setPosterUrls(new ArrayList<>(data.getPosterUrls()));
        newState.setErrorMessage(null);

        viewModel.setState(newState);
    }

    @Override
    public void presentError(String errorMessage) {
        WatchlistState old = viewModel.getState();
        WatchlistState newState = new WatchlistState();
        newState.setPosterUrls(old.getPosterUrls());
        newState.setErrorMessage(errorMessage);

        viewModel.setState(newState);
    }
}
