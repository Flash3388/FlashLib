package edu.flash3388.flashlib.robot.hid;

/**
 * An extension of {@link Button} for human interface devices. Provides time buffering for activation types.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class HIDButton extends Button {

	private HID hid;
	private int num;
	
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
	public boolean isDown() {
		return hid.getRawButton(num);
	}
}
