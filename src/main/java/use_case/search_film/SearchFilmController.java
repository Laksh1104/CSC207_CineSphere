package use_case.search_film;

import use_case.search_film.SearchFilmInputBoundary;
import use_case.search_film.SearchFilmInputData;

public class SearchFilmController {

    private final SearchFilmInputBoundary interactor;

    public SearchFilmController(SearchFilmInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String query) {
        interactor.execute(new SearchFilmInputData(query));
    }
}
