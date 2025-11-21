package interface_adapter.login;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class LoginViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private LoginState state = new LoginState();

    public LoginState getState() {
        return state;
    }

    public void setState(LoginState newState) {
        LoginState old = this.state;
        this.state = newState;
        // tjis triggers LoginViews listener
        support.firePropertyChange("state", old, this.state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
