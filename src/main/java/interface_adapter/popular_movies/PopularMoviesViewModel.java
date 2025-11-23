package interface_adapter.popular_movies;

import java.util.List;
import java.util.ArrayList;

public class PopularMoviesViewModel {
    private List<String> posterUrls = new ArrayList<>();

    public List<String> getPosterUrls() {
        return posterUrls;
    }

    public void setPosterUrls(List<String> posterUrls) {
        this.posterUrls = posterUrls;
    }
}
