package login;

import entity.User;
import org.junit.jupiter.api.Test;
import use_case.login.*;

import static org.junit.jupiter.api.Assertions.*;

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
    void failure_usernameNull_requiredFields() {
        FakeUserDAO userDao = new FakeUserDAO();
        LoginInputData input = new LoginInputData(null, "password");

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override public void prepareSuccessView(LoginOutputData data) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("Username and password are required.", errorMessage);
                assertNull(userDao.getCurrentUsername());
            }
        };

        new LoginInteractor(userDao, presenter).execute(input);
    }

    @Test
    void failure_passwordNull_requiredFields() {
        FakeUserDAO userDao = new FakeUserDAO();
        LoginInputData input = new LoginInputData("Paul", null);

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override public void prepareSuccessView(LoginOutputData data) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("Username and password are required.", errorMessage);
                assertNull(userDao.getCurrentUsername());
            }
        };

        new LoginInteractor(userDao, presenter).execute(input);
    }

    @Test
    void failure_usernameEmpty_requiredFields() {
        FakeUserDAO userDao = new FakeUserDAO();
        LoginInputData input = new LoginInputData("", "password");

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override public void prepareSuccessView(LoginOutputData data) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("Username and password are required.", errorMessage);
                assertNull(userDao.getCurrentUsername());
            }
        };

        new LoginInteractor(userDao, presenter).execute(input);
    }

    @Test
    void failure_passwordEmpty_requiredFields() {
        FakeUserDAO userDao = new FakeUserDAO();
        LoginInputData input = new LoginInputData("Paul", "");

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override public void prepareSuccessView(LoginOutputData data) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("Username and password are required.", errorMessage);
                assertNull(userDao.getCurrentUsername());
            }
        };

        new LoginInteractor(userDao, presenter).execute(input);
    }

    @Test
    void failure_disallowedUsername_space() {
        assertDisallowed("bad user", "password");
    }

    @Test
    void failure_disallowedUsername_period() {
        assertDisallowed("bad.user", "password");
    }

    @Test
    void failure_disallowedUsername_comma() {
        assertDisallowed("bad,user", "password");
    }

    @Test
    void failure_disallowedUsername_semicolon() {
        assertDisallowed("bad;user", "password");
    }

    @Test
    void failure_disallowedPassword_triggersPasswordBranch() {
        // username is clean, so we force evaluation of containsDisallowedCharacters(password)
        FakeUserDAO userDao = new FakeUserDAO();
        userDao.save(new User("Paul", "pass;word"));

        LoginInputData input = new LoginInputData("Paul", "pass;word");

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override public void prepareSuccessView(LoginOutputData data) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals(
                        "Username and password cannot contain spaces, periods '.', commas ',' or semicolons ';'.",
                        errorMessage
                );
                assertNull(userDao.getCurrentUsername());
            }
        };

        new LoginInteractor(userDao, presenter).execute(input);
    }

    @Test
    void failure_userDoesNotExist() {
        FakeUserDAO userDao = new FakeUserDAO();
        LoginInputData input = new LoginInputData("Paul", "password");

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override public void prepareSuccessView(LoginOutputData data) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("User does not exist.", errorMessage);
                assertNull(userDao.getCurrentUsername());
            }
        };

        new LoginInteractor(userDao, presenter).execute(input);
    }

    @Test
    void failure_userExistsButDaoReturnsNull_coversUserNullBranch() {
        LoginUserDataAccessInterface weirdDao = new LoginUserDataAccessInterface() {
            private String current;
            @Override public boolean existsByName(String username) { return true; }
            @Override public User get(String username) { return null; }
            @Override public void setCurrentUsername(String username) { current = username; }
            @Override public String getCurrentUsername() { return current; }
        };

        LoginInputData input = new LoginInputData("Paul", "password");

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override public void prepareSuccessView(LoginOutputData data) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("Incorrect password.", errorMessage);
                assertNull(weirdDao.getCurrentUsername());
            }
        };

        new LoginInteractor(weirdDao, presenter).execute(input);
    }

    @Test
    void failure_incorrectPassword() {
        FakeUserDAO userDao = new FakeUserDAO();
        userDao.save(new User("Paul", "correct"));

        LoginInputData input = new LoginInputData("Paul", "wrong");

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override public void prepareSuccessView(LoginOutputData data) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals("Incorrect password.", errorMessage);
                assertNull(userDao.getCurrentUsername());
            }
        };

        new LoginInteractor(userDao, presenter).execute(input);
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

    private void assertDisallowed(String username, String password) {
        FakeUserDAO userDao = new FakeUserDAO();

        LoginInputData input = new LoginInputData(username, password);

        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override public void prepareSuccessView(LoginOutputData data) { fail("Should not succeed"); }
            @Override public void prepareFailView(String errorMessage) {
                assertEquals(
                        "Username and password cannot contain spaces, periods '.', commas ',' or semicolons ';'.",
                        errorMessage
                );
            }
        };

        new LoginInteractor(userDao, presenter).execute(input);
    }
}
