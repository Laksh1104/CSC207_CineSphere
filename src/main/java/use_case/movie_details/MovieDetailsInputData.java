package use_case.movie_details;

public class MovieDetailsInputData {
    private final int filmId;

    public MovieDetailsInputData(int filmId) {
        this.filmId = filmId;
    }

    public int getFilmId() {
        return filmId;
    }
}
