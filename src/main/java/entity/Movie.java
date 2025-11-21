package entity;

public class Movie {

    private final int id;
    private final String title;
    private final String posterPath;

    // your custom fields (kept exactly as-is)
    private String film_name;
    private int film_id;

    // Full 3-arg constructor (TMDB)
    public Movie(int id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;

        // ensure your custom fields are assigned too
        this.film_id = id;
        this.film_name = title;
    }

    // Your factory uses a 2-arg constructor — so we must add it
    public Movie(int filmId, String filmName) {
        this.id = filmId;
        this.title = filmName;

        // no poster available from factory use-case
        this.posterPath = null;

        this.film_id = filmId;
        this.film_name = filmName;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getPosterPath() { return posterPath; }

    // your custom getters
    public String getFilmName() { return film_name; }
    public int getFilmId() { return film_id; }
}
