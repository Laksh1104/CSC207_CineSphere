import data_access.TmdbMovieDataAccessObject;
import interface_adapter.filter_movies.*;

import use_case.movie_filter.*;

import view.FilteredView;

import javax.swing.*;

public class TestFilteredView {

    public static void main(String[] args) {
        final String API_KEY = "4f21f7a70c618eee7d6d68454d8d7c83";
        FilterMoviesViewModel viewModel = new FilterMoviesViewModel();


        FilterMoviesDataAccessInterface api = new TmdbMovieDataAccessObject(API_KEY);

        FilterMoviesOutputBoundary presenter = new FilterMoviesPresenter(viewModel);

        FilterMoviesInputBoundary interactor = new FilterMoviesInteractor(api, presenter);

        FilterMoviesController controller = new FilterMoviesController(interactor);

        SwingUtilities.invokeLater(() -> {
            FilteredView fv = new FilteredView(controller, viewModel);
            fv.setVisible(true);
        });
    }
}
