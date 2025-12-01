package use_case.logout;

public class LogoutInteractor implements LogoutInputBoundary {

    private final LogoutUserDataAccessInterface userDataAccess;
    private final LogoutOutputBoundary presenter;

    public LogoutInteractor(LogoutUserDataAccessInterface userDataAccess,
                            LogoutOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        try {
            userDataAccess.logout();
            presenter.prepareSuccessView();
        } catch (Exception e) {
            presenter.prepareFailView("Logout failed.");
        }
    }
}
