package interface_adapter.login;

import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;
import view.ScreenSwitchListener;

public class LoginPresenter implements LoginOutputBoundary {

    private final LoginViewModel loginViewModel;
    private ScreenSwitchListener screenSwitchListener;

    public LoginPresenter(LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
    }

    public void setScreenSwitchListener(ScreenSwitchListener listener) {
        this.screenSwitchListener = listener;
    }

    @Override
    public void prepareFailView(String errorMessage) {
        LoginState oldState = new LoginState(loginViewModel.getState());
        LoginState newState = new LoginState(oldState);

        newState.setErrorMessage(errorMessage);
        newState.setLoggedIn(false);

        loginViewModel.setState(newState);
    }

    @Override
    public void prepareSuccessView(LoginOutputData data) {
        LoginState oldState = new LoginState(loginViewModel.getState());
        LoginState newState = new LoginState(oldState);

        newState.setErrorMessage(null);
        newState.setUsername(data.getUsername());
        newState.setLoggedIn(true);

        loginViewModel.setState(newState);

        if (screenSwitchListener != null) {
            screenSwitchListener.onSwitchScreen("Home");
        }
    }
}
