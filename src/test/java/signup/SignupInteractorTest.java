package signup;

import entity.User;
import entity.UserFactory;
import login.FakeUserDAO;
import org.junit.jupiter.api.Test;
import use_case.signup.*;

import static org.junit.jupiter.api.Assertions.*;

class SignupInteractorTest {

    @Test
    void success_signup_createsUserAndSetsCurrentUser() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        SignupInputData input = new SignupInputData("Paul", "password");

        final boolean[] successCalled = {false};

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData outputData) {
                successCalled[0] = true;
                assertEquals("Paul", outputData.getUsername());
                assertFalse(outputData.isSuccess());
                assertTrue(userDao.existsByName("Paul"));
                assertEquals("Paul", userDao.getCurrentUsername());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Should not fail: " + errorMessage);
            }
        };

        new SignupInteractor(userDao, presenter, factory).execute(input);
        assertTrue(successCalled[0]);
    }

    @Test
    void failure_usernameNull_required() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        SignupInputData input = new SignupInputData(null, "password");

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override public void prepareSuccessView(SignupOutputData outputData) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("Username is required.", errorMessage);
            }
        };

        new SignupInteractor(userDao, presenter, factory).execute(input);
    }

    @Test
    void failure_usernameEmpty_required() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        SignupInputData input = new SignupInputData("", "password");

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override public void prepareSuccessView(SignupOutputData outputData) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("Username is required.", errorMessage);
            }
        };

        new SignupInteractor(userDao, presenter, factory).execute(input);
    }

    @Test
    void failure_passwordNull_required() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        SignupInputData input = new SignupInputData("Paul", null);

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override public void prepareSuccessView(SignupOutputData outputData) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("Password is required.", errorMessage);
            }
        };

        new SignupInteractor(userDao, presenter, factory).execute(input);
    }

    @Test
    void failure_passwordEmpty_required() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        SignupInputData input = new SignupInputData("Paul", "");

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override public void prepareSuccessView(SignupOutputData outputData) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("Password is required.", errorMessage);
            }
        };

        new SignupInteractor(userDao, presenter, factory).execute(input);
    }

    @Test
    void failure_disallowedUsername_space() { assertDisallowed("bad user", "password"); }

    @Test
    void failure_disallowedUsername_period() { assertDisallowed("bad.user", "password"); }

    @Test
    void failure_disallowedUsername_comma() { assertDisallowed("bad,user", "password"); }

    @Test
    void failure_disallowedUsername_semicolon() { assertDisallowed("bad;user", "password"); }

    @Test
    void failure_disallowedPassword_triggersPasswordBranch() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        SignupInputData input = new SignupInputData("Paul", "pass,word");

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override public void prepareSuccessView(SignupOutputData outputData) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals(
                        "Username and password cannot contain spaces, periods '.', commas ',' or semicolons ';'.",
                        errorMessage
                );
                assertFalse(userDao.existsByName("Paul"));
            }
        };

        new SignupInteractor(userDao, presenter, factory).execute(input);
    }

    @Test
    void failure_userAlreadyExists() {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        userDao.save(new User("Paul", "existing"));

        SignupInputData input = new SignupInputData("Paul", "password");

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override public void prepareSuccessView(SignupOutputData outputData) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("User already exists.", errorMessage);
                assertTrue(userDao.existsByName("Paul"));
            }
        };

        new SignupInteractor(userDao, presenter, factory).execute(input);
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

    private void assertDisallowed(String username, String password) {
        FakeUserDAO userDao = new FakeUserDAO();
        UserFactory factory = new UserFactory();

        SignupInputData input = new SignupInputData(username, password);

        SignupOutputBoundary presenter = new SignupOutputBoundary() {
            @Override public void prepareSuccessView(SignupOutputData outputData) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals(
                        "Username and password cannot contain spaces, periods '.', commas ',' or semicolons ';'.",
                        errorMessage
                );
            }
        };

        new SignupInteractor(userDao, presenter, factory).execute(input);
    }
}
