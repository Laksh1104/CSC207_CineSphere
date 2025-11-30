package use_case.movie_details;

import entity.MovieDetails;

import java.util.stream.Collectors;

public class MovieDetailsInteractor implements MovieDetailsInputBoundary {
    private final MovieDetailsDataAccessInterface dataAccess;
    private final MovieDetailsOutputBoundary presenter;

    public MovieDetailsInteractor(MovieDetailsDataAccessInterface dataAccess, 
                                MovieDetailsOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(MovieDetailsInputData inputData) {
        try {
            MovieDetails movieDetails = dataAccess.getMovieDetails(inputData.getFilmId());

            MovieDetailsOutputData outputData = new MovieDetailsOutputData(
                movieDetails.filmId(),
                movieDetails.filmName(),
                movieDetails.director(),
                movieDetails.releaseDate(),
                movieDetails.ratingOutOf5(),
                movieDetails.genres(),
                movieDetails.description(),
                movieDetails.reviews().stream()
                    .map(review -> new MovieDetailsOutputData.MovieReviewData(
                        review.author(), review.content()))
                    .collect(Collectors.toList()),
                movieDetails.posterUrl()
            );

            presenter.presentMovieDetails(outputData);
        } catch (Exception e) {
            presenter.presentError(e.getMessage());
        }
    }
}
