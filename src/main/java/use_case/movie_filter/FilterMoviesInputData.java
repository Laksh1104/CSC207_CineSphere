package use_case.movie_filter;

public class FilterMoviesInputData {
    private final String year;
    private final String rating;
    private final String genre;
    private final String search;
    private final int page;

    public FilterMoviesInputData(String year, String rating, String genre, String search, int page) {
        this.year = year;
        this.rating = rating;
        this.genre = genre;
        this.search = search;
        this.page = page;
    }

    public String getYear() { return year; }
    public String getRating() { return rating; }
    public String getGenre() { return genre; }
    public String getSearch() { return search; }
    public int getPage() { return page; }
}
