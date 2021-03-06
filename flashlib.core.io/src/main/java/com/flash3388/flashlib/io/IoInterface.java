package com.flash3388.flashlib.io;

/**
 * A connection layer to Input/Output ports. Capable of creating connection and allowing access to different IO
 * devices and ports.
 * What ports can be accessed depends on the implementations.
 * <p>
 *     The {@link IoChannel} objects passed to this interface, must match the ones wanted by the implementation.
 *     Refer to the specific implementation used to verify how to retrieve/create those.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public interface IoInterface {

    /**
     * Creates a new {@link AnalogInput} representing an analog port for input defined by the given
     * {@link IoChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link AnalogInput} object matching the control described by the given {@link IoChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link AnalogInput}.
     */
    AnalogInput newAnalogInput(IoChannel channel);

    /**
     * Creates a new {@link AnalogOutput} representing an analog port for output defined by the given
     * {@link IoChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link AnalogOutput} object matching the control described by the given {@link IoChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link AnalogOutput}.
     */
    AnalogOutput newAnalogOutput(IoChannel channel);

    /**
     * Creates a new {@link DigitalInput} representing an digital port for input defined by the given
     * {@link IoChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link DigitalInput} object matching the control described by the given {@link IoChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link DigitalInput}.
     */
    DigitalInput newDigitalInput(IoChannel channel);

    /**
     * Creates a new {@link DigitalOutput} representing an digital port for output defined by the given
     * {@link IoChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link DigitalOutput} object matching the control described by the given {@link IoChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link DigitalOutput}.
     */
    DigitalOutput newDigitalOutput(IoChannel channel);

    /**
     * Creates a new {@link Pwm} representing an PWM port for output defined by the given
     * {@link IoChannel}.
     *
     * @param channel the channel describing the connection.
     *
     * @return {@link Pwm} object matching the control described by the given {@link IoChannel}.
     *
     * @throws IllegalArgumentException if the given channel does not exist or does not correspond to a control
     *  representable by {@link Pwm}.
     */
    Pwm newPwm(IoChannel channel);

    /**
     * A stub implementation of {@link IoInterface}. All the methods throw {@link UnsupportedOperationException}.
     *
     * @since FlashLib 3.0.0
     */
    class Stub implements IoInterface {

        @Override
        public AnalogInput newAnalogInput(IoChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public AnalogOutput newAnalogOutput(IoChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public DigitalInput newDigitalInput(IoChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public DigitalOutput newDigitalOutput(IoChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public Pwm newPwm(IoChannel channel) {
            throw new UnsupportedOperationException("stub");
        }
    }
}
