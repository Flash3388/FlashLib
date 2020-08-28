package com.flash3388.flashlib.hid;

/**
 * Represents an axis on an {@link XboxController}. Can be used to retrieve axes instead of
 * using raw index number, by using {@link XboxController#getAxis(XboxAxis)}:
 * <pre>
 *     joystick.getAxis(JoystickAxis.X)
 * </pre>
 * As opposed to:
 * <pre>
 *     joystick.getAxis(0)
 * </pre>
 *
 * @since FlashLib 3.0.0
 */
public enum JoystickAxis {
    X(0),
    Y(1),
    Z(2),
    THROTTLE(3)
    ;

    private final int mAxisIndex;

    JoystickAxis(int axisIndex) {
        mAxisIndex = axisIndex;
    }

    /**
     * The index of the axis on the joystick, such that:
     * <pre>
     *     JoystickAxis xaxis = ...
     *     joystick.getAxis(xaxis).equals(joystick.getAxis(xaxis.axisIndex()))
     * </pre>
     * Is always <b>true</b>
     *
     * @return the index for this axis
     */
    public int axisIndex() {
        return mAxisIndex;
    }

    /**
     * Gets the amount the amount of axes defined.
     * Corresponds to:
     * <pre>
     *     JoystickAxis.values().length
     * </pre>
     *
     * @return amount of axes defined.
     */
    public static int count() {
        return values().length;
    }
}
