package use_case.signup;

import entity.User;
import entity.UserFactory;

public class SignupInteractor implements SignupInputBoundary {

    private final SignupUserDataAccessInterface userDataAccessObject;
    private final SignupOutputBoundary signupPresenter;
    private final UserFactory userFactory;

    public SignupInteractor(SignupUserDataAccessInterface userDataAccessObject,
                            SignupOutputBoundary signupPresenter,
                            UserFactory userFactory) {
        this.userDataAccessObject = userDataAccessObject;
        this.signupPresenter = signupPresenter;
        this.userFactory = userFactory;
    }

    @Override
    public void execute(SignupInputData inputData) {
        String username = inputData.getUsername();
        String password = inputData.getPassword();

        // ===== REQUIRED FIELDS =====
        if (username == null || username.isEmpty()) {
            signupPresenter.prepareFailView("Username is required.");
            return;
        }

        if (password == null || password.isEmpty()) {
            signupPresenter.prepareFailView("Password is required.");
            return;
        }

        // ===== CHARACTER RESTRICTIONS =====
        if (containsDisallowedCharacters(username) || containsDisallowedCharacters(password)) {
            signupPresenter.prepareFailView(
                    "Username and password cannot contain spaces, periods '.', commas ',' or semicolons ';'."
            );
            return;
        }

        // ===== EXISTING USER CHECK =====
        if (userDataAccessObject.existsByName(username)) {
            signupPresenter.prepareFailView("User already exists.");
            return;
        }

        // ===== CREATE + SAVE USER =====
        User user = userFactory.create(username, password);
        userDataAccessObject.save(user);
        userDataAccessObject.setCurrentUsername(username);

        // Adjust constructor if your SignupOutputData is different
        SignupOutputData outputData = new SignupOutputData(username, false);
        signupPresenter.prepareSuccessView(outputData);
    }

    /**
     * Returns true if the string contains any disallowed characters:
     * space, '.', ',' or ';'.
     */
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
