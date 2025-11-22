package use_case.login;

import entity.User;

/**
 * Application business rules for logging a user in.
 */
public class LoginInteractor implements LoginInputBoundary {

    private final LoginUserDataAccessInterface userDataAccess;
    private final LoginOutputBoundary presenter;

    public LoginInteractor(LoginUserDataAccessInterface userDataAccess,
                           LoginOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(LoginInputData inputData) {
        String username = inputData.getUsername();
        String password = inputData.getPassword();

        // 1. Missing fields
        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty()) {
            presenter.prepareFailView("Username and password are required.");
            return;
        }

        // 2. User exists?
        if (!userDataAccess.existsByName(username)) {
            presenter.prepareFailView("User '" + username + "' does not exist.");
            return;
        }

        // 3. Password correct?
        User user = userDataAccess.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            presenter.prepareFailView("Incorrect password.");
            return;
        }

        // 4. Success -> mark current user + tell presenter
        userDataAccess.setCurrentUsername(username);

        // 🔴 FIX: presenter expects LoginOutputData, not a String
        LoginOutputData outputData = new LoginOutputData(username);
        presenter.prepareSuccessView(outputData);
    }
}
