package com.flash3388.flashlib.hid;

/**
 * Represents a button on an {@link XboxController}. Can be used to retrieve axes instead of
 * using raw index number, by using {@link XboxController#getButton(XboxButton)}:
 * <pre>
 *     xbox.getButton(XboxButton.A)
 * </pre>
 * As opposed to:
 * <pre>
 *     xbox.getButton(0)
 * </pre>
 *
 * @since FlashLib 3.0.0
 */
public enum XboxButton {
    A(0),
    B(1),
    X(2),
    Y(3),
    LB(4),
    RB(5),
    Back(6),
    Start(7),
    LeftStickButton(8),
    RightStickButton(9);

    private final int mButtonIndex;

    XboxButton(int buttonIndex) {
        mButtonIndex = buttonIndex;
    }

    /**
     * The index of the button on the controller, such that:
     * <pre>
     *     XboxButton xbutton = ...
     *     xbox.getButton(xbutton).equals(xbox.getButton(xbutton.buttonIndex()))
     * </pre>
     * Is always <b>true</b>
     *
     * @return the index for this button
     */
    public int buttonIndex() {
        return mButtonIndex;
    }

    /**
     * Gets the amount of buttons defined.
     * Corresponds to:
     * <pre>
     *     XboxButton.values().length
     * </pre>
     *
     * @return amount of buttons defined.
     */
    public static int count() {
        return values().length;
    }
}
