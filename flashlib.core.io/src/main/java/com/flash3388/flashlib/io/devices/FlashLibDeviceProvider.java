package com.flash3388.flashlib.io.devices;

import com.flash3388.flashlib.io.devices.actuators.PwmPositionController;
import com.flash3388.flashlib.io.devices.actuators.PwmSpeedController;
import com.flash3388.flashlib.io.devices.actuators.PwmTalonSrx;
import com.flash3388.flashlib.io.devices.actuators.Talon;
import com.flash3388.flashlib.io.devices.sensors.AnalogAccelerometer;
import com.flash3388.flashlib.io.devices.sensors.AnalogGyro;
import com.flash3388.flashlib.io.devices.sensors.AnalogRangeFinder;
import com.flash3388.flashlib.io.devices.sensors.PulseEncoder;
import com.flash3388.flashlib.io.devices.sensors.PulseWidthRangeFinder;
import com.flash3388.flashlib.io.devices.sensors.Ultrasonic;

public class FlashLibDeviceProvider extends AbstractDeviceProvider {

    public FlashLibDeviceProvider() {
        // actuators
        registerDevice("flashlib.device.PwmSpeedController", PwmSpeedController.class);
        registerDevice("flashlib.device.PwmPositionController", PwmPositionController.class);
        registerDevice("flashlib.device.TalonSrx", PwmTalonSrx.class);
        registerDevice("flashlib.device.Talon", Talon.class);
        // sensors
        registerDevice("flashlib.device.AnalogAccelerometer", AnalogAccelerometer.class);
        registerDevice("flashlib.device.AnalogGyro", AnalogGyro.class);
        registerDevice("flashlib.device.AnalogRangeFinder", AnalogRangeFinder.class);
        registerDevice("flashlib.device.PulseEncoder", PulseEncoder.class);
        registerDevice("flashlib.device.PulseWidthRangeFinder", PulseWidthRangeFinder.class);
        registerDevice("flashlib.device.Ultrasonic", Ultrasonic.class);
    }
}
