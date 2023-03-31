package com.flash3388.flashlib.io.devices.sensors;

import com.flash3388.flashlib.io.AnalogInput;
import com.flash3388.flashlib.io.devices.DeviceConstructor;
import com.flash3388.flashlib.io.devices.NamedArg;
import com.flash3388.flashlib.io.devices.RangeFinder;

import java.io.IOException;

/**
 * Control class for an analog range finder sensor. Range finders are sensors used to measure distances between
 * them and an object in front of them. There are several ways range finders measure distances, for example: sound waves,
 * infrared, etc.
 * <p>
 * Analog range finders output the range value using an analog signal. The voltage on the port indicates the range
 * and can be converted to centimeters by dividing it by a sensitivity value. The sensitivity value is measured by 
 * volts/centimeter.
 * 
 *
 * @since FlashLib 1.2.0
 */
public class AnalogRangeFinder implements RangeFinder {
	
	private AnalogInput mInput;
	private double mSensitivity;

	/**
	 * Creates a new analog range finder sensor.
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into distance.
	 * <p>
	 * The sensitivity of the sensor is used to convert from analog voltage into distance.
	 * 
	 * @param input analog input port
	 * @param sensitivity sensitivity in volts/centimeter
	 */
	@DeviceConstructor
	public AnalogRangeFinder(
			@NamedArg("input") AnalogInput input,
			@NamedArg("sensitivity") double sensitivity
	) {
		this.mInput = input;
		this.mSensitivity = sensitivity;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Releases the analog input port.
	 */
	@Override
	public void close() throws IOException {
		if(mInput != null) {
			mInput.close();
			mInput = null;
		}
	}
	
	/**
	 * Sets the sensor sensitivity value.
	 * <p>
	 * Sensitivity is used to convert the voltage from the input port to range in centimeters. The value
	 * indicates the amount of volts per centimeter.
	 * 
	 * @param sensitivity sensitivity in volts per centimeter
	 */
	public void setSensitivity(double sensitivity){
		this.mSensitivity = sensitivity;
	}

	/**
	 * Gets the sensor sensitivity value.
	 * <p>
	 * Sensitivity is used to convert the voltage from the input port to range in centimeters. The value
	 * indicates the amount of volts per centimeter.
	 * 
	 * @return sensitivity in volts per centimeter
	 */
	public double getSensitivity(){
		return mSensitivity;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The voltage on the input port is read and then divided by the sensitivity value
	 * to convert it from volts to centimeters.
	 */
	@Override
	public double getRangeCm() {
		return mInput.getVoltage() / mSensitivity;
	}
}
