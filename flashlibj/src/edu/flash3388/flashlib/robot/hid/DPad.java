package edu.flash3388.flashlib.robot.hid;

/**
 * Represents a D-Pad from and XBox controller.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DPad extends POV implements Runnable{
	/**
	 * The Up button on the D-Pad
	 */
	public final DPadButton Up;
	/**
	 * The Down button on the D-Pad
	 */
	public final DPadButton Down;
	/**
	 * The Right button on the D-Pad
	 */
	public final DPadButton Right;
	/**
	 * The Left button on the D-Pad
	 */
	public final DPadButton Left;
	/**
	 * The entire POV as a button
	 */
	public final DPadButton POV;
	
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

	@Override
	public void run() {
		Up.run();
		Down.run();
		Left.run();
		Right.run();
	}
}
