package use_case.logout;

public interface LogoutUserDataAccessInterface {
    void setCurrentUsername(String name);
    String getCurrentUsername();
}
