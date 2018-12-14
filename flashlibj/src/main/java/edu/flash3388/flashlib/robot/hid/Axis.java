package edu.flash3388.flashlib.robot.hid;

import java.util.function.DoubleSupplier;

/**
 * A wrapper for axes on human interface devices.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class Axis implements DoubleSupplier {

	private final HidInterface mHidInterface;
	private final int mChannel;
	private final int mAxis;

	private double mValueThreshold;
	private boolean mIsInverted;

	public Axis(HidInterface hidInterface, int channel, int axis) {
        mHidInterface = hidInterface;
        mChannel = channel;
		mAxis = axis;

		mValueThreshold = 0;
		mIsInverted = false;
	}

	public void setValueThreshold(double valueThreshold) {
	    if (valueThreshold < 0.0 || valueThreshold > 1.0) {
	        throw new IllegalArgumentException("illegal value threshold [0.0, 1.0] " + valueThreshold);
        }

        mValueThreshold = valueThreshold;
    }

    public void setInverted(boolean isInverted) {
	    mIsInverted = isInverted;
    }

    public boolean isInverted() {
	    return mIsInverted;
    }

	/**
	 * Gets the value of the axis.
     *
	 * @return the value of the axis
	 */
	public double get(){
		double raw = mHidInterface.getHidAxis(mChannel, mAxis);
		if (mIsInverted) {
		    raw = -raw;
        }
        if (Math.abs(raw) < mValueThreshold) {
            raw = 0.0;
        }

        return raw;
	}

    @Override
    public double getAsDouble() {
        return get();
    }
}
