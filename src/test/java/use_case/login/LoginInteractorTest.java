package use_case.login;

import entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoginInteractor.
 */
class LoginInteractorTest {

    @Test
    void success_validCredentials_logsInAndSetsCurrentUser() {
        FakeUserDAO userDao = new FakeUserDAO();
        userDao.save(new User("Paul", "password"));

        LoginInputData input = new LoginInputData("Paul", "password");

        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData data) {
                successCalled[0] = true;
                assertEquals("Paul", data.getUsername());
                assertEquals("Paul", userDao.getCurrentUsername());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
            }
        };

        LoginInteractor interactor = new LoginInteractor(userDao, presenter);
        interactor.execute(input);

        assertTrue(successCalled[0]);
        assertFalse(failCalled[0]);
    }

    @Test
    void failure_missingUsernameOrPassword() {
        FakeUserDAO userDao = new FakeUserDAO();
        LoginInputData input = new LoginInputData("", "password");

        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData data) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
                assertEquals("Username and password are required.", errorMessage);
                assertNull(userDao.getCurrentUsername());
            }
        };

        LoginInteractor interactor = new LoginInteractor(userDao, presenter);
        interactor.execute(input);

        assertFalse(successCalled[0]);
        assertTrue(failCalled[0]);
    }

    @Test
    void failure_disallowedCharactersInUsernameOrPassword() {
        FakeUserDAO userDao = new FakeUserDAO();
        LoginInputData input = new LoginInputData("bad user", "password");

        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData data) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
                assertEquals(
                        "Username and password cannot contain spaces, periods '.', commas ',' or semicolons ';'.",
                        errorMessage
                );
            }
        };

        LoginInteractor interactor = new LoginInteractor(userDao, presenter);
        interactor.execute(input);

        assertFalse(successCalled[0]);
        assertTrue(failCalled[0]);
    }

    @Test
    void failure_userDoesNotExist() {
        FakeUserDAO userDao = new FakeUserDAO();
        LoginInputData input = new LoginInputData("Paul", "password");

        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData data) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
                assertEquals("User does not exist.", errorMessage);
            }
        };

        LoginInteractor interactor = new LoginInteractor(userDao, presenter);
        interactor.execute(input);

        assertFalse(successCalled[0]);
        assertTrue(failCalled[0]);
        assertNull(userDao.getCurrentUsername());
    }

    @Test
    void failure_incorrectPassword() {
        FakeUserDAO userDao = new FakeUserDAO();
        userDao.save(new User("Paul", "correct"));

        LoginInputData input = new LoginInputData("Paul", "wrong");

        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData data) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
                assertEquals("Incorrect password.", errorMessage);
                assertNull(userDao.getCurrentUsername());
            }
        };

        LoginInteractor interactor = new LoginInteractor(userDao, presenter);
        interactor.execute(input);

        assertFalse(successCalled[0]);
        assertTrue(failCalled[0]);
    }

    @Test
    void loginOutputData_getterWorks() {
        LoginOutputData data = new LoginOutputData("Alice");
        assertEquals("Alice", data.getUsername());
    }

    @Test
    void loginInputData_gettersWork() {
        LoginInputData data = new LoginInputData("Bob", "secret");
        assertEquals("Bob", data.getUsername());
        assertEquals("secret", data.getPassword());
    }
}
