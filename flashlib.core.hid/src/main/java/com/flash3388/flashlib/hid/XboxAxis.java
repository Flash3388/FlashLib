package com.flash3388.flashlib.hid;

/**
 * Represents an axis on an {@link XboxController}. Can be used to retrieve axes instead of
 * using raw index number, by using {@link XboxController#getAxis(XboxAxis)}:
 * <pre>
 *     xbox.getAxis(XboxAxis.LeftStickX)
 * </pre>
 * As opposed to:
 * <pre>
 *     xbox.getAxis(0)
 * </pre>
 *
 * @since FlashLib 3.0.0
 */
public enum XboxAxis {
    LeftStickX(0),
    LeftStickY(1),
    LT(2),
    RT(3),
    RightStickX(4),
    RightStickY(5);

    private final int mAxisIndex;

    XboxAxis(int axisIndex) {
        mAxisIndex = axisIndex;
    }

    /**
     * The index of the axis on the controller, such that:
     * <pre>
     *     XboxAxis xaxis = ...
     *     xbox.getAxis(xaxis).equals(xbox.getAxis(xaxis.axisIndex()))
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
     *     XboxAxis.values().length
     * </pre>
     *
     * @return amount of axes defined.
     */
    public static int count() {
        return values().length;
    }
}
