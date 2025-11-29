package use_case.watchlist;

import entity.Watchlist;

import java.util.List;

public class WatchlistInteractor implements WatchlistInputBoundary{

    private final Watchlist watchlist;
    private final WatchlistOutputBoundary presenter;

    private int currentPage = 0;
    private final int moviesPerPage = 12;

    public WatchlistInteractor(Watchlist watchlist,
                               WatchlistOutputBoundary presenter) {
        this.watchlist = watchlist;
        this.presenter = presenter;
    }

    @Override
    public void addMovie(WatchlistInputData data) {
        watchlist.add(data.movieUrl);
        loadPage();
    }

    @Override
    public List<String> loadPage() {
        List<String> all = watchlist.getMovies();

        int start = currentPage * moviesPerPage;
        int end = Math.min(start + moviesPerPage, all.size());

        List<String> sublist = all.subList(start, end);

        return sublist;
    }

    public void forward() {
        if (currentPage * moviesPerPage < watchlist.getMovies().size()) {
            currentPage++;
            loadPage();
        }
        else {
            return;
        }
    }

    public void back() {
        if (currentPage > 0) currentPage--;
        loadPage();
    }
}
