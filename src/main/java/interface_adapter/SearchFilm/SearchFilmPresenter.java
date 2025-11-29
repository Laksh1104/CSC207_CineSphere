package interface_adapter.SearchFilm;

import use_case.search_film.SearchFilmOutputBoundary;
import use_case.search_film.SearchFilmOutputData;

public class SearchFilmPresenter implements SearchFilmOutputBoundary {
    private final SearchFilmViewModel viewModel;

    public SearchFilmPresenter(SearchFilmViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(SearchFilmOutputData outputData) {
        SearchFilmState oldState = new SearchFilmState(viewModel.getState());
        SearchFilmState newState = new SearchFilmState(oldState);

        newState.setErrorMessage(null);
        newState.setFilmId(outputData.getFilmId());

        viewModel.setState(newState);
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailureView(String errorMessage) {
        SearchFilmState oldState = new SearchFilmState(viewModel.getState());
        SearchFilmState newState = new SearchFilmState(oldState);
        newState.setErrorMessage(errorMessage);
        newState.setFilmId(-1);
        viewModel.setState(newState);
        viewModel.firePropertyChanged();
    }

}
