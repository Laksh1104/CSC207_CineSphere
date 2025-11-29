package use_case.movie_details;

/**
 * Output boundary interface for the movie details use case.
 * Defines the contract for presenting movie details results and handling errors.
 */
public interface MovieDetailsOutputBoundary {

    /**
     * Presents the successfully retrieved movie details to the user interface.
     * 
     * @param outputData the output data containing the movie details
     */
    void presentMovieDetails(MovieDetailsOutputData outputData);

    /**
     * Presents an error message when the movie details retrieval fails.
     * 
     * @param errorMessage an error message
     */
    void presentError(String errorMessage);
}
