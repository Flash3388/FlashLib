package com.flash3388.flashlib.hid;

import java.util.Collections;

/**
 * A specialized {@link Hid} extension for xbox controller-like human interface devices.
 * Mostly adds convenience methods for accessing controls, such as {@link #getAxis(XboxAxis)} and
 * {@link #getButton(XboxButton)}.
 * <p>
 *     In addition, adds access to the controller's DPad, with {@link #getDpad()}.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public interface XboxController extends Hid {

    /**
     * Gets the {@link Axis} on this device specified by the given {@link XboxAxis}.
     *
     * @param axis the axis to retrieve.
     *
     * @return the {@link Axis} object.
     */
    default Axis getAxis(XboxAxis axis) {
        return getAxis(axis.axisIndex());
    }

    /**
     * Gets the {@link Button} on this device specified by the given {@link XboxButton}.
     *
     * @param button the button to retrieve.
     *
     * @return the {@link Button} object.
     */
    default Button getButton(XboxButton button) {
        return getButton(button.buttonIndex());
    }

    @Override
    default Pov getPov(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("unknown pov " + index);
        }
        return getDpad();
    }

    @Override
    default int getPovCount() {
        return 1;
    }

    @Override
    default Iterable<Pov> povs() {
        return Collections.singleton(getDpad());
    }

    /**
     * Gets the Directional Pad control on the device.
     *
     * @return DPad.
     */
    Dpad getDpad();
}
