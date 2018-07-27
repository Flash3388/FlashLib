package edu.flash3388.flashlib.robot.hid;

/**
 * Represents a D-Pad from and XBox controller.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DPad extends POV {
	/**
	 * The Up button on the D-Pad
	 */
	public final Button Up;

	/**
	 * The Down button on the D-Pad
	 */
	public final Button Down;

	/**
	 * The Right button on the D-Pad
	 */
	public final Button Right;

	/**
	 * The Left button on the D-Pad
	 */
	public final Button Left;

	/**
	 * The entire POV as a button
	 */
	public final Button POV;
	
	/**
	 * Creates a new instance of DPad, representing the D-Pad of a given Joystick.
	 * 
	 * @param hid The hid the D-Pad is on.
	 * @param num the number of the D-Pad on the controller.
	 */
	public DPad(HID hid, int num){
		super(hid, num);
		
		Up = new DPadButton(hid, num, DPadButton.Type.UP);
		Down = new DPadButton(hid, num, DPadButton.Type.DOWN);
		Right = new DPadButton(hid, num, DPadButton.Type.RIGHT);
		Left = new DPadButton(hid, num, DPadButton.Type.LEFT);
		POV = new DPadButton(hid, num, DPadButton.Type.POV);
	}

	/**
	 * Gets the up DPad button object
	 * @return up button
	 */
	public Button getUp(){
		return Up;
	}

	/**
	 * Gets the down DPad button object
	 * @return down button
	 */
	public Button getDown(){
		return Down;
	}

	/**
	 * Gets the right DPad button object
	 * @return right button
	 */
	public Button getRight(){
		return Right;
	}

	/**
	 * Gets the left DPad button object
	 * @return left button
	 */
	public Button getLeft(){
		return Left;
	}
}
