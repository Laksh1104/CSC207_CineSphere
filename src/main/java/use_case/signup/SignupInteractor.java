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

        if (username == null || username.trim().isEmpty()) {
            signupPresenter.prepareFailView("Username must not be empty.");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            signupPresenter.prepareFailView("Password must not be empty.");
            return;
        }

        if (userDataAccessObject.existsByName(username)) {
            signupPresenter.prepareFailView("User already exists.");
            return;
        }

        User user = userFactory.create(username, password);
        userDataAccessObject.save(user);
        userDataAccessObject.setCurrentUsername(username);

        SignupOutputData outputData = new SignupOutputData(username);
        signupPresenter.prepareSuccessView(outputData);
    }
}
