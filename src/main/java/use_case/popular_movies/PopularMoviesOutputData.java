package use_case.popular_movies;

import java.util.List;

public class PopularMoviesOutputData {
    private final List<String> posterUrls;
    private final int page;

    public PopularMoviesOutputData(List<String> posterUrls, int page) {
        this.posterUrls = posterUrls;
        this.page = page;
    }

    public List<String> getPosterUrls() {
        return posterUrls;
    }

    public int getPage() {
        return page;
    }
}
