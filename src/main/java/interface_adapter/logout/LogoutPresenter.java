package interface_adapter.logout;

import use_case.logout.LogoutOutputBoundary;
import view.ScreenSwitchListener;

import javax.swing.*;

public class LogoutPresenter implements LogoutOutputBoundary {

    private final ScreenSwitchListener screenSwitchListener;

    public LogoutPresenter(ScreenSwitchListener screenSwitchListener) {
        this.screenSwitchListener = screenSwitchListener;
    }

    @Override
    public void prepareSuccessView() {
        SwingUtilities.invokeLater(() -> {
            if (screenSwitchListener != null) {
                screenSwitchListener.onSwitchScreen("Login");
            }
        });
    }

    @Override
    public void prepareFailView(String errorMessage) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                errorMessage,
                "Logout failed",
                JOptionPane.ERROR_MESSAGE
        ));
    }
}
