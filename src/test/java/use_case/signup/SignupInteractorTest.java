package use_case.signup;

import entity.UserFactory;
import org.junit.jupiter.api.Test;
import use_case.InMemoryUserTestDAO;

import static org.junit.jupiter.api.Assertions.*;

class SignupInteractorTest {

    private SignupOutputBoundary successPresenter(String expectedUsername) {
        return new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData data) {
                assertEquals(expectedUsername, data.getUsername());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Unexpected failure: " + errorMessage);
            }
        };
    }

    private SignupOutputBoundary failPresenter() {
        return new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData data) {
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
    void success_savesUser() {
        UserFactory uf = new UserFactory();
        InMemoryUserTestDAO dao = new InMemoryUserTestDAO(uf);

        SignupInteractor interactor = new SignupInteractor(dao, successPresenter("newuser"), uf);
        interactor.execute(new SignupInputData("newuser", "pw"));

        assertTrue(dao.existsByName("newuser"));
        assertNotNull(dao.get("newuser"));
        assertEquals("pw", dao.get("newuser").getPassword());
    }

    @Test
    void fail_usernameAlreadyExists() {
        UserFactory uf = new UserFactory();
        InMemoryUserTestDAO dao = new InMemoryUserTestDAO(uf);
        dao.seed("taken", "pw");

        SignupInteractor interactor = new SignupInteractor(dao, failPresenter(), uf);
        interactor.execute(new SignupInputData("taken", "pw2"));

        // confirm original still there
        assertEquals("pw", dao.get("taken").getPassword());
    }
}
