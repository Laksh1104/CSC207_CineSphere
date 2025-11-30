package use_case;

import entity.User;
import entity.UserFactory;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory fake DAO for Login/Signup/Logout tests.
 */
public class InMemoryUserTestDAO implements
        LoginUserDataAccessInterface,
        SignupUserDataAccessInterface,
        LogoutUserDataAccessInterface {

    private final Map<String, User> users = new HashMap<>();
    private final UserFactory userFactory;
    private String currentUsername;

    public InMemoryUserTestDAO(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    // helper for tests
    public void seed(String username, String password) {
        save(userFactory.create(username, password));
    }

    @Override
    public boolean existsByName(String username) {
        return users.containsKey(username);
    }

    @Override
    public void save(User user) {
        users.put(user.getName(), user);
    }

    @Override
    public User get(String username) {
        return users.get(username);
    }

    @Override
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public void logout() {
        this.currentUsername = null;
    }
}
