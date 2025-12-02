package interface_adapter.filter_movies;

/**
 * Simple DTO for passing filter settings from LoggedInView to FilteredView.
 */
public class FilterCriteria {

    private final String year;
    private final String rating;
    private final String genreName;
    private final String search;

    public FilterCriteria(String year, String rating, String genreName, String search) {
        this.year = year;
        this.rating = rating;
        this.genreName = genreName;
        this.search = search;
    }

    public String getYear() {
        return year;
    }

    public String getRating() {
        return rating;
    }

    public String getGenreName() {
        return genreName;
    }

    public String getSearch() {
        return search;
    }
}
