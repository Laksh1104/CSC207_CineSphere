import data_access.PopularMoviesDataAccessObject;
import data_access.SearchFilmDataAccessObject;
import interface_adapter.SearchFilm.*;
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

        SearchFilmDataAccessInterface api = new SearchFilmDataAccessObject();
        SearchFilmViewModel searchFilmViewModel = new SearchFilmViewModel();
        SearchFilmOutputBoundary searchFilmPresenter = new SearchFilmPresenter(searchFilmViewModel);
        SearchFilmInputBoundary searchFilmInteractor = new SearchFilmInteractor(api, searchFilmPresenter);
        SearchFilmController searchFilmController = new SearchFilmController(searchFilmInteractor);

        loggedInView.setSearchDependencies(searchFilmController, searchFilmViewModel);

        String bearerToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0ZjIxZjdhNzBjNjE4ZWVlN2Q2ZDY4NDU0ZDhkN2M4MyIsIm5iZiI6MTc2MDAyMjcwMC43MTIsInN1YiI6IjY4ZTdkMGFjYzA4MTNjMzM3NmFhMTRiNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.xmj85hm4wS20IGD5DQoA9jR_APNoutqa7sd9TZ-xsiw";

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
