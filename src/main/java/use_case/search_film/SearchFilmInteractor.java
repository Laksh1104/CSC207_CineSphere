package use_case.search_film;

public class SearchFilmInteractor implements SearchFilmInputBoundary{

    private final SearchFilmDataAccessInterface api;
    private final SearchFilmOutputBoundary presenter;

    public SearchFilmInteractor(SearchFilmDataAccessInterface api, SearchFilmOutputBoundary presenter) {
        this.api = api;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchFilmInputData inputData) {
        String query = inputData.getQuery();

        if (query == null || query.isBlank()) {
            presenter.prepareFailureView("Query cannot be empty");
            return;
        }

        try {
            int filmId = api.searchFilmId(query);
            if (filmId == -1) {
                presenter.prepareFailureView("Film not found");
            } else {
                presenter.prepareSuccessView(new SearchFilmOutputData(filmId));
            }
        } catch (Exception e) {
            presenter.prepareFailureView("Search Failed");

        }
    }
}
