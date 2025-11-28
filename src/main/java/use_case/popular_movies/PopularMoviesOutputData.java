package use_case.popular_movies;

import java.util.List;

public class PopularMoviesOutputData {
    private final List<String> posterUrls;
    private final List<Integer> filmIds;
    private final int page;

    public PopularMoviesOutputData(List<String> posterUrls, List<Integer> filmIds, int page) {
        this.posterUrls = posterUrls;
        this.filmIds = filmIds;
        this.page = page;
    }

    public List<String> getPosterUrls() {
        return posterUrls;
    }

    public List<Integer> getFilmIds() { return filmIds; }

    public int getPage() {
        return page;
    }
}
