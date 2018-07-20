package edu.flash3388.flashlib.robot.scheduling;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Provides a series of scheduling to run in a order. Action can run sequentially or parallel to one another.
 * <p>
 * Sequential scheduling cannot run if another action from the group is active currently. Parallel action will run at all
 * conditions.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class ActionGroup extends Action {

	private static class Entry{
		Action action;
		boolean sequential;
	}
	
	private Vector<Entry> actions = new Vector<Entry>(5);
	private int index = -1;
	private Vector<Entry> current = new Vector<Entry>(5);
	
	/**
	 * Creates a new empty action group
	 */
	public ActionGroup(){}
	/**
	 * Creates an action group and adds an array of scheduling to run sequentially.
	 * @param actions array of scheduling to add
	 */
	public ActionGroup(Action...actions){
		this(true, actions);
	}
	/**
	 * Creates an action group and adds an array of scheduling to run.
	 * @param actions array of scheduling to add
	 * @param sequential if true, the scheduling will run sequentially, otherwise they will run in parallel
	 */
	public ActionGroup(boolean sequential, Action...actions){
		if(sequential)
			addSequential(actions);
		else addParallel(actions);
	}
	
	/**
	 * Adds an action to run sequentially with a timeout in seconds.
	 * 
	 * @param action action to run
	 * @param timeout timeout in seconds for the action
	 * @return this instance
	 */
	public ActionGroup addSequential(Action action, double timeout){
		addSequential(new TimedAction(action, timeout));
		return this;
	}
	/**
	 * Adds an action to run sequentially.
	 * 
	 * @param action action to run
	 * @return this instance
	 */
	public ActionGroup addSequential(Action action){
		Entry entry = new Entry();
		entry.action = action;
		entry.sequential = true;
		this.actions.add(entry);
		return this;
	}
	/**
	 * Adds an array of scheduling to run sequentially.
	 * 
	 * @param actions action to run
	 * @return this instance
	 */
	public ActionGroup addSequential(Action... actions){
		for(Action action : actions)
			addSequential(action);
		return this;
	}
	/**
	 * Adds an empty action to run for few seconds.
	 * 
	 * @param seconds seconds for the empty action to run
	 * @return this instance
	 */
	public ActionGroup addWaitAction(double seconds){
		addSequential(new TimedAction(Action.EMPTY, seconds));
		return this;
	}
	
	/**
	 * Adds an action to run in parallel with a timeout in seconds.
	 * 
	 * @param action action to run
	 * @param timeout timeout in seconds for the action
	 * @return this instance
	 */
	public ActionGroup addParallel(Action action, double timeout){
		addParallel(new TimedAction(action, timeout));
		return this;
	}
	/**
	 * Adds an action to run in parallel.
	 * 
	 * @param action action to run
	 * @return this instance
	 */
	public ActionGroup addParallel(Action action){
		Entry entry = new Entry();
		entry.action = action;
		entry.sequential = false;
		this.actions.add(entry);
		return this;
	}
	/**
	 * Adds an array of scheduling to run in parallel.
	 * 
	 * @param actions action to run
	 * @return this instance
	 */
	public ActionGroup addParallel(Action... actions){
		for(Action action : actions)
			addParallel(action);
		return this;
	}
	
	@Override
	protected void initialize(){
		index = 0;
		Entry c = actions.elementAt(index);
		c.action.start();
		current.addElement(c);
	}
	@Override
	protected void execute() {
		Entry c = actions.elementAt(index); boolean next = false;
		if(!c.action.isRunning()){
			current.removeElement(c);
			index++;
			next = true;
		}
		else if(!c.sequential){
			index++;
			next = true;
		}
		
		for(Enumeration<Entry> en = current.elements(); en.hasMoreElements();){
			 Entry entry = en.nextElement();
			 if(!entry.action.isRunning()){
				 current.remove(entry);
			 }
		}
		
		if(next && index < actions.size()){
			Entry toRun = actions.elementAt(index);
			toRun.action.start();
			current.add(toRun);
		}
	}
	@Override
	protected boolean isFinished() {
		return index >= actions.size();
	}
	@Override
	protected void end() {
		for(Enumeration<Entry> en = current.elements(); en.hasMoreElements();){
			Entry entry = en.nextElement();
			if(entry.action.isRunning())
				entry.action.cancel();
		}
		current.clear();
		index = -1;
	}
}
