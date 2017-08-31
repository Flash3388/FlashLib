package edu.flash3388.flashlib.robot.hid;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.util.FlashUtil;

import edu.flash3388.flashlib.util.beans.BooleanProperty;

/**
 * Represents a button on a Human Interface Device. Can activate actions depending on the state of 
 * the button.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Button implements BooleanProperty{

	private static enum ActivateType {
		Press, Hold, Release
	}
	private static class ButtonCommand{
		final ActivateType type;
		final Action action;
		
		ButtonCommand(ActivateType t, Action action){
			type = t;
			this.action = action;
		}
	}
	
	private static final int MAX_MILLIS_PRESS = 500;
	
	private boolean current = false, last = false, changedDown = false, changedUp = false;
	private int number;
	private int stick; 
	private int holdStart = -1;
	
	private Vector<ButtonCommand> commands = new Vector<ButtonCommand>();
	
	/**
	 * Creates a new instance of Button class for a button in a given joystick. 
	 * 
	 * @param stick An instance of Joystick representing the joystick the button belongs to.
	 * @param number The number of the button on the joystick.
	 */
	public Button(int stick, int number){
		this.stick = stick;
		this.number = number;
	}
	
	private void onPress(){
		Enumeration<ButtonCommand> commEnum = commands.elements();
		while(commEnum.hasMoreElements()){
			ButtonCommand ex = commEnum.nextElement();
			if(ex.type == ActivateType.Press)
				ex.action.start();
			else if(ex.type == ActivateType.Hold || ex.type == ActivateType.Release)
				ex.action.cancel();
		}
	}
	private void onRelease(){
		Enumeration<ButtonCommand> commEnum = commands.elements();
		while(commEnum.hasMoreElements()){
			ButtonCommand ex = commEnum.nextElement();
			if(ex.type == ActivateType.Hold || ex.type == ActivateType.Press)
				ex.action.cancel();
			else if(ex.type == ActivateType.Release)
				ex.action.start();
		}
	}
	private void onHold(){
		Enumeration<ButtonCommand> commEnum = commands.elements();
		while(commEnum.hasMoreElements()){
			ButtonCommand ex = commEnum.nextElement();
			if(ex.type == ActivateType.Hold)
				ex.action.start();
			else if(ex.type == ActivateType.Release || ex.type == ActivateType.Press)
				ex.action.cancel();
		}
	}
	private void setCommands(boolean press, boolean hold, boolean release){
		if(hold || press || release)
			holdStart = -1;
		
		if(press)
			onPress();
		else if(hold)
			onHold();
		else if(release)
			onRelease();
	}
	
	/**
	 * Get the HID channel
	 * @return hid channel
	 */
	public final int getChannel(){
		return stick;
	}
	/**
	 * Get the button number
	 * @return button number
	 */
	public final int getButtonNumber(){
		return number;
	}
	
	/**
	 * Gets if the button state was changed and is now held down.
	 * 
	 * @return True if the button has changed down.
	 */
	public final boolean changedDown() { return changedDown;}
	
	/**
	 * Gets if the button state was changed and is now released.
	 * 
	 * @return True if the button has changed up.
	 */
	public final boolean changedUp() { return changedUp;}
	
	/**
	 * Gets if the button is now held.
	 * 
	 * @return True if the button is held down.
	 */
	public final boolean isHeld() { return current && last;}
	
	/**
	 * Gets if the button state was changed in any way (up or down).
	 * 
	 * @return True if the button state was changed.
	 */
	public final boolean hasChanged() { return changedDown || changedUp;}
	
	/**
	 * Gets whether the button is pressed currently
	 * @return True if the button is pressed currently.
	 */
	public final boolean get(){ return current;}

	/**
	 * Gets whether the button is pressed currently
	 * @return True if the button is pressed currently.
	 */
	@Override
	public final Boolean getValue() { return get(); }
	
	/**
	 * Sets the value of the button
	 * @param down true if the button is down, false otherwise
	 */
	@Override
	public final void set(boolean down){
		last = current;
		current = down;
		changedDown = !last && current;
    	changedUp = last && !current;
    	
    	if(changedDown)
    		holdStart = FlashUtil.millisInt();
    	
    	int timepassed = FlashUtil.millisInt() - holdStart;
    	boolean pressed = (holdStart > 0 && timepassed < MAX_MILLIS_PRESS);
    	setCommands(
    			changedUp && pressed,
    			last && current && (holdStart > 0 && timepassed > MAX_MILLIS_PRESS),
    			changedUp && !pressed
    			);
	}
	
	/**
	 * Sets the value of the button
	 * @param o true if the button is down, false otherwise
	 */
	@Override
	public final void setValue(Boolean o) {
		set(o == null? false : o.booleanValue());
	}
	
	/**
	 * Sets the button to pressed or unpressed modes
	 * @param press true for pressed mode, false for unpressed
	 */
	public final void setPressed(boolean press){
		changedUp = press;
		last = press;
		current = !press;
		changedDown = !press;
		
		setCommands(press, false, false);
	}
	
	/**
	 * Adds an {@link Action} which will be started when the button is pressed
	 * @param action an action to add
	 */
	public final void whenPressed(Action action){
		commands.add(new ButtonCommand(ActivateType.Press, action));
	}
	/**
	 * Adds an {@link Action} which will be started when the button is released
	 * @param action an action to add
	 */
	public final void whenReleased(Action action){
		commands.add(new ButtonCommand(ActivateType.Release, action));
	}
	/**
	 * Adds an {@link Action} which will be started when the button is held
	 * @param action an action to add
	 */
	public final void whileHeld(Action action){
		commands.add(new ButtonCommand(ActivateType.Hold, action));
	}
	
	public final boolean actionsStillRunning(){
		Enumeration<ButtonCommand> commEnum = commands.elements();
		while(commEnum.hasMoreElements()){
			if(commEnum.nextElement().action.isRunning())
				return true;
		}
		return false;
	}
	
	public void refresh(){
		set(RobotFactory.getImplementation().getHIDInterface().getHIDButton(stick, number));
	}
}