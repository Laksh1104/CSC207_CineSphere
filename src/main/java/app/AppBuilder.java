package app;

import data_access.FileUserDataAccessObject;
import entity.UserFactory;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;

import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;

import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginUserDataAccessInterface;

import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;

import view.LoginView;

import javax.swing.SwingUtilities;

public class AppBuilder {

    public static void main(String[] args) {

        // ==== ENTITY FACTORIES ====
        UserFactory userFactory = new UserFactory();

        // ==== DATA ACCESS ====
        FileUserDataAccessObject userDAO =
                new FileUserDataAccessObject("users.txt", userFactory);

        LoginUserDataAccessInterface loginUserDAO = userDAO;

        // ==== LOGIN WIRES ====
        LoginViewModel loginViewModel = new LoginViewModel();
        LoginPresenter loginPresenter = new LoginPresenter(loginViewModel);
        LoginInputBoundary loginInteractor =
                new LoginInteractor(loginUserDAO, loginPresenter);
        LoginController loginController = new LoginController(loginInteractor);

        // ==== SIGNUP WIRES ====
        SignupViewModel signupViewModel = new SignupViewModel();
        SignupOutputBoundary signupPresenter =
                new SignupPresenter(signupViewModel);
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
            MainAppFrame app = new MainAppFrame(loginView);

            loginPresenter.setScreenSwitchListener(app);
        });
    }
}
