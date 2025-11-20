package interface_adapter.signup;

import use_case.signup.SignupOutputBoundary;
import use_case.signup.SignupOutputData;

import javax.swing.*;

public class SignupPresenter implements SignupOutputBoundary {

    private final SignupViewModel signupViewModel;

    public SignupPresenter(SignupViewModel signupViewModel) {
        this.signupViewModel = signupViewModel;
    }

    @Override
    public void prepareSuccessView(SignupOutputData outputData) {
        SignupState state = signupViewModel.getState();
        state.setUsername(outputData.getUsername());
        state.setErrorMessage(null);

        signupViewModel.setState(state);
        signupViewModel.firePropertyChanged();

        JOptionPane.showMessageDialog(
                null,
                "Account created for " + outputData.getUsername() + "!",
                "Sign up successful",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void prepareFailView(String errorMessage) {
        SignupState state = signupViewModel.getState();
        state.setErrorMessage(errorMessage);

        signupViewModel.setState(state);
        signupViewModel.firePropertyChanged();

        JOptionPane.showMessageDialog(
                null,
                errorMessage,
                "Sign up failed",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
