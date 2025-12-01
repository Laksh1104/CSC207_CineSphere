package interface_adapter.signup;

public class SignupState {

    private String username;
    private String errorMessage;
    private boolean signupSuccess;

    public SignupState() {}

    public SignupState(SignupState copy) {
        this.username = copy.username;
        this.errorMessage = copy.errorMessage;
        this.signupSuccess = copy.signupSuccess;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSignupSuccess() {
        return signupSuccess;
    }

    public void setSignupSuccess(boolean signupSuccess) {
        this.signupSuccess = signupSuccess;
    }
}
