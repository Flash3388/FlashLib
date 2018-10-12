package edu.flash3388.flashlib.robot.io.devices.actuators;

public interface PositionController {
    // TODO: COMPILE A STRONGER CONTRACT FOR THIS INTERFACE: values ranges, and meaning.

    /**
     * Sets the position of the component controlled by this object. <em>Position</em> is a decimal which indicates the position of the object.
     *
     * @param position the position to set the controlled object.
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
    void stop();
}
