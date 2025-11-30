package use_case.movie_details;

import entity.MovieDetails;
import entity.MovieReview;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class MovieDetailsInteractorTest {

    @Test
    void successCase() {
        int filmId = 1;

        MovieDetails daoOutput = new MovieDetails(
                filmId,
                "filmName",
                "director",
                "releaseDate",
                5,
                List.of("genres"),
                "description",
                List.of(new MovieReview("author", "content")),
                "posterUrl"
        );

        MovieDetailsOutputData expectedOutputData = new MovieDetailsOutputData(
                daoOutput.filmId(),
                daoOutput.filmName(),
                daoOutput.director(),
                daoOutput.releaseDate(),
                daoOutput.ratingOutOf5(),
                daoOutput.genres(),
                daoOutput.description(),
                daoOutput
                        .reviews()
                        .stream()
                        .map(review -> new MovieDetailsOutputData.MovieReviewData(
                                review.author(),
                                review.content()
                        ))
                        .toList(),
                daoOutput.posterUrl()
        );

        MovieDetailsInteractor interactor = new MovieDetailsInteractor(
                new MovieDetailsDataAccessInterface() {
                    @Override
                    public MovieDetails getMovieDetails(int id) {
                        assertEquals(filmId, id);
                        return daoOutput;
                    }
                },
                new MovieDetailsOutputBoundary() {
                    @Override
                    public void presentMovieDetails(MovieDetailsOutputData outputData) {
                        assertEquals(expectedOutputData, outputData);
                    }

                    @Override
                    public void presentError(String errorMessage) {
                        fail("No error expected");
                    }
                }
        );

        interactor.execute(new MovieDetailsInputData(filmId));
    }

    @Test
    void errorCase() {
        int inputFilmId = 1;
        RuntimeException exception = new RuntimeException("error");

        MovieDetailsInteractor interactor = new MovieDetailsInteractor(
                new MovieDetailsDataAccessInterface() {
                    @Override
                    public MovieDetails getMovieDetails(int filmId) {
                        throw exception;
                    }
                },
                new MovieDetailsOutputBoundary() {
                    @Override
                    public void presentMovieDetails(MovieDetailsOutputData outputData) {
                        fail("No movie details expected");
                    }

                    @Override
                    public void presentError(String errorMessage) {
                        assertEquals(exception.getMessage(), errorMessage);
                    }
                }
        );

        interactor.execute(new MovieDetailsInputData(inputFilmId));
    }
}