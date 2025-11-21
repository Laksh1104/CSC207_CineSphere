package use_case.signup;

public class SignupOutputData {
    private final String username;

    public SignupOutputData(String username, Boolean bool) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
