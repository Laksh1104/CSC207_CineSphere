package use_case.movie_filter;

import java.util.List;
import java.util.Map;

public class FilterMoviesOutputData {
    private final List<String> posters;
    private final List<Integer> filmIds;
    private final int page;
    private final int totalPages;

    // NEW: all genres from TMDB (name -> id)
    private final Map<String, Integer> genres;

    public FilterMoviesOutputData(List<String> posters,
                                  List<Integer> filmIds,
                                  int page,
                                  int totalPages,
                                  Map<String, Integer> genres) {
        this.posters = posters;
        this.filmIds = filmIds;
        this.page = page;
        this.totalPages = totalPages;
        this.genres = genres;
    }

    public List<String> getPosters() { return posters; }
    public List<Integer> getFilmIds() { return filmIds; }
    public int getPage() { return page; }
    public int getTotalPages() { return totalPages; }

    public Map<String, Integer> getGenres() { return genres; }
}
