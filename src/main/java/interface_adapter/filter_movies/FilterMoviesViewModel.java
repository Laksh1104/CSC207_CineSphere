package interface_adapter.filter_movies;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FilterMoviesViewModel {

    private List<String> posters;
    private List<Integer> filmIds;
    private int page;
    private int totalPages;

    // NEW: genres from TMDB
    private Map<String, Integer> genres = new LinkedHashMap<>();

    public void setPosters(List<String> posters) { this.posters = posters; }
    public void setFilmIds(List<Integer> filmIds) { this.filmIds = filmIds; }
    public void setPage(int page) { this.page = page; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public List<String> getPosters() { return posters; }
    public List<Integer> getFilmIds() { return filmIds; }
    public int getPage() { return page; }
    public int getTotalPages() { return totalPages; }

    public void setGenres(Map<String, Integer> genres) {
        this.genres = (genres == null) ? new LinkedHashMap<>() : new LinkedHashMap<>(genres);
    }

    public Map<String, Integer> getGenres() {
        return genres;
    }
}
