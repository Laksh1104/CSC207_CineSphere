package interface_adapter.logout;

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
import use_case.signup.SignupUserDataAccessInterface;
import use_case.logout.LogoutOutputBoundary;
import view.LoginView;

import javax.swing.*;

public class LogoutPresenter implements LogoutOutputBoundary {

    private final LoginUserDataAccessInterface loginGateway;
    private final Runnable closeCurrentWindow;

    public LogoutPresenter(LoginUserDataAccessInterface loginGateway,
                           Runnable closeCurrentWindow) {
        this.loginGateway = loginGateway;
        this.closeCurrentWindow = closeCurrentWindow;
    }

    @Override
    public void prepareSuccessView() {
        SwingUtilities.invokeLater(() -> {
            if (closeCurrentWindow != null) closeCurrentWindow.run();

            LoginViewModel lvm = new LoginViewModel();
            LoginPresenter lp = new LoginPresenter(lvm, loginGateway);
            LoginInputBoundary li = new LoginInteractor(loginGateway, lp);
            LoginController lc = new LoginController(li);

            SignupViewModel svm = new SignupViewModel();
            SignupOutputBoundary sp = new SignupPresenter(svm);

            // same DAO implements SignupUserDataAccessInterface too
            SignupUserDataAccessInterface signupGateway =
                    (SignupUserDataAccessInterface) loginGateway;

            UserFactory userFactory = new UserFactory();
            SignupInputBoundary si = new SignupInteractor(signupGateway, sp, userFactory);
            SignupController sc = new SignupController(si);

            LoginView loginView = new LoginView(lc, lvm, sc, svm);
            loginView.setVisible(true);
        });
    }

    @Override
    public void prepareFailView(String errorMessage) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                errorMessage,
                "Logout failed",
                JOptionPane.ERROR_MESSAGE
        ));
    }
}
