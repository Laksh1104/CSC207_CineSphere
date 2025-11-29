package use_case.signup;

public class SignupOutputData {

    private final String username;
    private final boolean success;

    public SignupOutputData(String username, Boolean success) {
        this.username = username;
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public boolean isSuccess() {
        return success;
    }
}
