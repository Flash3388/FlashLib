package com.flash3388.flashlib.hid;

import java.util.Collections;

/**
 * A specialized {@link Hid} extension for dualshock controller-like human interface devices.
 * Mostly adds convenience methods for accessing controls, such as {@link #getAxis(DualshockAxis)} and
 * {@link #getButton(DualshockButton)}.
 * <p>
 *     In addition, adds access to the controller's DPad, with {@link #getDpad()}.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public interface DualshockController extends Hid {

    /**
     * Gets the {@link Axis} on this device specified by the given {@link DualshockAxis}.
     *
     * @param axis the axis to retrieve.
     *
     * @return the {@link Axis} object.
     */
    default Axis getAxis(DualshockAxis axis) {
        return getAxis(axis.axisIndex());
    }

    /**
     * Gets the {@link Button} on this device specified by the given {@link DualshockButton}.
     *
     * @param button the button to retrieve.
     *
     * @return the {@link Button} object.
     */
    default Button getButton(DualshockButton button) {
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
