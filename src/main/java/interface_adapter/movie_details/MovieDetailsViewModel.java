package interface_adapter.movie_details;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MovieDetailsViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private MovieDetailsState state = null;
    private String errorMessage = null;

    public void setState(MovieDetailsState newState) {
        MovieDetailsState oldState = this.state;
        this.state = newState;
        support.firePropertyChange("state", oldState, newState);
    }

    public void setErrorMessage(String errorMessage) {
        String oldErrorMessage = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange("errorMessage", oldErrorMessage, errorMessage);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
