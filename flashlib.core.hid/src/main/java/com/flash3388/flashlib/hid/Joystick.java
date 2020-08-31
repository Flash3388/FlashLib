package com.flash3388.flashlib.hid;

import java.util.Collections;

/**
 * A specialized {@link Hid} extension for joystick-like human interface devices.
 * Mostly adds convenience methods for accessing controls, such as {@link #getAxis(JoystickAxis)} and
 * {@link #getButton(JoystickButton)}.
 *
 * @since FlashLib 3.0.0
 */
public interface Joystick extends Hid {

    /**
     * Gets the {@link Axis} on this device specified by the given {@link JoystickAxis}.
     *
     * @param axis the axis to retrieve.
     *
     * @return the {@link Axis} object.
     */
    default Axis getAxis(JoystickAxis axis) {
        return getAxis(axis.axisIndex());
    }

    /**
     * Gets the {@link Button} on this device specified by the given {@link JoystickButton}.
     *
     * @param button the button to retrieve.
     *
     * @return the {@link Button} object.
     */
    default Button getButton(JoystickButton button) {
        return getButton(button.buttonIndex());
    }

    @Override
    default Pov getPov(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("unknown pov " + index);
        }
        return getPov();
    }

    @Override
    default int getPovCount() {
        return 1;
    }

    @Override
    default Iterable<Pov> povs() {
        return Collections.singleton(getPov());
    }

    /**
     * Gets the Point of View control on the device.
     *
     * @return point of view.
     */
    Pov getPov();
}
