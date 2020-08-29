package com.flash3388.flashlib.io.devices.pneumatics;

import com.flash3388.flashlib.time.Time;

/**
 * Represents a single solenoid valve.
 * <p>
 *     Solenoids are electromechanically operated valves which are used
 *     to control the flow of pneumatic or hydraulic systems.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public interface Solenoid {

    /**
     * Sets whether or not the valve is open.
     *
     * @param open <b>true</b> to open, <b>false</b> otherwise.
     */
    void set(boolean open);

    /**
     * Gets whether or not the valve is open.
     *
     * @return <b>true</b> if open, <b>false</b> otherwise.
     */
    boolean get();

    void pulse(Time duration);
}
