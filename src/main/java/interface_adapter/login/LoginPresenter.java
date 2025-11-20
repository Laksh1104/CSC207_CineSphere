package interface_adapter.login;

import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;
import view.LoggedInView;
import view.LoginView;

import javax.swing.*;
import java.awt.*;

public class LoginPresenter implements LoginOutputBoundary {

    private final LoginViewModel loginViewModel;

    public LoginPresenter(LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareFailView(String errorMessage) {
        LoginState state = loginViewModel.getState();
        state.setErrorMessage(errorMessage);
        loginViewModel.setState(state);

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                errorMessage,
                "Login failed",
                JOptionPane.ERROR_MESSAGE
        ));
    }

    @Override
    public void prepareSuccessView(LoginOutputData data) {
        // clear previous error
        LoginState state = loginViewModel.getState();
        state.setErrorMessage(null);
        loginViewModel.setState(state);

        SwingUtilities.invokeLater(() -> {
            // open the home screen
            LoggedInView home = new LoggedInView(data.getUsername());
            home.setVisible(true);

            // close any open LoginView windows so you can't spawn more
            for (Window window : Window.getWindows()) {
                if (window instanceof LoginView) {
                    window.dispose();
                }
            }
        });
    }
}
