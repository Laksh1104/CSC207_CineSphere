package view.components;

import javax.swing.JButton;
import java.awt.Cursor;

/**
 * Custom JButton that uses a hand cursor by default.
 */
public class ClickableButton extends JButton {
    
    public ClickableButton() {
        super();
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    public ClickableButton(String text) {
        super(text);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    public ClickableButton(javax.swing.Icon icon) {
        super(icon);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    public ClickableButton(String text, javax.swing.Icon icon) {
        super(text, icon);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
