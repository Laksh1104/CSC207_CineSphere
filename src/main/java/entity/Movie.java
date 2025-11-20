package entity;


public class Movie {

    private String film_name;
    private int film_id;

    public Movie(int film_id, String film_name) {
        this.film_id = film_id;
        this.film_name = film_name;
    }

    public String getFilmName() {
        return film_name;
    }
    public int getFilmId() {
        return film_id;
    }
}
