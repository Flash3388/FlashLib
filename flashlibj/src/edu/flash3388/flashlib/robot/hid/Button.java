package edu.flash3388.flashlib.robot.hid;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.BooleanSource;

/**
 * Represents a button on a Human Interface Device. Can activate actions depending on the state of 
 * the button.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Button implements ButtonListener, BooleanSource{
	
	protected static enum ActivateType {
		Press, Hold, Release
	}
	private static abstract class ButtonCommand{
		final ActivateType type;
		
		ButtonCommand(ActivateType t){
			type = t;
		}
		
		abstract boolean isRunning();
		abstract void start();
		abstract void cancel();
	}
	private static class ButtonAction extends ButtonCommand{

		final Action action;
		
		ButtonAction(ActivateType t, Action action) {
			super(t);
			this.action = action;
		}

		@Override
		boolean isRunning() {
			return action.isRunning();
		}
		@Override
		void start() {
			action.start();
		}
		@Override
		void cancel() {
			action.cancel();
		}
	}
	private static class ButtonTask extends ButtonCommand{

		final Runnable task;
		private boolean running = false;
		
		ButtonTask(ActivateType t, Runnable task) {
			super(t);
			this.task = task;
		}

		@Override
		boolean isRunning() {
			return running;
		}
		@Override
		void start() {
			if(!running){
				RobotFactory.getScheduler().addTask(task);
				running = true;
			}
		}
		@Override
		void cancel() {
			if(running){
				RobotFactory.getScheduler().remove(task);
				running = false;
			}
		}
	}

	private static final int MAX_MILLIS_PRESS = 500;
	
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
	public final int getJoystick() {return stick;}
	
	/**
	 * Gets the number of the button on its joystick.
	 * 
	 * @return An Integer representing the number of the button on its joystick.
	 */
	public final int getNumber() {return number;}
	
	/**
	 * Gets whether the button is pressed currently
	 * @return True if the button is pressed currently.
	 */
	public final boolean get(){ return current;}
	
	/**
	 * Add an event listener to listen to button events. 
	 * 
	 * @param listener An instance of an object implementing ButtonHold.
	 */
	public final void addListener(ButtonListener listener){ 
		listeners.add(listener);
	}
	
	/**
	 * Adds a Command/Action to automatically start when the button is pressed.
	 * 
	 * @param c The Command/Action to activate on press.
	 */
	public final void whenPressed(Action c){
		commands.add(new ButtonAction(ActivateType.Press, c));
	}
	public final void whenPressed(Action... actions){
		for(Action a : actions)
			commands.add(new ButtonAction(ActivateType.Press, a));
	}
	
	/**
	 * Adds a Command/Action to automatically run while the button is held, stops on release.
	 * 
	 * @param c The Command/Action to activate on hold.
	 */
	public void whileHeld(Action c){
		commands.add(new ButtonAction(ActivateType.Hold, c));
	}
	public void whileHeld(Action... actions){
		for(Action a : actions)
			commands.add(new ButtonAction(ActivateType.Hold, a));
	}
	
	/**
	 * Adds a Command/Action to automatically start when the button is released.
	 * 
	 * @param c The Command/Action to activate on release.
	 */
	public void whenReleased(Action c){
		commands.add(new ButtonAction(ActivateType.Release, c));
	}
	public void whenReleased(Action... actions){
		for(Action a : actions)
			commands.add(new ButtonAction(ActivateType.Release, a));
	}
	
	/**
	 * Adds a task to automatically start when the button is pressed.
	 * 
	 * @param c The task to activate on press.
	 */
	public final void whenPressed(Runnable c){
		commands.add(new ButtonTask(ActivateType.Press, c));
	}
	public final void whenPressed(Runnable... ts){
		for(Runnable a : ts)
			commands.add(new ButtonTask(ActivateType.Press, a));
	}
	
	/**
	 * Adds a task to automatically run while the button is held, stops on release.
	 * 
	 * @param c The task to activate on hold.
	 */
	public void whileHeld(Runnable c){
		commands.add(new ButtonTask(ActivateType.Hold, c));
	}
	public void whileHeld(Runnable... ts){
		for(Runnable a : ts)
			commands.add(new ButtonTask(ActivateType.Hold, a));
	}
	
	/**
	 * Adds a task to automatically start when the button is released.
	 * 
	 * @param c The task to activate on release.
	 */
	public void whenReleased(Runnable c){
		commands.add(new ButtonTask(ActivateType.Release, c));
	}
	public void whenReleased(Runnable... ts){
		for(Runnable a : ts)
			commands.add(new ButtonTask(ActivateType.Release, a));
	}
	
	public void stopAll(){
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
	
	public void set(boolean down){
		last = current;
		current = down;
		changedDown = !last && current;
    	changedUp = last && !current;
    	
    	if(changedDown)
    		holdStart = FlashUtil.millisInt();
    	
    	setCommands(changedUp && (holdStart > 0 && FlashUtil.millisInt() - holdStart < MAX_MILLIS_PRESS));
	}
	public void setPressed(boolean press){
		changedUp = press;
		last = press;
		current = !press;
		changedDown = !press;
		
		setCommands(press);
	}
	protected void setCommands(boolean press){
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
			if(ex.type == ActivateType.Press)
				ex.start();
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
		set(RobotFactory.getHidInterface().getHIDButton(stick, number));
	}
}