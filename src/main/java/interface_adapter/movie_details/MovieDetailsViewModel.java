package interface_adapter.movie_details;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MovieDetailsViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private MovieDetailsState state;
    private String errorMessage;

    /**
     * Sets the state and notifies listeners of the change.
     *
     * @param newState the new state to set
     */
    public void setState(MovieDetailsState newState) {
        final MovieDetailsState oldState = this.state;
        this.state = newState;
        support.firePropertyChange("state", oldState, newState);
    }

    /**
     * Sets the error message and notifies listeners of the change.
     *
     * @param errorMessage the error message to set
     */
    public void setErrorMessage(String errorMessage) {
        final String oldErrorMessage = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange("errorMessage", oldErrorMessage, errorMessage);
    }

    /**
     * Adds a property change listener to this view model.
     *
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
