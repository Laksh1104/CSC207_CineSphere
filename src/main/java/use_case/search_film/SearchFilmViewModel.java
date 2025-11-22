package use_case.search_film;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SearchFilmViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private SearchFilmState state = new SearchFilmState();

    public SearchFilmState getState() {
        return state;
    }
    public void setState(SearchFilmState newState) {
        this.state = newState;
    }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
