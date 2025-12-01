package use_case.watchlist;

import entity.Watchlist;

import java.util.List;

public class WatchlistInteractor implements WatchlistInputBoundary{

    private final Watchlist watchlist = new Watchlist();

    private static int currentPage = 0;
    private static final int moviesPerPage = 12;

    @Override
    public void addMovie(WatchlistInputData data) {
        watchlist.add(data.movieUrl);
        loadPage();
    }

    @Override
    public void removeMovie(WatchlistInputData data) {
        watchlist.remove(data.movieUrl);
        loadPage();
    }

    @Override
    public List<String> loadPage() {
        List<String> all = watchlist.getMovies();

        int start = currentPage * moviesPerPage;
        int end = Math.min(start + moviesPerPage, all.size());

        return all.subList(start, end);
    }

    public void forward() {
        if (currentPage * moviesPerPage < watchlist.getMovies().size()) {
            currentPage++;
            loadPage();
        }
    }

    public void back() {
        if (currentPage > 0) currentPage--;
        loadPage();
    }

    public boolean isInWatchlist(String posterUrl) {
        return watchlist.getMovies().contains(posterUrl);
    }


}
