package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.RobotFactory;

/**
 * This represents a D-Pad from and XBox controller.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DPad {
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
	
	public final POVButton POV;
	
	private int stick;
	private int num;
	
	/**
	 * Creates a new instance of DPad, representing the D-Pad of a given Joystick.
	 * 
	 * @param stick The Joystick the D-Pad is on.
	 * @param num the number of the D-Pad on the controller.
	 */
	public DPad(int stick, int num){
		this.stick = stick;
		this.num = num;
		
		POV = new POVButton("POV", stick, num, POVButton.Type.ALL);
		Up = new POVButton("POV Up", stick, num, POVButton.Type.UP);
		Down = new POVButton("POV Down", stick, num, POVButton.Type.DOWN);
		Right = new POVButton("POV Right", stick, num, POVButton.Type.RIGHT);
		Left = new POVButton("POV Left", stick, num, POVButton.Type.LEFT);
	}
	
	public int get(){
		return RobotFactory.getImplementation().getHIDInterface().getHIDPOV(stick, num);
	}
	
	public void refresh(){
		int degrees = get();
		Up.set(degrees);
		Down.set(degrees);
		Left.set(degrees);
		Right.set(degrees);
		POV.set(degrees);
	}
}
