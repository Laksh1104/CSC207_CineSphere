package use_case.login;

import entity.UserFactory;
import org.junit.jupiter.api.Test;
import use_case.InMemoryUserTestDAO;

import static org.junit.jupiter.api.Assertions.*;

class LoginInteractorTest {

    private LoginOutputBoundary successPresenter(String expectedUsername) {
        return new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData data) {
                assertEquals(expectedUsername, data.getUsername());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Unexpected failure: " + errorMessage);
            }
        };
    }

    private LoginOutputBoundary failPresenter() {
        return new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData data) {
                fail("Unexpected success for: " + data.getUsername());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertNotNull(errorMessage);
                assertFalse(errorMessage.isBlank());
            }
        };
    }

    @Test
    void success_loginSetsCurrentUsername() {
        UserFactory uf = new UserFactory();
        InMemoryUserTestDAO dao = new InMemoryUserTestDAO(uf);
        dao.seed("khalid", "123");

        LoginInteractor interactor = new LoginInteractor(dao, successPresenter("khalid"));
        interactor.execute(new LoginInputData("khalid", "123"));

        assertEquals("khalid", dao.getCurrentUsername());
    }

    @Test
    void fail_userDoesNotExist() {
        UserFactory uf = new UserFactory();
        InMemoryUserTestDAO dao = new InMemoryUserTestDAO(uf);

        LoginInteractor interactor = new LoginInteractor(dao, failPresenter());
        interactor.execute(new LoginInputData("ghost", "pw"));

        assertNull(dao.getCurrentUsername());
    }

    @Test
    void fail_wrongPassword() {
        UserFactory uf = new UserFactory();
        InMemoryUserTestDAO dao = new InMemoryUserTestDAO(uf);
        dao.seed("khalid", "123");

        LoginInteractor interactor = new LoginInteractor(dao, failPresenter());
        interactor.execute(new LoginInputData("khalid", "WRONG"));

        assertNull(dao.getCurrentUsername());
    }
}
