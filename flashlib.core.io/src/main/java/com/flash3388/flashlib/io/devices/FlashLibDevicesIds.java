package com.flash3388.flashlib.io.devices;

public class FlashLibDevicesIds {

    private FlashLibDevicesIds() {}

    /**
     * Defines a basic PWM speed (motor) controller.
     *
     * <p>
     * Params:
     * <ul>
     *     <li><em>port</em> ({@link com.flash3388.flashlib.io.Pwm}): port the device is connected to.</li>
     *     <li><em>pulseBounds</em> ({@link com.flash3388.flashlib.io.devices.actuators.PwmBounds}): defines pwm bounds for controlling the device.</li>
     *     <li><em>pwmFrequency</em> (<em>double</em>): frequency to use for communicating with the device (in HZ).</li>
     * </ul>
     */
    public static final DeviceId<SpeedController> PwmSpeedController = DeviceId.of(21, SpeedController.class);

    /**
     * Defines a basic PWM position controller.
     *
     * <p>
     * Params:
     * <ul>
     *     <li><em>port</em> ({@link com.flash3388.flashlib.io.Pwm}): port the device is connected to.</li>
     *     <li><em>pulseBounds</em> ({@link com.flash3388.flashlib.io.devices.actuators.PwmBounds}): defines pwm bounds for controlling the device.</li>
     *     <li><em>pwmFrequency</em> (<em>double</em>): frequency to use for communicating with the device (in HZ).</li>
     * </ul>
     */
    public static final DeviceId<PositionController> PwmPositionController = DeviceId.of(22, PositionController.class);

    /**
     * Defines a TalonSRX speed (motor) controller connected via a PWM port.
     *
     * <p>
     * Params:
     * <ul>
     *     <li><em>port</em> ({@link com.flash3388.flashlib.io.Pwm}): port the device is connected to.</li>
     * </ul>
     */
    public static final DeviceId<SpeedController> TalonSrx = DeviceId.of(23, SpeedController.class);

    /**
     * Defines a Talon speed (motor) controller connected via a PWM port.
     *
     * <p>
     * Params:
     * <ul>
     *     <li><em>port</em> ({@link com.flash3388.flashlib.io.Pwm}): port the device is connected to.</li>
     * </ul>
     */
    public static final DeviceId<SpeedController> Talon = DeviceId.of(24, SpeedController.class);

    /**
     * Defines a basic accelerometer which is connected via an analog port.
     *
     * <p>
     * Params:
     * <ul>
     *     <li><em>input</em> ({@link com.flash3388.flashlib.io.AnalogInput}): port the device is connected to.</li>
     *     <li><em>zeroGVoltage</em> (<em>double</em>): voltage when acceleration is 0, as defined by the device.</li>
     *     <li><em>voltsPerG</em> (<em>double</em>): conversion factor from volts to G acceleration.</li>
     * </ul>
     */
    public static final DeviceId<Accelerometer> AnalogAccelerometer = DeviceId.of(51, Accelerometer.class);

    /**
     * Defines a basic range finder which is connected via an analog port.
     *
     * <p>
     * Params:
     * <ul>
     *     <li><em>input</em> ({@link com.flash3388.flashlib.io.AnalogInput}): port the device is connected to.</li>
     *     <li><em>sensitivity</em> (<em>double</em>): sensitivity of the device in volts/centimeter</li>
     * </ul>
     */
    public static final DeviceId<RangeFinder> AnalogRangeFinder = DeviceId.of(52, RangeFinder.class);

    /**
     * Defines a basic range finder which is connected via a digital port and uses pulses to report the range.
     *
     * <p>
     * Params:
     * <ul>
     *     <li><em>counter</em> ({@link com.flash3388.flashlib.io.PulseLengthCounter}): counter on the port the device is connected to.</li>
     *     <li><em>sensitivity</em> (<em>double</em>): sensitivity of the device in microseconds/centimeter</li>
     * </ul>
     */
    public static final DeviceId<RangeFinder> PulseWidthRangeFinder = DeviceId.of(53, RangeFinder.class);

    /**
     * Defines a basic ultrasonic range finder which is connected to a digital port. Works for devices
     * which use two channels: trigger which forces the device to send a pulse, and echo which the device uses
     * to reply with the distance.
     *
     * <p>
     * Params:
     * <ul>
     *     <li><em>pingChannel</em> ({@link com.flash3388.flashlib.io.DigitalOutput}): channel used to trigger pings (trigger).</li>
     *     <li><em>counter</em> ({@link com.flash3388.flashlib.io.PulseLengthCounter}): counter on the pulse used to report the range (echo).</li>
     * </ul>
     */
    public static final DeviceId<RangeFinder> Ultrasonic = DeviceId.of(54, RangeFinder.class);
}
