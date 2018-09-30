package edu.flash3388.flashlib.robot.hid;

import java.util.function.IntSupplier;

/**
 * A wrapper for POVs on human interface devices.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class POV implements IntSupplier {

	private final HID mHid;
	private final int mPov;
	
	/**
	 * Creates a POV wrapper object
	 * 
	 * @param hid The hid
	 * @param pov the number of the POV on the controller.
	 */
	public POV(HID hid, int pov){
		mHid = hid;
		mPov = pov;
	}
	
	/**
	 * Get the HID
	 * @return hid
	 */
	public final HID getHID(){
		return mHid;
	}

	/**
	 * Get the POV number
	 * @return POV number
	 */
	public final int getPovNumber(){
		return mPov;
	}

	public int get(){
		return mHid.getRawPov(mPov);
	}

    @Override
    public int getAsInt() {
	    return get();
    }
}
