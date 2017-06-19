package edu.flash3388.flashlib.robot.hid;

/**
 * Contains information of any button events.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class ButtonEvent {
	/**
	 * The number of the button pressed.
	 */
	public final int number;
	/**
	 * The joystick of the button pressed.
	 */
	public final int joystick;
	/**
	 * The name of the button pressed.
	 */
	public final String name;
	
	public ButtonEvent(String name, int joystick, int number){
		this.joystick = joystick;
		this.number = number;
		this.name = name;
	}
}
