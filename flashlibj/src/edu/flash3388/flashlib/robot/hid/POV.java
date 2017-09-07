package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.util.beans.IntegerSource;

/**
 * A wrapper for POVs on human interface devices.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class POV implements IntegerSource{

	private HID hid;
	private int num;
	
	/**
	 * Creates a POV wrapper object
	 * 
	 * @param hid The hid
	 * @param num the number of the POV on the controller.
	 */
	public POV(HID hid, int num){
		this.hid = hid;
		this.num = num;
	}
	
	/**
	 * Get the HID
	 * @return hid
	 */
	public final HID getHID(){
		return hid;
	}
	/**
	 * Get the POV number
	 * @return POV number
	 */
	public final int getPOVNumber(){
		return num;
	}
	
	@Override
	public int get(){
		return hid.getRawPOV(num);
	}
}
