package com.flash3388.flashlib.hid;

/**
 * Represents a button on an {@link DualshockController}. Can be used to retrieve axes instead of
 * using raw index number, by using {@link DualshockController#getButton(DualshockButton)}:
 * <pre>
 *     ds.getButton(DualshockButton.A)
 * </pre>
 * As opposed to:
 * <pre>
 *     ds.getButton(0)
 * </pre>
 *
 * @since FlashLib 3.0.0
 */
public enum DualshockButton {
    Cross(0),
    Circle(1),
    Square(2),
    Triangle(3),
    L1(4),
    R1(5),
    L2(6),
    R2(7),
    Share(8),
    Options(9),
    L3(10),
    R3(11),
    PS(12),
    Touchpad(13);

    private final int mButtonIndex;

    DualshockButton(int buttonIndex) {
        mButtonIndex = buttonIndex;
    }

    /**
     * The index of the button on the controller, such that:
     * <pre>
     *     DualshockButton dsbutton = ...
     *     ds.getButton(dsbutton).equals(ds.getButton(dsbutton.buttonIndex()))
     * </pre>
     * Is always <b>true</b>
     *
     * @return the index for this button
     */
    public int buttonIndex() {
        return mButtonIndex;
    }

    /**
     * Gets the amount the amount of buttons defined.
     * Corresponds to:
     * <pre>
     *     DualshockButton.values().length
     * </pre>
     *
     * @return amount of buttons defined.
     */
    public static int count() {
        return values().length;
    }
}
