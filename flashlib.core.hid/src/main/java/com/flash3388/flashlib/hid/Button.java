package com.flash3388.flashlib.hid;

import com.flash3388.flashlib.control.Invertable;
import com.flash3388.flashlib.scheduling.Trigger;

import java.util.function.BooleanSupplier;

/**
 * Represents a button on a human interface device.
 * <p>
 *     A button is a boolean input data, producing <b>true</b> if active and <b>false</b> otherwise.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public interface Button extends BooleanSupplier, Invertable, Trigger {

    /**
     * <p>
     *     Gets the button value. <b>true</b> if active, <b>false</b> otherwise.
     * </p>
     * <p>
     *     If {@link #isInverted()} is <b>true</b>, the returned results are reversed,
     *     i.e. <code>buttonValue = !buttonValue</code>.
     * </p>
     */
    @Override
    boolean getAsBoolean();

    /**
     * {@inheritDoc}
     * <p>
     *     If inverted <b>true</b>: any values from {@link #getAsBoolean()} will be reversed in value,
     *     i.e. <code>buttonValue = !buttonValue</code>.
     * </p>
     */
    @Override
    void setInverted(boolean inverted);
}
