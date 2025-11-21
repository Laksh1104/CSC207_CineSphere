import view.LoggedInView;

import javax.swing.*;

public class TestLoggedInView {
    public static void main(String[] args) {

        // Create the LoggedInView panel
        LoggedInView loggedInView = new LoggedInView();

        // Create window
        JFrame frame = new JFrame("LoggedInView Test Harness");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);

        // Add the view into the frame
        frame.add(loggedInView);

        // Show the window
        frame.setVisible(true);
    }
}
