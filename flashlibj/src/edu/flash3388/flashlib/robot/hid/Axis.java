package edu.flash3388.flashlib.robot.hid;

import java.util.function.DoubleSupplier;

/**
 * A wrapper for axes on human interface devices.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class Axis implements DoubleSupplier {

	private final HID mHid;
	private final int mAxis;
	
	/**
	 * Creates a new trigger.
	 * 
	 * @param hid the device
	 * @param number the axis index
	 */
	public Axis(HID hid, int number) {
		mHid = hid;
		mAxis = number;
	}
	
	/**
	 * Get the HID
	 * @return hid
	 */
	public final HID getHID(){
		return mHid;
	}

	/**
	 * Get the axis number
	 * @return axis number
	 */
	public final int getAxisNumber(){
		return mAxis;
	}
	
	/**
	 * Gets the value of the trigger.
	 * @return the value of the trigger
	 */
	public double get(){
		return mHid.getRawAxis(mAxis);
	}

    @Override
    public double getAsDouble() {
        return get();
    }
}
