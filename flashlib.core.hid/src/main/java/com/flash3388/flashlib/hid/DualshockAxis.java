package com.flash3388.flashlib.hid;

/**
 * Represents an axis on an {@link DualshockController}. Can be used to retrieve axes instead of
 * using raw index number, by using {@link DualshockController#getAxis(DualshockAxis)}:
 * <pre>
 *     ds.getAxis(DualshockAxis.LeftStickX)
 * </pre>
 * As opposed to:
 * <pre>
 *     ds.getAxis(0)
 * </pre>
 *
 * @since FlashLib 3.0.0
 */
public enum DualshockAxis {
    LeftStickX(0),
    LeftStickY(1),
    L2(2),
    R2(3),
    RightStickX(4),
    RightStickY(5);

    private final int mAxisIndex;

    DualshockAxis(int axisIndex) {
        mAxisIndex = axisIndex;
    }

    /**
     * The index of the axis on the controller, such that:
     * <pre>
     *     DualshockAxis dsaxis = ...
     *     ds.getAxis(dsaxis).equals(dsaxis.getAxis(dsaxis.axisIndex()))
     * </pre>
     * Is always <b>true</b>
     *
     * @return the index for this axis
     */
    public int axisIndex() {
        return mAxisIndex;
    }

    /**
     * Gets the amount of axes defined.
     * Corresponds to:
     * <pre>
     *     DualshockAxis.values().length
     * </pre>
     *
     * @return amount of axes defined.
     */
    public static int count() {
        return values().length;
    }
}
