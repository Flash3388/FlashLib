package edu.flash3388.flashlib.robot;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Allows to combine multiple actions to run together on the same system. Does not add the actions to the Scheduler, but
 * instead runs them manually.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class CombinedAction extends Action{
	
	private Vector<Action> actions = new Vector<Action>();
	private ArrayList<Action> currentActions = new ArrayList<Action>();
	
	/**
	 * Adds a new action to run.
	 * @param part action to run
	 */
	protected void add(Action part){
		if(part == null)
			 return;
		actions.add(part);
	}
	/**
	 * Gets the action at the given index.
	 * @param index the index of the action
	 * @return action at the given index
	 */
	protected Action get(int index){
		return actions.get(index);
	}
	/**
	 * Removes an action from the added actions.
	 * @param part action to remove
	 */
	protected void remove(Action part){
		if(part == null)
			 return;
		actions.remove(part);
	}
	/**
	 * Removes an action from the added action by index.
	 * @param index index of action to remove.
	 */
	protected void remove(int index){
		actions.remove(index);
	}
	
	@Override
	protected void initialize(){ 
		for (Enumeration<Action> parts = actions.elements(); parts.hasMoreElements();) {
			Action part = parts.nextElement();
			part.initialize();
			currentActions.add(part);
		}
	}
	@Override
	protected void execute(){
		for (int i = 0; i < currentActions.size(); i++)
			currentActions.get(i).execute();
	}
	@Override
	protected boolean isFinished(){ 
		boolean is = true;
		for (int i = currentActions.size() - 1; i >= 0; i--) {
			Action part = currentActions.get(i);
			if(!part.isFinished()){
				is = false;
				break;
			}
		}
		return is;
	}
	@Override
	protected void end(){
		for (int i = currentActions.size() - 1; i >= 0; i--) {
			currentActions.get(i).end();
			currentActions.remove(i);
		}
	}

}
