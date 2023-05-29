package com.flash3388.flashlib.io.devices;

import com.flash3388.flashlib.io.devices.actuators.PwmPositionController;
import com.flash3388.flashlib.io.devices.actuators.PwmSpeedController;
import com.flash3388.flashlib.io.devices.actuators.PwmTalonSrx;
import com.flash3388.flashlib.io.devices.actuators.Talon;
import com.flash3388.flashlib.io.devices.sensors.AnalogAccelerometer;
import com.flash3388.flashlib.io.devices.sensors.AnalogRangeFinder;
import com.flash3388.flashlib.io.devices.sensors.PulseWidthRangeFinder;
import com.flash3388.flashlib.io.devices.sensors.Ultrasonic;

public class FlashLibDeviceProvider extends AbstractDeviceProvider {

    public FlashLibDeviceProvider() {
        // actuators
        registerDevice(FlashLibDevicesIds.PwmSpeedController, PwmSpeedController.class);
        registerDevice(FlashLibDevicesIds.PwmPositionController, PwmPositionController.class);
        registerDevice(FlashLibDevicesIds.TalonSrx, PwmTalonSrx.class);
        registerDevice(FlashLibDevicesIds.Talon, Talon.class);
        // sensors
        registerDevice(FlashLibDevicesIds.AnalogAccelerometer, AnalogAccelerometer.class);
        registerDevice(FlashLibDevicesIds.AnalogRangeFinder, AnalogRangeFinder.class);
        registerDevice(FlashLibDevicesIds.PulseWidthRangeFinder, PulseWidthRangeFinder.class);
        registerDevice(FlashLibDevicesIds.Ultrasonic, Ultrasonic.class);
    }
}
