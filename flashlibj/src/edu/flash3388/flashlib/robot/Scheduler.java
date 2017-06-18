package edu.flash3388.flashlib.robot;

import java.util.Enumeration;
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
 * To initialize the scheduler, call {@link #init()}.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Scheduler {
	
	private static Scheduler instance;
	
	private boolean disabled = false;
	
	private Vector<Action> actions = new Vector<Action>();
	private Vector<System> systems = new Vector<System>();
	private Vector<ScheduledTask> tasks = new Vector<ScheduledTask>();
	
	private Scheduler(){}
	
	/**
	 * Runs the scheduler. 
	 */
	public void run(){
		if(disabled) return;
		
		ScheduledTask task = null;
		for(Enumeration<ScheduledTask> taskEnum = tasks.elements(); taskEnum.hasMoreElements();){
			task = taskEnum.nextElement();
			if(!task.run())
				tasks.remove(task);
		}
		
		Action action = null;
		for(Enumeration<Action> actionEnum = actions.elements(); actionEnum.hasMoreElements();){
			action = actionEnum.nextElement();
			if(!action.run())
				remove(action);
		}
		
		System system = null;
		for(Enumeration<System> systemEnum = systems.elements(); systemEnum.hasMoreElements();){
			system = systemEnum.nextElement();
			if(!system.hasCurrentAction() && !RobotState.isRobotDisabled())
				system.startDefaultAction();
		}
	}
	
	/**
	 * Adds a new ScheduledTask to be executed.
	 * @param task task to execute
	 */
	public void add(ScheduledTask task){
		tasks.addElement(task);
	}
	/**
	 * Removes a ScheduledTask from execution.
	 * @param task task to remove
	 */
	public void remove(ScheduledTask task){
		tasks.remove(task);
	}
	
	void registerSystem(System system){
		systems.add(system);
	}
	
	/**
	 * Adds a new Action to be executed by the scheduler. The action's system requirements are checked. If
	 * other actions use those same systems, those actions are canceled. If the scheduler is disabled, the action
	 * cannot be added.
	 * 
	 * @param action action to be added
	 * @return true if the action was successfully added
	 */
	public boolean add(Action action){
		if(disabled) return false;
		
		Enumeration<System> requirements = action.getRequirements();
		while(requirements.hasMoreElements()){
			System system = requirements.nextElement();
			if(system.hasCurrentAction())
				remove(system.getCurrentAction());
			system.setCurrentAction(action);
		}
		actions.addElement(action);
		
		return true;
	}
	/**
	 * Removes an action from the scheduler. If the action is in the scheduler, it is removed and its required systems
	 * are updated as lacking a current action.
	 * @param action action to remove.
	 */
	public void remove(Action action){
		if(actions.removeElement(action)){
			Enumeration<System> requirements = action.getRequirements();
			while(requirements.hasMoreElements())
				requirements.nextElement().setCurrentAction(null);
			
			action.removed();
		}
	}
	/**
	 * Removes all the actions from the scheduler.
	 */
	public void removeAllActions(){
		Enumeration<Action> actionEnum = actions.elements();
		for (;actionEnum.hasMoreElements();)
			remove(actionEnum.nextElement());
	}
	
	/**
	 * Sets whether or not the scheduler is disabled. If the scheduler is disabled, it cannot be run.
	 * @param disable true to disable, false otherwise
	 */
	public void setDisabled(boolean disable){
		this.disabled = disable;
	}
	/**
	 * Gets whether or not the scheduler is disabled
	 * @return true if the scheduler is disabled, false otherwise
	 */
	public boolean isDisabled(){
		return disabled;
	}
	
	/**
	 * If the scheduler has an instance, the given value is passed to {@link #setDisabled(boolean)}.
	 * 
	 * @param disable true to disable, false to enable
	 */
	public static void disableScheduler(boolean disable){
		if(!schedulerHasInstance()) return;
		getInstance().setDisabled(disable);
	}
	/**
	 * If the scheduler has an instance, the {@link #run()} method is called.
	 */
	public static void runScheduler(){
		if(!schedulerHasInstance()) return;
		getInstance().run();
	}
	/**
	 * Gets whether or not the scheduler has an instance.
	 * @return true if the scheduler has an instance, false otherwise
	 */
	public static boolean schedulerHasInstance(){
		return instance != null;
	}
	/**
	 * Gets the instance of the scheduler. 
	 * @return the instance of the scheduler, null if the scheduler was not initialized
	 */
	public static Scheduler getInstance(){
		return instance;
	}
	/**
	 * Initializes the scheduler, if it was not initialized.
	 */
	public static void init(){
		if(instance == null)
			instance = new Scheduler();
	}
}