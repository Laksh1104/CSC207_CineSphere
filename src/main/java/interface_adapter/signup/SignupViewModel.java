package interface_adapter.signup;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SignupViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private SignupState state = new SignupState();

    public SignupState getState() {
        return state;
    }

    public void setState(SignupState state) {
        this.state = state;
    }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
