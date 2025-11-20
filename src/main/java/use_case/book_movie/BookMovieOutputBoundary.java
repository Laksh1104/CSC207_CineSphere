package use_case.book_movie;

/**
 * The output boundary for the Book Movie Use Case.
 */
public interface BookMovieOutputBoundary {

    /**
     * Prepares the success view for the Book Movie Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(BookMovieOutputData outputData);

    /**
     * Prepares the failure view for the Book Movie Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}