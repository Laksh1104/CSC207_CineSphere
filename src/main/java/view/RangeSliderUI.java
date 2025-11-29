package view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

public class RangeSliderUI extends BasicSliderUI {

    private final Rectangle upperThumbRect = new Rectangle();
    private boolean upperThumbSelected;
    private boolean lowerDragging;
    private boolean upperDragging;

    public RangeSliderUI(RangeSlider b) {
        super(b);
    }

    @Override
    protected void calculateThumbSize() {
        super.calculateThumbSize();
        upperThumbRect.setSize(thumbRect.width, thumbRect.height);
    }

    @Override
    protected void calculateThumbLocation() {
        super.calculateThumbLocation();

        // position upper thumb based on high value
        int upperPosition = xPositionForValue(((RangeSlider) slider).getHighValue());

        upperThumbRect.x = upperPosition - (upperThumbRect.width / 2);
        upperThumbRect.y = trackRect.y;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);

        // paint the upper thumb (super.paint paints the lower thumb via thumbRect)
        Rectangle saved = new Rectangle(thumbRect);
        thumbRect.setBounds(upperThumbRect);
        paintThumb(g);
        thumbRect.setBounds(saved);

        paintRange(g);
    }

    private void paintRange(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            int lowX = thumbRect.x + thumbRect.width / 2;
            int highX = upperThumbRect.x + upperThumbRect.width / 2;

            int y = trackRect.y + trackRect.height / 2 - 2;
            int h = 4;

            int x = Math.min(lowX, highX);
            int w = Math.abs(highX - lowX);

            g2.setColor(new Color(120, 120, 120, 80));
            g2.fillRect(x, y, w, h);
        } finally {
            g2.dispose();
        }
    }

    @Override
    protected TrackListener createTrackListener(JSlider slider) {
        return new RangeTrackListener();
    }

    private class RangeTrackListener extends TrackListener {
        @Override
        public void mousePressed(MouseEvent e) {
            if (!slider.isEnabled()) return;

            currentMouseX = e.getX();
            currentMouseY = e.getY();

            // pick the closer thumb
            boolean lowerHit = thumbRect.contains(currentMouseX, currentMouseY);
            boolean upperHit = upperThumbRect.contains(currentMouseX, currentMouseY);

            if (upperHit && lowerHit) {
                // if both: choose nearest center
                int lowerCenter = thumbRect.x + thumbRect.width / 2;
                int upperCenter = upperThumbRect.x + upperThumbRect.width / 2;
                upperThumbSelected = Math.abs(currentMouseX - upperCenter) < Math.abs(currentMouseX - lowerCenter);
            } else if (upperHit) {
                upperThumbSelected = true;
            } else if (lowerHit) {
                upperThumbSelected = false;
            } else {
                // clicked on track: snap nearest thumb
                int lowerCenter = thumbRect.x + thumbRect.width / 2;
                int upperCenter = upperThumbRect.x + upperThumbRect.width / 2;
                upperThumbSelected = Math.abs(currentMouseX - upperCenter) < Math.abs(currentMouseX - lowerCenter);
            }

            if (upperThumbSelected) {
                upperDragging = true;
            } else {
                lowerDragging = true;
            }

            slider.setValueIsAdjusting(true);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            lowerDragging = false;
            upperDragging = false;
            slider.setValueIsAdjusting(false);
            slider.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!slider.isEnabled()) return;

            currentMouseX = e.getX();
            int value = valueForXPosition(currentMouseX);

            RangeSlider rs = (RangeSlider) slider;

            if (lowerDragging) {
                // clamp so low <= high
                int high = rs.getHighValue();
                if (value > high) value = high;
                rs.setLowValue(value);
            } else if (upperDragging) {
                // clamp so high >= low
                int low = rs.getLowValue();
                if (value < low) value = low;
                rs.setHighValue(value);
            }
        }
    }
}
