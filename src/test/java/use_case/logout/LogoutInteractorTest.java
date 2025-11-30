package use_case.logout;

import entity.UserFactory;
import org.junit.jupiter.api.Test;
import use_case.InMemoryUserTestDAO;

import static org.junit.jupiter.api.Assertions.*;

class LogoutInteractorTest {

    private LogoutOutputBoundary successPresenter() {
        return new LogoutOutputBoundary() {
            @Override
            public void prepareSuccessView() {
                // ok
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Unexpected failure: " + errorMessage);
            }
        };
    }

    @Test
    void success_clearsCurrentUsername() {
        InMemoryUserTestDAO dao = new InMemoryUserTestDAO(new UserFactory());
        dao.setCurrentUsername("khalid");

        LogoutInteractor interactor = new LogoutInteractor(dao, successPresenter());
        interactor.execute();

        assertNull(dao.getCurrentUsername());
    }
}
