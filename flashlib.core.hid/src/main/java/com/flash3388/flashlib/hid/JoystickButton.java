package com.flash3388.flashlib.hid;

/**
 * Represents a button on a {@link Joystick}. Can be used to retrieve axes instead of
 * using raw index number, by using {@link Joystick#getButton(JoystickButton)}:
 * <pre>
 *     joystick.getButton(JoystickButton.TRIGGER)
 * </pre>
 * As opposed to:
 * <pre>
 *     joystick.getButton(0)
 * </pre>
 *
 * @since FlashLib 3.0.0
 */
public enum JoystickButton {
    TRIGGER(0)
    ;

    private final int mButtonIndex;

    JoystickButton(int buttonIndex) {
        mButtonIndex = buttonIndex;
    }

    /**
     * The index of the button on the joystick, such that:
     * <pre>
     *     JoystickButton xbutton = ...
     *     joystick.getButton(xbutton).equals(joystick.getButton(xbutton.buttonIndex()))
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
     *     JoystickButton.values().length
     * </pre>
     *
     * @return amount of buttons defined.
     */
    public static int count() {
        return values().length;
    }
}
