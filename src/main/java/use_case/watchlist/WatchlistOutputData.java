package use_case.watchlist;
import java.util.List;

public class WatchlistOutputData {
    public final List<String> moviesOnPage;

    public WatchlistOutputData(List<String> moviesOnPage) {
        this.moviesOnPage = moviesOnPage;
    }
}
