package use_case.search_film;

public class SearchFilmState {
    private Integer filmId;
    private String errorMessage;
    private String query;

    public SearchFilmState() {}

    public SearchFilmState(SearchFilmState copy) {
        this.filmId = copy.filmId;
        this.errorMessage = copy.errorMessage;
        this.query = copy.query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
