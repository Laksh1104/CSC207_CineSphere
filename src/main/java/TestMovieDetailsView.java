import data_access.MovieDetailsDataAccessObject;
import entity.MovieDetails;
import view.MovieDetailsView;

public class TestMovieDetailsView {

    public static void main(String[] args) {
        final MovieDetails movieDetails = new MovieDetailsDataAccessObject().getMovieDetails(1062722);
        new MovieDetailsView(movieDetails);
    }
}
