package use_case.movie_details;

/**
 * Input boundary interface for the movie details use case.
 * Defines the contract for executing movie details retrieval operations.
 */
public interface MovieDetailsInputBoundary {

    /**
     * Executes the movie details use case with the provided input data.
     * 
     * @param inputData the input data needed to retrieve movie details
     */
    void execute(MovieDetailsInputData inputData);
}
