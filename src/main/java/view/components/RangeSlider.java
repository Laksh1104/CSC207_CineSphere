package view.components;

import javax.swing.JSlider;
import javax.swing.plaf.SliderUI;

/**
 * A JSlider that supports selecting a range using two thumbs (low/high).
 * Internally uses the standard BoundedRangeModel:
 * - value = low
 * - extent = high - low
 */
public class RangeSlider extends JSlider {

    public RangeSlider() {
        super();
    }

    public RangeSlider(int min, int max) {
        super(min, max);
        setOrientation(HORIZONTAL);
    }

    @Override
    public void updateUI() {
        setUI((SliderUI) new RangeSliderUI(this));
        updateLabelUIs();
    }

    public int getLowValue() {
        return getValue();
    }

    public void setLowValue(int lowValue) {
        int oldLow = getLowValue();
        int high = getHighValue();

        if (lowValue > high) lowValue = high;
        if (lowValue < getMinimum()) lowValue = getMinimum();

        int newExtent = high - lowValue;
        getModel().setRangeProperties(lowValue, newExtent, getMinimum(), getMaximum(), getValueIsAdjusting());
        firePropertyChange("lowValue", oldLow, lowValue);
    }

    public int getHighValue() {
        return getValue() + getExtent();
    }

    public void setHighValue(int highValue) {
        int low = getLowValue();
        int oldHigh = getHighValue();

        if (highValue < low) highValue = low;
        if (highValue > getMaximum()) highValue = getMaximum();

        int newExtent = highValue - low;
        setExtent(newExtent);
        firePropertyChange("highValue", oldHigh, highValue);
    }
}
