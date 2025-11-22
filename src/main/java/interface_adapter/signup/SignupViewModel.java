package interface_adapter.signup;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SignupViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private SignupState state = new SignupState();

    public SignupState getState() {
        return state;
    }

    public void setState(SignupState newState) {
        SignupState oldState = this.state;
        this.state = newState;
        support.firePropertyChange("state", oldState, newState);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void firePropertyChanged() {
        SignupState oldState = this.state;
        SignupState newState = new SignupState(oldState);
        support.firePropertyChange("state", oldState, newState);
    }

}
