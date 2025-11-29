package entity;

public class Movie {

    private final int id;
    private final String title;
    private final String posterPath;



    // Full 3-arg constructor (TMDB)
    public Movie(int id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
    }


    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getPosterPath() { return posterPath; }

}
