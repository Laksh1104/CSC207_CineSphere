package watchlist;

import use_case.watchlist.WatchlistDataAccessInterface;

import java.util.*;

class InMemoryWatchlistTestDAO implements WatchlistDataAccessInterface {
    //Dummy database storing a username and a set of poster urls
    public Map<String, Set<String>> storage = new HashMap<>();

    //Adds poster url to watchlist or creates new set if it doesnt exist
    @Override
    public void addToWatchlist(String username, String posterUrl) {
        storage.putIfAbsent(username, new HashSet<>());
        storage.get(username).add(posterUrl);
    }

    //Same as above
    @Override
    public void removeFromWatchlist(String username, String posterUrl) {
        storage.putIfAbsent(username, new HashSet<>());
        storage.get(username).remove(posterUrl);
    }

    @Override
    public boolean isInWatchlist(String username, String posterUrl) {
        return storage.getOrDefault(username, Set.of()).contains(posterUrl);
    }

    @Override
    public List<String> getWatchlist(String username) {
        return new ArrayList<>(storage.getOrDefault(username, Set.of()));
    }
}

