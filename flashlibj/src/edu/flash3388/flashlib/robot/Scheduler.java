package edu.flash3388.flashlib.robot;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Scheduler is responsible for executing actions for robot systems. Scheduler contains a collection for actions,
 * systems and scheduled tasks. When a system is created it is added automatically by the constructor. Actions are 
 * added when they are started. ScheduledTasks are added manually. To avoid collisions between actions running on the
 * same system, when an action is added its system requirements are checked and any actions using those systems are canceled.
 * If registered systems do not gave an action running but do have a default action, the default action is started.
 * 
 * <p>
 * To run the scheduler, it is necessary to manually call {@link #run()}.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public final class Scheduler {
	
	private static abstract class TaskWrapper{
		boolean removeOnFinish;
		
		TaskWrapper(boolean removeFinshed){
			this.removeOnFinish = removeFinshed;
		}
		
		abstract boolean run();
	}
	private static class RunnableWrapper extends TaskWrapper{
		Runnable runnable;
		
		RunnableWrapper(Runnable runnable, boolean removeOnFinish){
			super(removeOnFinish);
			this.runnable = runnable;
		}

		@Override
		boolean run() {
			runnable.run();
			return true;
		}
		
		@Override
		public boolean equals(Object obj) {
			return super.equals(obj) || runnable.equals(obj);
		}
		@Override
		public String toString() {
			return runnable.toString();
		}
	}
	
	public static final byte MODE_DISABLED = 0x0;
	public static final byte MODE_TASKS = 0x1;
	public static final byte MODE_ACTIONS = 0x2;
	public static final byte MODE_FULL = MODE_ACTIONS | MODE_TASKS;
	
	private static Scheduler instance;
	
	private byte mode = MODE_FULL;
	
	private Vector<Action> actions = new Vector<Action>();
	private Set<Subsystem> systems = new HashSet<Subsystem>();
	private Vector<TaskWrapper> tasks = new Vector<TaskWrapper>();
	
	private Scheduler(){}
	
	/**
	 * Runs the scheduler. 
	 */
	public void run(){
		if(isDisabled()) return;
		
		if(isMode(MODE_TASKS) && tasks.size() > 0){
			TaskWrapper taskWrapper = null;
			for(Enumeration<TaskWrapper> taskEnum = tasks.elements(); taskEnum.hasMoreElements();){
				taskWrapper = taskEnum.nextElement();
				if(!taskWrapper.run() || taskWrapper.removeOnFinish)
					tasks.remove(taskWrapper);
			}
		}
		
		if(isMode(MODE_ACTIONS) && actions.size() > 0){
			Action action = null;
			for(Enumeration<Action> actionEnum = actions.elements(); actionEnum.hasMoreElements();){
				action = actionEnum.nextElement();
				if(!action.run())
					remove(action);
			}
		}
		
		if(isMode(MODE_ACTIONS) && systems.size() > 0){
			Subsystem system = null;
			for(Iterator<Subsystem> systemEnum = systems.iterator(); systemEnum.hasNext();){
				system = systemEnum.next();
				if(!system.hasCurrentAction())
					system.startDefaultAction();
			}
		}
	}
	
	/**
	 * Adds a new {@link Runnable} to be executed continuously until manually removed.
	 * @param runnable task to execute
	 * @return true if the task was added, false if the task already exists
	 */
	public boolean addTask(Runnable runnable){
		if(!tasks.contains(runnable)){
			tasks.addElement(new RunnableWrapper(runnable, false));
			return true;
		}
		return false;
	}
	/**
	 * Adds a new {@link Runnable} to be executed once.
	 * @param runnable task to execute
	 * @return true if the task was added, false if the task already exists
	 */
	public boolean execute(Runnable runnable){
		if(!tasks.contains(runnable)){
			tasks.addElement(new RunnableWrapper(runnable, true));
			return true;
		}
		return false;
	}
	/**
	 * Removes a {@link Runnable} from execution.
	 * @param runnable task to remove
	 * @return true if the task was in execution, false otherwise
	 */
	public boolean remove(Runnable runnable){
		return tasks.remove(runnable);
	}
	
	/**
	 * Registers a {@link Subsystem} to this {@link Scheduler}. Allows for activation of
	 * default {@link Action} for this system.
	 * @param system system to register
	 */
	public void registerSystem(Subsystem system){
		systems.add(system);
	}
	
	/**
	 * Adds a new {@link Action} to be executed by the scheduler. The action's system requirements are checked. If
	 * other actions use those same systems, those actions are canceled. If the scheduler is disabled, the action
	 * cannot be added.
	 * 
	 * @param action action to be added
	 * @return true if the action was successfully added
	 */
	public boolean add(Action action){
		if(isDisabled()) return false;
		
		Iterator<Subsystem> requirements = action.getRequirements();
		while(requirements.hasNext()){
			Subsystem system = requirements.next();
			if(system.hasCurrentAction())
				remove(system.getCurrentAction());
			system.setCurrentAction(action);
		}
		actions.addElement(action);
		
		return true;
	}
	/**
	 * Removes an {@link Action} from the scheduler. If the action is in the scheduler, it is removed and its required systems
	 * are updated as lacking a current action.
	 * @param action action to remove.
	 */
	public void remove(Action action){
		if(actions.removeElement(action)){
			Iterator<Subsystem> requirements = action.getRequirements();
			while(requirements.hasNext())
				requirements.next().setCurrentAction(null);
			
			action.removed();
		}
	}
	/**
	 * Removes all the {@link Action}s from the scheduler.
	 */
	public void removeAllActions(){
		Enumeration<Action> actionEnum = actions.elements();
		for (;actionEnum.hasMoreElements();)
			remove(actionEnum.nextElement());
	}
	
	/**
	 * Gets whether or not the given mode is active. This can be used to check if mode parts 
	 * ({@link #MODE_ACTIONS} or {@link #MODE_TASKS}) are applied.
	 * 
	 * @param mode the mode to check
	 * @return true id the mode is appliec
	 */
	public boolean isMode(byte mode){
		return (this.mode & mode) != 0;
	}
	/**
	 * Sets the current mode of this scheduler. The mode dictates the opeation of this scheduler when 
	 * {@link #run()} is called.
	 * @param mode the mode
	 */
	public void setMode(byte mode){
		this.mode = mode;
	}
	/**
	 * Gets the current mode of this scheduler. The mode dictates the opeation of this scheduler when 
	 * {@link #run()} is called.
	 * @return the mode
	 */
	public byte getMode(){
		return mode;
	}
	
	/**
	 * Sets whether or not the scheduler is disabled. If the scheduler is disabled, it cannot be run.
	 * @param disable true to disable, false otherwise
	 */
	public void setDisabled(boolean disable){
		if(disable)
			mode = MODE_DISABLED;
		else
			mode = MODE_FULL;
	}
	/**
	 * Gets whether or not the scheduler is disabled
	 * @return true if the scheduler is disabled, false otherwise
	 */
	public boolean isDisabled(){
		return mode == MODE_DISABLED;
	}
	
	/**
	 * Gets the instance of the {@link Scheduler} class. If the instance was not
	 * create yet, it is created on the spot.
	 * 
	 * @return the instance of the scheduler
	 */
	public static Scheduler getInstance(){
		if(instance == null)
			instance = new Scheduler();
		return instance;
	}
}