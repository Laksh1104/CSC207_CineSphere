package watchlist;

import entity.User;
import use_case.login.LoginUserDataAccessInterface;

public class FakeLoginDAO implements LoginUserDataAccessInterface {

    private String currentUser;

    public void setUser(String username) {
        this.currentUser = username;
    }

    @Override
    public boolean existsByName(String username) {
        return false;
    }

    @Override
    public User get(String username) {
        return null;
    }

    @Override
    public void setCurrentUsername(String username) {

    }

    @Override
    public String getCurrentUsername() {
        return currentUser;
    }
}
