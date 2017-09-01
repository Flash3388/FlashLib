package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.util.FlashUtil;

public class HIDButton extends Button{

	private static final int MAX_MILLIS_PRESS = 500;
	
	private HID hid;
	private int num;
	
	private boolean last = false;
	private int holdStart = -1;
	
	/**
	 * Creates a button wrapper object
	 * 
	 * @param hid The hid
	 * @param num the number of the button on the controller.
	 */
	public HIDButton(HID hid, int num){
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
	 * Get the button number
	 * @return button number
	 */
	public final int getButtonNumber(){
		return num;
	}

	/**
	 * Gets the current button state
	 */
	@Override
	public boolean get() {
		return hid.getRawButton(num);
	}
	
	/**
	 * Updates the {@link Button} state. Necessary in order to activate and stop attached {@link Action}s
	 */
	@Override
	public void run(){
		boolean down = get();
		
		if(last && down && holdStart <= 0)
			holdStart = FlashUtil.millisInt();
		
		int timepassed = holdStart > 0? FlashUtil.millisInt() - holdStart : 0;
		if(last && !down){
			holdStart = -1;
			if(timepassed < MAX_MILLIS_PRESS)
				setPressed();
			else
				setReleased();
		}else if(last && down && timepassed > MAX_MILLIS_PRESS)
			setHeld();
		
		last = down;
	}
}
