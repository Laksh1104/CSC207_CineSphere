package app;

import data_access.FileUserDataAccessObject;
import entity.UserFactory;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;

import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;

import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;

import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginUserDataAccessInterface;

import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;

import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.logout.LogoutUserDataAccessInterface;

import view.LoginView;

import javax.swing.SwingUtilities;

public class AppBuilder {

    public static void main(String[] args) {

        // ==== ENTITY FACTORIES ====
        UserFactory userFactory = new UserFactory();

        // ==== DATA ACCESS ====
        FileUserDataAccessObject userDAO =
                new FileUserDataAccessObject("users.txt", userFactory);

        // For clarity on the type
        LoginUserDataAccessInterface loginGateway = userDAO;

        // ==== LOGIN WIRES ====
        LoginViewModel loginViewModel = new LoginViewModel();
        LoginPresenter loginPresenter = new LoginPresenter(loginViewModel);
        LoginInputBoundary loginInteractor = new LoginInteractor(loginGateway, loginPresenter);
        LoginController loginController = new LoginController(loginInteractor);

        // ==== SIGNUP WIRES ====
        SignupViewModel signupViewModel = new SignupViewModel();
        SignupOutputBoundary signupPresenter = new SignupPresenter(signupViewModel);
        SignupInputBoundary signupInteractor =
                new SignupInteractor(userDAO, signupPresenter, userFactory);
        SignupController signupController = new SignupController(signupInteractor);

        // ==== INITIAL VIEW (LOGIN PANEL) ====
        LoginView loginView = new LoginView(
                loginController,
                loginViewModel,
                signupController,
                signupViewModel
        );

        SwingUtilities.invokeLater(() -> {
            MainAppFrame app = new MainAppFrame(loginView, loginGateway);

            // allow login presenter to switch cards (Login -> Home)
            loginPresenter.setScreenSwitchListener(app);

            // ==== LOGOUT WIRES ====
            LogoutUserDataAccessInterface logoutGateway = userDAO;
            LogoutOutputBoundary logoutPresenter = new LogoutPresenter(app);
            LogoutInputBoundary logoutInteractor = new LogoutInteractor(logoutGateway, logoutPresenter);
            LogoutController logoutController = new LogoutController(logoutInteractor);

            app.setLogoutController(logoutController);
        });
    }
}
