package interface_adapter.filter_movies;

import java.util.List;

public class FilterMoviesViewModel {

    private List<String> posters;
    private int page;
    private int totalPages;

    public void setPosters(List<String> posters) { this.posters = posters; }
    public void setPage(int page) { this.page = page; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public List<String> getPosters() { return posters; }
    public int getPage() { return page; }
    public int getTotalPages() { return totalPages; }
}
