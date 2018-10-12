package edu.flash3388.flashlib.robot.hid;

import java.util.function.DoubleSupplier;

/**
 * A wrapper for axes on human interface devices.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class Axis implements DoubleSupplier {

	private final HIDInterface mHidInterface;
	private final int mChannel;
	private final int mAxis;

	public Axis(HIDInterface hidInterface, int channel, int axis) {
        mHidInterface = hidInterface;
        mChannel = channel;
		mAxis = axis;
	}
	
	/**
	 * Gets the value of the axis.
     *
	 * @return the value of the axis
	 */
	public double get(){
		return mHidInterface.getHidAxis(mChannel, mAxis);
	}

    @Override
    public double getAsDouble() {
        return get();
    }
}
