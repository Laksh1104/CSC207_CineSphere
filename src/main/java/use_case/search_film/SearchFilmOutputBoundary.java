package use_case.search_film;

public interface SearchFilmOutputBoundary {
    void prepareSuccessView(SearchFilmOutputData outputData);
    void prepareFailureView(String errorMessage);
}
