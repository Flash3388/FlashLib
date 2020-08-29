package com.flash3388.flashlib.hid;

/**
 * Represents a generic Human Interface Device, possessing axes, buttons
 * and povs.
 * <p>
 *     This is a generic representation for human interface devices,
 *     and thus can be a bit uncomfortable to use, as it requires specifying indices
 *     to axes controls.
 *
 *     There are several specialized extensions for this interface,
 *     if one of them matches the device, it is best to use them instead of this.
 * </p>
 *
 * @since FlashLib 3.0.0
 * @see Joystick
 * @see XboxController
 */
public interface Hid {

    /**
     * Gets the {@link Axis} on this device specified by the given index.
     *
     * @param index index of the axis, starting at 0.
     *
     * @return the {@link Axis} object.
     *
     * @throws IllegalArgumentException if the index represents a non-existent axis.
     */
    Axis getAxis(int index);

    /**
     * Gets the amount of axes on this device.
     *
     * @return amount of {@link Axis} objects connected to this device.
     */
    int getAxisCount();

    /**
     * Gets an {@link Iterable} for iterating over all the {@link Axis} objects
     * on the device.
     *
     * @return iterable of the axes.
     */
    Iterable<Axis> axes();

    /**
     * Gets the {@link Button} on this device specified by the given index.
     *
     * @param index index of the button, starting at 0.
     *
     * @return the {@link Button} object.
     *
     * @throws IllegalArgumentException if the index represents a non-existent button.
     */
    Button getButton(int index);

    /**
     * Gets the amount of buttons on this device.
     *
     * @return amount of {@link Button} objects connected to this device.
     */
    int getButtonCount();

    /**
     * Gets an {@link Iterable} for iterating over all the {@link Button} objects
     * on the device.
     *
     * @return iterable of the buttons.
     */
    Iterable<Button> buttons();

    /**
     * Gets the {@link Pov} on this device specified by the given index.
     *
     * @param index index of the pov, starting at 0.
     *
     * @return the {@link Pov} object.
     *
     * @throws IllegalArgumentException if the index represents a non-existent pov.
     */
    Pov getPov(int index);

    /**
     * Gets the amount of povs on this device.
     *
     * @return amount of {@link Pov} objects connected to this device.
     */
    int getPovCount();

    /**
     * Gets an {@link Iterable} for iterating over all the {@link Pov} objects
     * on the device.
     *
     * @return iterable of the povs.
     */
    Iterable<Pov> povs();

    /**
     * Creates a new builder for constructing a custom {@link Hid}.
     *
     * @return {@link CustomHid.Builder} instance.
     * @see CustomHid
     */
    static CustomHid.Builder builder() {
        return new CustomHid.Builder();
    }
}
