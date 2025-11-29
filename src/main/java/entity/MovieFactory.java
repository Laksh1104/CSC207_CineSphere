package entity;

public class MovieFactory {
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    // Create Movie from MovieGlu API (no poster)
    public Movie fromMovieGlu(int filmId, String filmName) {
        return new Movie(filmId, filmName, null);
    }

    // Create Movie from TMDB API
    public Movie fromTMDB(int id, String title, String posterPath) {
        String fullPosterUrl = (posterPath == null)
                ? null
                : IMAGE_BASE_URL + posterPath;

        return new Movie(id, title, fullPosterUrl);
    }
}