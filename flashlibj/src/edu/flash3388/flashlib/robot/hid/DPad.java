package edu.flash3388.flashlib.robot.hid;

/**
 * This represents a D-Pad from and XBox controller.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DPad extends POV{
	/**
	 * The Up button on the D-Pad
	 */
	public final POVButton Up;
	/**
	 * The Down button on the D-Pad
	 */
	public final POVButton Down;
	/**
	 * The Right button on the D-Pad
	 */
	public final POVButton Right;
	/**
	 * The Left button on the D-Pad
	 */
	public final POVButton Left;
	
	/**
	 * Creates a new instance of DPad, representing the D-Pad of a given Joystick.
	 * 
	 * @param stick The Joystick the D-Pad is on.
	 * @param num the number of the D-Pad on the controller.
	 */
	public DPad(int stick, int num){
		super(stick, num);
		
		Up = new POVButton(stick, num, POVButton.Type.UP);
		Down = new POVButton(stick, num, POVButton.Type.DOWN);
		Right = new POVButton(stick, num, POVButton.Type.RIGHT);
		Left = new POVButton(stick, num, POVButton.Type.LEFT);
	}
	
	public void refresh(){
		int degrees = get();
		Up.set(degrees);
		Down.set(degrees);
		Left.set(degrees);
		Right.set(degrees);
	}
}
