package entity;

public class MovieFactory {
    public Movie create(int filmId, String filmName) {
        return new Movie(filmId, filmName);
    }
}