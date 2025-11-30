import javax.swing.JFrame;

import data_access.MovieDetailsDataAccessObject;
import interface_adapter.movie_details.MovieDetailsController;
import interface_adapter.movie_details.MovieDetailsPresenter;
import interface_adapter.movie_details.MovieDetailsViewModel;
import use_case.movie_details.MovieDetailsDataAccessInterface;
import use_case.movie_details.MovieDetailsInputBoundary;
import use_case.movie_details.MovieDetailsInteractor;
import use_case.movie_details.MovieDetailsOutputBoundary;
import view.MovieDetailsView;

/**
 * Test harness for the MovieDetailsView.
 */
public class TestMovieDetailsView {

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 800;
    private static final int MOVIE_ID = 1062722;

    /**
     * Main method to run the test harness.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        final MovieDetailsDataAccessInterface dataAccess = new MovieDetailsDataAccessObject();
        final MovieDetailsViewModel viewModel = new MovieDetailsViewModel();
        final MovieDetailsOutputBoundary presenter = new MovieDetailsPresenter(viewModel);
        final MovieDetailsInputBoundary interactor = new MovieDetailsInteractor(dataAccess, presenter);
        final MovieDetailsController controller = new MovieDetailsController(interactor);

        final MovieDetailsView view = new MovieDetailsView(viewModel);

        final JFrame frame = new JFrame("Movie Details Test Harness");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.add(view);
        frame.setVisible(true);

        controller.showMovieDetails(MOVIE_ID);
    }
}
