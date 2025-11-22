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
        SignupState newState = new SignupState();
        newState.setUsername(data.getUsername());
        newState.setSignupSuccess(true);

        signupViewModel.setState(newState);

        // reviewer requested this:
        signupViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        SignupState newState = new SignupState();
        newState.setErrorMessage(errorMessage);
        newState.setSignupSuccess(false);

        signupViewModel.setState(newState);

        // reviewer requested this:
        signupViewModel.firePropertyChanged();
    }
}
