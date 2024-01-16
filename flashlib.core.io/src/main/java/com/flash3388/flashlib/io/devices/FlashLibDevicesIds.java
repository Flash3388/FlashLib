package com.flash3388.flashlib.io.devices;

public class FlashLibDevicesIds {

    private FlashLibDevicesIds() {}

    public static final DeviceId<SpeedController> PwmSpeedController = DeviceId.of(21, SpeedController.class);
    public static final DeviceId<PositionController> PwmPositionController = DeviceId.of(22, PositionController.class);
    public static final DeviceId<SpeedController> TalonSrx = DeviceId.of(23, SpeedController.class);
    public static final DeviceId<SpeedController> Talon = DeviceId.of(24, SpeedController.class);

    public static final DeviceId<Accelerometer> AnalogAccelerometer = DeviceId.of(51, Accelerometer.class);
    public static final DeviceId<RangeFinder> AnalogRangeFinder = DeviceId.of(52, RangeFinder.class);
    public static final DeviceId<RangeFinder> PulseWidthRangeFinder = DeviceId.of(53, RangeFinder.class);
    public static final DeviceId<RangeFinder> Ultrasonic = DeviceId.of(54, RangeFinder.class);
}
