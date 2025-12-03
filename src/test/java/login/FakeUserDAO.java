package login;

import entity.User;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory DAO for login/signup/logout tests.
 */
public class FakeUserDAO implements
        LoginUserDataAccessInterface,
        SignupUserDataAccessInterface,
        LogoutUserDataAccessInterface {

    private final Map<String, User> users = new HashMap<>();
    private String currentUsername;

    @Override
    public boolean existsByName(String username) {
        return users.containsKey(username);
    }

    @Override
    public User get(String username) {
        return users.get(username);
    }

    @Override
    public void save(User user) {
        users.put(user.getName(), user);
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
        currentUsername = null;
    }
}
