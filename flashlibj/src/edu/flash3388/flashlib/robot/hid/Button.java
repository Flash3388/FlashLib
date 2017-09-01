package edu.flash3388.flashlib.robot.hid;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.util.beans.BooleanSource;

public abstract class Button implements BooleanSource, Runnable{
	
	private static enum ActivateType {
		Press, Hold, Release
	}
	private static class ButtonAction{
		final ActivateType type;
		final Action action;
		
		ButtonAction(ActivateType t, Action action){
			type = t;
			this.action = action;
		}
		
		void start(){
			if(!action.isRunning())
				action.start();
		}
		void stop(){
			if(action.isRunning())
				action.cancel();
		}
	}
	private static class CancelAction extends ButtonAction{
		CancelAction(ActivateType t, Action action){
			super(t, action);
		}
		
		void start(){
			if(action.isRunning())
				action.cancel();
		}
		void stop(){
		}
	}
	private static class ToggleAction extends ButtonAction{
		ToggleAction(ActivateType t, Action action){
			super(t, action);
		}
		
		void start(){
			if(!action.isRunning())
				action.start();
			else 
				action.cancel();
		}
		void stop(){
		}
	}
	
	private Vector<ButtonAction> actions = new Vector<ButtonAction>();
	
	private void setActions(ActivateType type){
		Enumeration<ButtonAction> commEnum = actions.elements();
		while(commEnum.hasMoreElements()){
			ButtonAction ex = commEnum.nextElement();
			
			if(type == ex.type)
				ex.start();
			else
				ex.stop();
		}
	}
	
	/**
	 * Adds an {@link Action} which will be started when the button is pressed
	 * @param action an action to add
	 */
	public final void whenPressed(Action action){
		actions.add(new ButtonAction(ActivateType.Press, action));
	}
	/**
	 * Adds an {@link Action} which will be started when the button is released
	 * @param action an action to add
	 */
	public final void whenReleased(Action action){
		actions.add(new ButtonAction(ActivateType.Release, action));
	}
	/**
	 * Adds an {@link Action} which will be started when the button is held
	 * @param action an action to add
	 */
	public final void whileHeld(Action action){
		actions.add(new ButtonAction(ActivateType.Hold, action));
	}
	/**
	 * Adds an {@link Action} which will be canceled when the button is pressed
	 * @param action an action to add
	 */
	public final void cancelWhenPressed(Action action){
		actions.add(new CancelAction(ActivateType.Press, action));
	}
	/**
	 * Adds an {@link Action} which when pressed and if not running will start, and if
	 * not running will be canceled.
	 * @param action an action to add
	 */
	public final void toggleWhenPressed(Action action){
		actions.add(new ToggleAction(ActivateType.Press, action));
	}
	
	/**
	 * Gets whether or not at least one attached {@link Action} is running.
	 * @return true if at least one is running, false otherwise
	 */
	public final boolean actionsRunning(){
		Enumeration<ButtonAction> commEnum = actions.elements();
		while(commEnum.hasMoreElements()){
			if(commEnum.nextElement().action.isRunning())
				return true;
		}
		return false;
	}
	
	/**
	 * Sets the current activation mode for this button as pressed. Will update attached {@link Action}s.
	 */
	public void setPressed(){
		setActions(ActivateType.Press);
	}
	/**
	 * Sets the current activation mode for this button as held. Will update attached {@link Action}s.
	 */
	public void setHeld(){
		setActions(ActivateType.Hold);
	}
	/**
	 * Sets the current activation mode for this button as released. Will update attached {@link Action}s.
	 */
	public void setReleased(){
		setActions(ActivateType.Release);
	}
}
