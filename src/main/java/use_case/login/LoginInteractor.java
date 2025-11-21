package use_case.login;

import entity.User;

public class LoginInteractor implements LoginInputBoundary {

    private final LoginUserDataAccessInterface userDataAccessObject;
    private final LoginOutputBoundary loginPresenter;

    public LoginInteractor(LoginUserDataAccessInterface userDataAccessObject,
                           LoginOutputBoundary loginPresenter) {
        this.userDataAccessObject = userDataAccessObject;
        this.loginPresenter = loginPresenter;
    }

    @Override
    public void execute(LoginInputData inputData) {
        String username = inputData.getUsername();
        String password = inputData.getPassword();

        // ===== REQUIRED FIELDS =====
        if (username == null || username.isEmpty()
                || password == null || password.isEmpty()) {
            loginPresenter.prepareFailView("Username and password are required.");
            return;
        }

        // ===== CHARACTER RESTRICTIONS =====
        if (containsDisallowedCharacters(username) || containsDisallowedCharacters(password)) {
            loginPresenter.prepareFailView(
                    "Username and password cannot contain spaces, periods '.', commas ',' or semicolons ';'."
            );
            return;
        }

        // ===== USER EXISTS? =====
        if (!userDataAccessObject.existsByName(username)) {
            loginPresenter.prepareFailView("User does not exist.");
            return;
        }

        User user = userDataAccessObject.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            loginPresenter.prepareFailView("Incorrect password.");
            return;
        }

        userDataAccessObject.setCurrentUsername(username);

        // Adjust constructor if your LoginOutputData is different
        LoginOutputData outputData = new LoginOutputData(username, false);
        loginPresenter.prepareSuccessView(outputData);
    }

    private boolean containsDisallowedCharacters(String s) {
        if (s == null) return false;
        for (char c : s.toCharArray()) {
            if (c == ' ' || c == '.' || c == ',' || c == ';') {
                return true;
            }
        }
        return false;
    }
}
