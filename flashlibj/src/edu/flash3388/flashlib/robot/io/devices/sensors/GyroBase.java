package edu.flash3388.flashlib.robot.io.devices.sensors;

/**
 * An abstract base for gyroscope sensors.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public abstract class GyroBase implements Gyro {

	private GyroDataType mPidType;

	public GyroBase() {
		mPidType = GyroDataType.Angle;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GyroDataType getDataType() {
		return mPidType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDataType(GyroDataType type) {
		mPidType = type;
	}
}
