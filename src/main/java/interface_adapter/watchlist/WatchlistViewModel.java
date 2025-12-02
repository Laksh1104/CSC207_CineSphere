package interface_adapter.watchlist;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * ViewModel for the Watchlist view.
 *
 * Holds the WatchlistState and notifies listeners on change.
 */
public class WatchlistViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private WatchlistState state = new WatchlistState();

    public WatchlistState getState() {
        return state;
    }

    public void setState(WatchlistState state) {
        WatchlistState old = this.state;
        this.state = state;
        support.firePropertyChange("state", old, this.state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    // Convenience getters used by views/controllers:

    public List<String> getPosterUrls() {
        return state.getPosterUrls();
    }

    public String getErrorMessage() {
        return state.getErrorMessage();
    }

    public boolean isInWatchlist(String posterUrl) {
        List<String> urls = state.getPosterUrls();
        return urls != null && urls.contains(posterUrl);
    }
}
