package use_case.movie_filter;

import java.util.List;

public class FilterMoviesOutputData {

    private final List<String> posters;
    private final List<Integer> filmIds;
    private final int page;
    private final int totalPages;

    public FilterMoviesOutputData(List<String> posters,
                                  List<Integer> filmIds,
                                  int page,
                                  int totalPages) {
        this.posters = posters;
        this.filmIds = filmIds;
        this.page = page;
        this.totalPages = totalPages;
    }

    public List<String> getPosters() {
        return posters;
    }

    public List<Integer> getFilmIds() {
        return filmIds;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
