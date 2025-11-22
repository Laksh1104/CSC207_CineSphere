package use_case.search_film;

public class SearchFilmOutputData {
    private final int filmId;

    public SearchFilmOutputData(int filmId) {
        this.filmId = filmId;
    }

    public int getFilmId() {
        return filmId;
    }
}
