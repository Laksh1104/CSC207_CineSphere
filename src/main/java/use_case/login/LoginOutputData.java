package use_case.login;

public class LoginOutputData {
    private final String username;

    public LoginOutputData(String username) {
        this.username = username;
    }
    //GOT RID OF BOOL INPUT IN REFACTORING CAN ADD BACK IF IT IS NEEDED IN THE FUTURE

    public String getUsername() { return username; }
}
