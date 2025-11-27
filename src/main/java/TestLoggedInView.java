import data_access.PopularMoviesDataAccessObject;
import interface_adapter.popular_movies.PopularMoviesController;
import interface_adapter.popular_movies.PopularMoviesPresenter;
import interface_adapter.popular_movies.PopularMoviesViewModel;
import use_case.popular_movies.PopularMoviesInputBoundary;
import use_case.popular_movies.PopularMoviesInteractor;
import use_case.popular_movies.PopularMoviesOutputBoundary;
import use_case.search_film.*;
import view.LoggedInView;
import javax.swing.*;

public class TestLoggedInView {
    public static void main(String[] args) {

        // Create the LoggedInView panel
        LoggedInView loggedInView = new LoggedInView();

        SearchFilmDataAccessInterface api = new SearchFilmAPIAccess();
        SearchFilmViewModel searchFilmViewModel = new SearchFilmViewModel();
        SearchFilmOutputBoundary searchFilmPresenter = new SearchFilmPresenter(searchFilmViewModel);
        SearchFilmInputBoundary searchFilmInteractor = new SearchFilmInteractor(api, searchFilmPresenter);
        SearchFilmController searchFilmController = new SearchFilmController(searchFilmInteractor);

        loggedInView.setSearchDependencies(searchFilmController, searchFilmViewModel);

        String bearerToken = "Bearer YOUR_TMDB_TOKEN_HERE";

        PopularMoviesDataAccessObject popularMoviesDao = new PopularMoviesDataAccessObject(bearerToken);

        PopularMoviesViewModel popularMoviesViewModel = new PopularMoviesViewModel();
        PopularMoviesOutputBoundary popularMoviesPresenter = new PopularMoviesPresenter(popularMoviesViewModel);
        PopularMoviesInputBoundary popularMoviesInteractor = new PopularMoviesInteractor(popularMoviesDao, popularMoviesPresenter);
        PopularMoviesController popularMoviesController = new PopularMoviesController(popularMoviesInteractor);

        loggedInView.setPopularMoviesDependencies(popularMoviesController, popularMoviesViewModel);


        // Create window
        JFrame frame = new JFrame("LoggedInView Test Harness");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);

        // Add the view into the frame
        frame.setContentPane(loggedInView);

        // Show the window
        frame.setVisible(true);
    }
}
