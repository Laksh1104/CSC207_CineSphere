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
