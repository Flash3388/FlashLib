package edu.flash3388.flashlib.robot.hid;

/**
 * This represents a D-Pad from and XBox controller.
 * 
 * @author Tom Tzook
 */
public abstract class DPad {
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
	
	/**
	 * Creates a new instance of DPad, representing the D-Pad of a given Joystick.
	 * 
	 * @param stick The Joystick the D-Pad is on.
	 */
	public DPad(int stick){
		POV = new POVButton("POV", stick, POVButton.Type.ALL);
		Up = new POVButton("POV Up", stick, POVButton.Type.UP);
		Down = new POVButton("POV Down", stick, POVButton.Type.DOWN);
		Right = new POVButton("POV Right", stick, POVButton.Type.RIGHT);
		Left = new POVButton("POV Left", stick, POVButton.Type.LEFT);
	}
	
	public abstract int get();
	
	public void refresh() {
		POV.refresh();
		Up.refresh();
		Down.refresh();
		Right.refresh();
		Left.refresh();
	}
}
