package com.flash3388.flashlib.io.devices.actuators;

import com.flash3388.flashlib.control.Stoppable;

/**
 * Represents controller component for position-based actuator,
 * such as a <em>Servo</em>, <em>Stepper</em> and such.
 *
 * @since FlashLib 2.0.0
 */
public interface PositionController extends Stoppable {

    /**
     * Sets the position of the component controlled by this object.
     *
     * @param position the position to set the controlled object between 0 and 1,
     *                 representing percentages from initial position to max position.
     */
    void set(double position);

    /**
     * Gets the current position of the component controlled by this object.
     *
     * @return the position of the controlled object.
     */
    double get();

    /**
     * Stops the motion of the controlled component.
     */
    @Override
    void stop();
}
