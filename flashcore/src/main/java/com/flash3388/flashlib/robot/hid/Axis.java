package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.robot.control.Invertable;

import java.util.function.DoubleSupplier;

/**
 * A wrapper for axes on human interface devices.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public interface Axis extends DoubleSupplier, Invertable {

    default Button asButton(double threshold) {
        return new AxisButton(this, threshold);
    }
}
