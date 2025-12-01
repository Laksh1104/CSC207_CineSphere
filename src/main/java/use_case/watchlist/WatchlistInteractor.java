package use_case.watchlist;

import entity.Watchlist;

import java.util.List;

public class WatchlistInteractor implements WatchlistInputBoundary{

    private final WatchlistOutputBoundary presenter;
    private final Watchlist watchlist;

    private int currentPage = 0;
    private final int moviesPerPage = 12;

    public WatchlistInteractor(WatchlistOutputBoundary presenter,  Watchlist watchlist) {
        this.presenter = presenter;
        this.watchlist = watchlist;
    }

    @Override
    public void addMovie(WatchlistInputData data) {
        watchlist.add(data.getMovieUrl());
        loadPage();
    }

    @Override
    public void removeMovie(WatchlistInputData data) {
        watchlist.remove(data.getMovieUrl());
        loadPage();
    }

    @Override
    public List<String> loadPage() {
        List<String> all = watchlist.getMovies();

        int start = currentPage * moviesPerPage;
        int end = Math.min(start + moviesPerPage, all.size());

        List<String> sublist = all.subList(start, end);
        presenter.presentWatchlistPage(new WatchlistOutputData(sublist));
        return sublist;
    }

    @Override
    public boolean isInWatchlist(String posterUrl) {
        return watchlist.getMovies().contains(posterUrl);
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


}
