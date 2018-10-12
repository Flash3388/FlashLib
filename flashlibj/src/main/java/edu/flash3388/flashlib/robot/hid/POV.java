package edu.flash3388.flashlib.robot.hid;

import java.util.function.IntSupplier;

/**
 * A wrapper for POVs on human interface devices.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class POV implements IntSupplier {

    private final HIDInterface mHidInterface;
    private final int mChannel;
    private final int mPov;

    public POV(HIDInterface hidInterface, int channel, int pov) {
        mHidInterface = hidInterface;
        mChannel = channel;
        mPov = pov;
    }

	public int get(){
		return mHidInterface.getHidPov(mChannel, mPov);
	}

    @Override
    public int getAsInt() {
	    return get();
    }
}
