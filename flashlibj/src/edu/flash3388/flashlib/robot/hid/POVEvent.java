package edu.flash3388.flashlib.robot.hid;


public class POVEvent extends ButtonEvent {

	public final POVButton.Type type;
	public int degrees = -1;
	
	public POVEvent(String name, int joystick, POVButton.Type t){
		super(name, joystick, 0);
		this.type = t;
	}
}
