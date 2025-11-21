package interface_adapter.signup;

import use_case.signup.SignupOutputBoundary;
import use_case.signup.SignupOutputData;

public class SignupPresenter implements SignupOutputBoundary {

    private final SignupViewModel signupViewModel;

    public SignupPresenter(SignupViewModel signupViewModel) {
        this.signupViewModel = signupViewModel;
    }

    @Override
    public void prepareSuccessView(SignupOutputData data) {
        SignupState oldState = new SignupState(signupViewModel.getState());
        SignupState newState = new SignupState(oldState);

        newState.setErrorMessage(null);
        newState.setSignupSuccess(true);
        newState.setUsername(data.getUsername());

        signupViewModel.setState(newState);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        SignupState oldState = new SignupState(signupViewModel.getState());
        SignupState newState = new SignupState(oldState);

        newState.setErrorMessage(errorMessage);
        newState.setSignupSuccess(false);

        signupViewModel.setState(newState);
    }
}
