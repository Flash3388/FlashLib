package com.flash3388.flashlib.control;

/**
 * Represents a component capable of reversing it's input/output data.
 *
 * @since FlashLib 2.0.0
 */
public interface Invertable {

    /**
     * Sets whether invert input/output values.
     * <p>
     *     If the given <i>inverted</i> is already the same as {@link #isInverted()},
     *     nothing will happen.
     * </p>
     *
     * @param inverted <b>true</b> to invert, <b>false</b> otherwise.
     */
    void setInverted(boolean inverted);

    /**
     * Gets whether the data inverted.
     * @return <b>true</b> if inverted, <b>false</b> otherwise.
     */
    boolean isInverted();

    /**
     * Inverts the current data status. If {@link #isInverted()} is <b>true</b>,
     * it is set to <b>false</b>, and vice versa.
     */
    default void invert() {
        setInverted(!isInverted());
    }
}
