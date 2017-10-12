package edu.flash3388.flashlib.robot.devices;

/**
 * An abstract base for gyroscope sensors.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public abstract class GyroBase implements Gyro{

	private GyroDataType pidType = GyroDataType.Angle;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GyroDataType getDataType() {
		return pidType;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDataType(GyroDataType type) {
		pidType = type;
	}
}
