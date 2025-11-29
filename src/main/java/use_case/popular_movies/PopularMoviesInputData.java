package use_case.popular_movies;

public class PopularMoviesInputData {
    private final int page;

    public PopularMoviesInputData(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }
}
