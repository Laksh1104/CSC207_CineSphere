package view;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager implements ScreenSwitchListener {

    private final JFrame frame;
    private final Map<String, JPanel> screens = new HashMap<>();

    public ScreenManager(JFrame frame) {
        this.frame = frame;
    }

    public void register(String name, JPanel panel) {
        screens.put(name, panel);
    }

    @Override
    public void onSwitchScreen(String screenName) {
        JPanel next = screens.get(screenName);
        if (next == null) return;

        frame.setContentPane(next);
        frame.revalidate();
        frame.repaint();
    }
}