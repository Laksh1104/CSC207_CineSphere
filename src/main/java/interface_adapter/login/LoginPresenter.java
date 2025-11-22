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
        LoginState oldState = new LoginState(loginViewModel.getState());
        LoginState newState = new LoginState(oldState);
        newState.setErrorMessage(errorMessage);
        newState.setLoggedIn(false);

        loginViewModel.setState(newState);

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                errorMessage,
                "Login failed",
                JOptionPane.ERROR_MESSAGE
        ));
    }

    @Override
    public void prepareSuccessView(LoginOutputData data) {
        LoginState oldState = new LoginState(loginViewModel.getState());
        LoginState newState = new LoginState(oldState);
        newState.setErrorMessage(null);
        newState.setUsername(data.getUsername());
        newState.setLoggedIn(true);

        loginViewModel.setState(newState);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CineSphere - Home");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 800);
            frame.setLocationRelativeTo(null);

            frame.setContentPane(new LoggedInView());
            frame.setVisible(true);

            for (Window window : Window.getWindows()) {
                if (window instanceof LoginView) {
                    window.dispose();
                }
            }
        });
    }
}
