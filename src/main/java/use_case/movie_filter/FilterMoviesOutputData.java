package use_case.movie_filter;

import java.util.List;

public class FilterMoviesOutputData {
    private final List<String> posters;
    private final int page;
    private final int totalPages;

    public FilterMoviesOutputData(List<String> posters, int page, int totalPages) {
        this.posters = posters;
        this.page = page;
        this.totalPages = totalPages;
    }

    public List<String> getPosters() { return posters; }
    public int getPage() { return page; }
    public int getTotalPages() { return totalPages; }
}
