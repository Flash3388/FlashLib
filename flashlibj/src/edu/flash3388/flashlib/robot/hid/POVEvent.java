package edu.flash3388.flashlib.robot.hid;

/**
 * Event object for POV events.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class POVEvent extends ButtonEvent {

	public final POVButton.Type type;
	public int degrees = -1;
	
	public POVEvent(String name, int joystick, int num, POVButton.Type t){
		super(name, joystick, num);
		this.type = t;
	}
}
