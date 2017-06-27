package edu.flash3388.flashlib.robot.hid;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.robot.RobotState;
import edu.flash3388.flashlib.robot.devices.BooleanDataSource;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Represents a button on a Human Interface Device. Can activate actions depending on the state of 
 * the button.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Button implements ButtonListener, BooleanDataSource{
	
	protected static class ButtonCommand{
		public final ActivateType type;
		private Action action;
		
		public ButtonCommand(Action c, ActivateType t){
			action = c;
			type = t;
		}
		
		public boolean isRunning(){
			return action.isRunning();
		}
		public void start(){
			if(action != null) action.start();
		}
		public void cancel(){
			if(action != null) action.cancel();
		}
	}
	protected static enum ActivateType {
		Press, Hold, Release
	}
	
	private boolean current = false, last = false, changedDown = false, changedUp = false;
	private String name;
	private int number;
	private int stick; 
	protected ButtonEvent event;
	private int holdStart = -1;
	
	private Vector<ButtonListener> listeners = new Vector<ButtonListener>();
	private Vector<ButtonCommand> commands = new Vector<ButtonCommand>();
	
	/**
	 * Creates a new instance of Button class for a button in a given joystick. 
	 * A name is generated for the given joystick.
	 * 
	 * @param stick An instance of Joystick representing the joystick the button belongs to.
	 * @param number The number of the button on the joystick.
	 */
	public Button(int stick, int number){
		this(stick+"-"+number, stick, number);
	}
	
	/**
	 * Creates a new instance of Button class for a button in a given joystick. 
	 * 
	 * @param name A name to represent the Button.
	 * @param stick An instance of Joystick representing the joystick the button belongs to.
	 * @param number The number of the button on the joystick.
	 */
	public Button(String name, int stick, int number){
		this.name = name;
		this.stick = stick;
		this.number = number;
		this.event = new ButtonEvent(name, stick, number);
		
		addListener(this);
	}
	
	/**
	 * Gets if the button state was changed and is now held down.
	 * 
	 * @return True if the button has changed down.
	 */
	public synchronized boolean changedDown() { return changedDown;}
	
	/**
	 * Gets if the button state was changed and is now released.
	 * 
	 * @return True if the button has changed up.
	 */
	public synchronized boolean changedUp() { return changedUp;}
	
	/**
	 * Gets if the button is now held.
	 * 
	 * @return True if the button is held down.
	 */
	public synchronized boolean isHeld() { return current && last;}
	
	/**
	 * Gets if the button state was changed in any way (up or down).
	 * 
	 * @return True if the button state was changed.
	 */
	public synchronized boolean hasChanged() { return changedDown || changedUp;}
	
	/**
	 * Gets the name representing the button. Either auto-generated or manually given.
	 * 
	 * @return A String representing the name.
	 */
	public String getName() {return name;}
	
	/**
	 * Gets the Joystick the button is on.
	 * 
	 * @return An instance of Joystick.
	 */
	public int getJoystick() {return stick;}
	
	/**
	 * Gets the number of the button on its joystick.
	 * 
	 * @return An Integer representing the number of the button on its joystick.
	 */
	public int getNumber() {return number;}
	
	/**
	 * Gets whether the button is pressed currently
	 * @return True if the button is pressed currently.
	 */
	public synchronized boolean get(){ return current;}
	
	/**
	 * Add an event listener to listen to button events. 
	 * 
	 * @param listener An instance of an object implementing ButtonHold.
	 */
	public void addListener(ButtonListener listener){ 
		listeners.add(listener);
	}
	
	/**
	 * Adds a Command/Action to automatically start when the button is pressed.
	 * 
	 * @param c The Command/Action to activate on press.
	 */
	public void whenPressed(Action c){
		commands.add(new ButtonCommand(c, ActivateType.Press));
	}
	public void whenPressed(Action... actions){
		for(Action a : actions)
			commands.add(new ButtonCommand(a, ActivateType.Press));
	}
	
	/**
	 * Adds a Command/Action to automatically run while the button is held, stops on release.
	 * 
	 * @param c The Command/Action to activate on hold.
	 */
	public void whileHeld(Action c){
		commands.add(new ButtonCommand(c, ActivateType.Hold));
	}
	public void whileHeld(Action... actions){
		for(Action a : actions)
			commands.add(new ButtonCommand(a, ActivateType.Hold));
	}
	
	/**
	 * Adds a Command/Action to automatically start when the button is released.
	 * 
	 * @param c The Command/Action to activate on release.
	 */
	public void whenReleased(Action c){
		commands.add(new ButtonCommand(c, ActivateType.Release));
	}
	public void whenReleased(Action... actions){
		for(Action a : actions)
			commands.add(new ButtonCommand(a, ActivateType.Release));
	}
	
	public synchronized void stopAll(){
		Enumeration<ButtonCommand> commEnum = commands.elements();
		while(commEnum.hasMoreElements()){
			ButtonCommand com = commEnum.nextElement();
			if(com.isRunning())
				com.cancel();
		}
	}
	public boolean actionsStillRunning(){
		boolean run = false;
		Enumeration<ButtonCommand> commEnum = commands.elements();
		while(commEnum.hasMoreElements()){
			ButtonCommand com = commEnum.nextElement();
			if(com.isRunning()){
				run = true;
				break;
			}
		}
		return run;
	}
	
	public synchronized void set(boolean down){
		last = current;
		current = down;
		changedDown = !last && current;
    	changedUp = last && !current;
    	
    	if(changedDown)
    		holdStart = FlashUtil.millisInt();
    	
    	setCommands(changedUp && (holdStart > 0 && FlashUtil.millisInt() - holdStart < 500));
	}
	public synchronized void setPressed(boolean press){
		changedUp = press;
		last = press;
		current = !press;
		changedDown = !press;
		
		setCommands(press);
	}
	protected void setCommands(boolean press){
		if(!RobotState.isRobotTeleop()) return;
		
		if(press) holdStart = -1;
		Enumeration<ButtonListener> listenersEnum = listeners.elements();
		while(listenersEnum.hasMoreElements()){
			ButtonListener listener = listenersEnum.nextElement();
			
			if(press)
				listener.onPress(event);
			else if(last && current)
				listener.onHold(event);
			else if(changedUp)
				listener.onRelease(event);
		}
	}
	
	@Override
	public void onPress(ButtonEvent e) {
		Enumeration<ButtonCommand> commEnum = commands.elements();
		while(commEnum.hasMoreElements()){
			ButtonCommand ex = commEnum.nextElement();
			if(ex.type == ActivateType.Press){
				ex.start();
			}
		}
	}
	@Override
	public void onRelease(ButtonEvent e) {
		Enumeration<ButtonCommand> commEnum = commands.elements();
		while(commEnum.hasMoreElements()){
			ButtonCommand ex = commEnum.nextElement();
			if(ex.type == ActivateType.Hold)
				ex.cancel();
			else if(ex.type == ActivateType.Release)
				ex.start();
		}
	}
	@Override
	public void onHold(ButtonEvent e) {
		Enumeration<ButtonCommand> commEnum = commands.elements();
		while(commEnum.hasMoreElements()){
			ButtonCommand ex = commEnum.nextElement();
			if(ex.type == ActivateType.Hold)
				ex.start();
			else if(ex.type == ActivateType.Release)
				ex.cancel();
		}
	}
	
	public void refresh(){
		set(RobotFactory.getStickButton(getJoystick(), (byte)getNumber()));
	}
}