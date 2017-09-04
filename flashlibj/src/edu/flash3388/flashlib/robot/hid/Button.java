package edu.flash3388.flashlib.robot.hid;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.util.beans.BooleanSource;

/**
 * The base logic for a button. Allows attaching {@link Action} objects which will be executed 
 * according to different parameters. To allow execution of actions, it is required to call {@link #run()}
 * periodically, so that the activation parameters will be checked.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Button implements BooleanSource, Runnable{
	
	/**
	 * Enumeration of activation types for {@link Action} associated with a {@link Button}.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static enum ActivateType {
		Press, Hold, Release
	}
	
	/**
	 * Wrapper for {@link Action} objects associated with a {@link Button}.
	 * When the {@link ActivateType} for the {@link Action} was met, this will start
	 * the {@link Action}.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static class ButtonAction{
		protected final ActivateType type;
		protected final Action action;
		
		ButtonAction(ActivateType t, Action action){
			type = t;
			this.action = action;
		}
		
		public final Action getAction(){
			return action;
		}
		public final ActivateType getActivateType(){
			return type;
		}
		
		public void start(){
			if(!action.isRunning())
				action.start();
		}
		public void stop(){
			if(action.isRunning())
				action.cancel();
		}
	}
	/**
	 * Wrapper for {@link Action} objects associated with a {@link Button}.
	 * When the {@link ActivateType} for the {@link Action} was met, this will cancel
	 * the {@link Action}.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.2
	 */
	public static class CancelAction extends ButtonAction{
		
		public CancelAction(ActivateType t, Action action){
			super(t, action);
		}
		
		public void start(){
			if(action.isRunning())
				action.cancel();
		}
		public void stop(){
		}
	}
	/**
	 * Wrapper for {@link Action} objects associated with a {@link Button}.
	 * When the {@link ActivateType} for the {@link Action} was met, this will toggle the
	 * {@link Action} between start and stop.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.2
	 */
	public static class ToggleAction extends ButtonAction{
		
		public ToggleAction(ActivateType t, Action action){
			super(t, action);
		}
		
		public void start(){
			if(!action.isRunning())
				action.start();
			else 
				action.cancel();
		}
		public void stop(){
		}
	}
	/**
	 * Wrapper for {@link Action} objects associated with a {@link Button}.
	 * When {@link ActivateType#Press} has occurred a given amount of time, this will 
	 * start the {@link Action}.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.2
	 */
	public static class MultiPressAction extends ButtonAction{
		
		private int presses = 0;
		private int pressesStart;
		
		public MultiPressAction(Action action, int presses){
			super(ActivateType.Press, action);
			this.pressesStart = presses;
		}
		
		public void start(){
			if(!action.isRunning()){
				if((++presses) == pressesStart){
					action.start();
					presses = 0;
				}
			}
			else if(presses != 0)
				presses = 0;
		}
		public void stop(){
			presses = 0;
			if(action.isRunning())
				action.cancel();
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
	 * Adds a new {@link ButtonAction} to this button. Will be update to check for activation
	 * when {@link #run()} is called.
	 * @param action button action to add
	 */
	public void addButtonAction(ButtonAction action){
		actions.add(action);
	}
	/**
	 * Adds an {@link Action} which will be started when the button is pressed
	 * @param action an action to add
	 */
	public void whenPressed(Action action){
		actions.add(new ButtonAction(ActivateType.Press, action));
	}
	/**
	 * Adds an {@link Action} which will be started when the button is released
	 * @param action an action to add
	 */
	public void whenReleased(Action action){
		actions.add(new ButtonAction(ActivateType.Release, action));
	}
	/**
	 * Adds an {@link Action} which will be started when the button is held
	 * @param action an action to add
	 */
	public void whileHeld(Action action){
		actions.add(new ButtonAction(ActivateType.Hold, action));
	}
	/**
	 * Adds an {@link Action} which will be canceled when the button is pressed
	 * @param action an action to add
	 */
	public void cancelWhenPressed(Action action){
		actions.add(new CancelAction(ActivateType.Press, action));
	}
	/**
	 * Adds an {@link Action} which when pressed and if not running will start, and if
	 * running will be canceled.
	 * @param action an action to add
	 */
	public void toggleWhenPressed(Action action){
		actions.add(new ToggleAction(ActivateType.Press, action));
	}
	/**
	 * Adds an {@link Action} which will run when the button was pressed multiple times continuously
	 * @param action an action to add
	 * @param presses amount of presses
	 */
	public void whenMultiPressed(Action action, int presses){
		actions.add(new MultiPressAction(action, presses));
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
