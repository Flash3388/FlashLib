package com.flash3388.flashlib.hid;

import com.flash3388.flashlib.annotations.MainThreadOnly;

/**
 * A connection layer to Human Interface Devices. Capable of
 * creating connection and allowing access to different HIDs.
 * What HIDs can be accessed depends on the implementations.
 * <p>
 *     The {@link HidChannel} objects passed to this interface, must match the ones wanted by the implementation.
 *     Refer to the specific implementation used to verify how to retrieve/create those.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public interface HidInterface {

    /**
     * Creates a new {@link Axis} representing an axis defined by the given {@link HidChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link Axis} object matching the control described by the given {@link HidChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link Axis}.
     */
    @MainThreadOnly
    Axis newAxis(HidChannel channel);

    /**
     * Creates a new {@link Button} representing button defined by the given {@link HidChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link Button} object matching the control described by the given {@link HidChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link Button}.
     */
    @MainThreadOnly
    Button newButton(HidChannel channel);

    /**
     * Creates a new {@link Pov} representing pov defined by the given {@link HidChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link Pov} object matching the control described by the given {@link HidChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link Pov}.
     */
    @MainThreadOnly
    Pov newPov(HidChannel channel);

    /**
     * Creates a new {@link Hid} representing a Human Interface device defined by the given {@link HidChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link Hid} object matching the control described by the given {@link HidChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link Hid}.
     */
    @MainThreadOnly
    Hid newGenericHid(HidChannel channel);

    /**
     * Creates a new {@link Joystick} representing a joystick-like device defined by the given {@link HidChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link Joystick} object matching the control described by the given {@link HidChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link Joystick}.
     */
    @MainThreadOnly
    Joystick newJoystick(HidChannel channel);

    /**
     * Creates a new {@link XboxController} representing a xbox-like controller defined by the given {@link HidChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link XboxController} object matching the control described by the given {@link HidChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link XboxController}.
     */
    @MainThreadOnly
    XboxController newXboxController(HidChannel channel);

    /**
     * Creates a new {@link DualshockController} representing a dualshock-like controller defined by
     * the given {@link HidChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link DualshockController} object matching the control described by the given {@link HidChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link DualshockController}.
     */
    @MainThreadOnly
    DualshockController newDualshockController(HidChannel channel);

    /**
     * A stub implementation of {@link HidInterface}. All the methods throw {@link UnsupportedOperationException}.
     *
     * @since FlashLib 3.0.0
     */
    class Stub implements HidInterface {

        @Override
        public Axis newAxis(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public Button newButton(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public Pov newPov(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public Hid newGenericHid(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public Joystick newJoystick(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public XboxController newXboxController(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public DualshockController newDualshockController(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }
    }
}
