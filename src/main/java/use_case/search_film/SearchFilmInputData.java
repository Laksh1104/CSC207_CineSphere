package use_case.search_film;

public class SearchFilmInputData {
    private final String query;

    public SearchFilmInputData(String query) {
        this.query = query;
    }
    public String getQuery() {
        return query;
    }
}
