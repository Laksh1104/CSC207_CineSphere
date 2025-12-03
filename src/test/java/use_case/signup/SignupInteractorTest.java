package use_case.signup;

import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.Test;
import use_case.login.FakeUserDAO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SignupInteractor.
 */
class SignupInteractorTest {

    @Test
    void success_signup_createsUserAndSetsCurrentUser() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        SignupInputData input = new SignupInputData("Paul", "password");

        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData outputData) {
                successCalled[0] = true;
                assertEquals("Paul", outputData.getUsername());
                // constructor currently sets success=false
                assertFalse(outputData.isSuccess());
                assertTrue(userDao.existsByName("Paul"));
                assertEquals("Paul", userDao.getCurrentUsername());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
            }
        };

        SignupInteractor interactor = new SignupInteractor(userDao, presenter, factory);
        interactor.execute(input);

        assertTrue(successCalled[0]);
        assertFalse(failCalled[0]);
    }

    @Test
    void failure_usernameRequired() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        SignupInputData input = new SignupInputData("", "password");

        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData outputData) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
                assertEquals("Username is required.", errorMessage);
                assertFalse(userDao.existsByName(""));
            }
        };

        SignupInteractor interactor = new SignupInteractor(userDao, presenter, factory);
        interactor.execute(input);

        assertFalse(successCalled[0]);
        assertTrue(failCalled[0]);
    }

    @Test
    void failure_passwordRequired() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        SignupInputData input = new SignupInputData("Paul", "");

        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData outputData) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
                assertEquals("Password is required.", errorMessage);
                assertFalse(userDao.existsByName("Paul"));
            }
        };

        SignupInteractor interactor = new SignupInteractor(userDao, presenter, factory);
        interactor.execute(input);

        assertFalse(successCalled[0]);
        assertTrue(failCalled[0]);
    }

    @Test
    void failure_disallowedCharactersInUsernameOrPassword() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        SignupInputData input = new SignupInputData("bad user", "password");

        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData outputData) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
                assertEquals(
                        "Username and password cannot contain spaces, periods '.', commas ',' or semicolons ';'.",
                        errorMessage
                );
                assertFalse(userDao.existsByName("bad user"));
            }
        };

        SignupInteractor interactor = new SignupInteractor(userDao, presenter, factory);
        interactor.execute(input);

        assertFalse(successCalled[0]);
        assertTrue(failCalled[0]);
    }

    @Test
    void failure_userAlreadyExists() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        userDao.save(new User("Paul", "existing"));

        SignupInputData input = new SignupInputData("Paul", "password");

        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData outputData) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
                assertEquals("User already exists.", errorMessage);
            }
        };

        SignupInteractor interactor = new SignupInteractor(userDao, presenter, factory);
        interactor.execute(input);

        assertFalse(successCalled[0]);
        assertTrue(failCalled[0]);
        assertTrue(userDao.existsByName("Paul"));
    }

    @Test
    void signupOutputData_gettersWork() {
        SignupOutputData data = new SignupOutputData("Alice", true);
        assertEquals("Alice", data.getUsername());
        assertTrue(data.isSuccess());
    }

    @Test
    void signupInputData_gettersWork() {
        SignupInputData data = new SignupInputData("Bob", "secret");
        assertEquals("Bob", data.getUsername());
        assertEquals("secret", data.getPassword());
    }
}
