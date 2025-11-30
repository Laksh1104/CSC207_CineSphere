package interface_adapter.login;

import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutUserDataAccessInterface;
import view.LoggedInView;
import view.LoginView;

import javax.swing.*;
import java.awt.*;

public class LoginPresenter implements LoginOutputBoundary {

    private final LoginViewModel loginViewModel;
    private final LoginUserDataAccessInterface userGateway;

    public LoginPresenter(LoginViewModel loginViewModel, LoginUserDataAccessInterface userGateway) {
        this.loginViewModel = loginViewModel;
        this.userGateway = userGateway;
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

            LoggedInView home = new LoggedInView();

            // ===== LOGOUT WIRING =====
            LogoutUserDataAccessInterface logoutGateway = (LogoutUserDataAccessInterface) userGateway;
            LogoutPresenter logoutPresenter = new LogoutPresenter(userGateway, frame::dispose);
            LogoutInputBoundary logoutInteractor = new LogoutInteractor(logoutGateway, logoutPresenter);
            LogoutController logoutController = new LogoutController(logoutInteractor);
            home.setLogoutDependencies(logoutController);
            // =========================

            frame.setContentPane(home);
            frame.setVisible(true);

            for (Window window : Window.getWindows()) {
                if (window instanceof LoginView) {
                    window.dispose();
                }
            }
        });
    }
}
