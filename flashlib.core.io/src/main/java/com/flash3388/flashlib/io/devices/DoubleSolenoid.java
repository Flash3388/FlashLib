package com.flash3388.flashlib.io.devices;

/**
 * Represents a double solenoid valve.
 * <p>
 *     Solenoids are electromechanically operated valves which are used
 *     to control the flow of pneumatic or hydraulic systems.
 * </p>
 * <p>
 *     Double solenoids use two solenoids to operate the valve,
 *     with voltage being applied to one of the to either close
 *     or open the value. When voltage is applied to both, or neither,
 *     the solenoid does not change position.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public interface DoubleSolenoid {

    /**
     * Operation values for {@link DoubleSolenoid}.
     *
     * @since FlashLib 3.0.0
     */
    enum Value {
        /**
         * Valve open.
         */
        FORWARD,
        /**
         * Valve close.
         */
        REVERSE,
        /**
         * No power to either solenoids.
         */
        OFF
    }

    /**
     * Sets the value to the solenoid.
     *
     * @param value {@link Value} indicating which solenoid to apply current to.
     */
    void set(Value value);

    /**
     * Gets the current value of the solenoid.
     *
     * @return {@link Value} indicating which solenoid has current applied to it.
     */
    Value get();
}
