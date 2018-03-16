package edu.flash3388.flashlib.robot;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import edu.flash3388.flashlib.util.beans.BooleanSource;

/**
 * Scheduler is responsible for executing tasks for robots. Users can add {@link Action} and
 * {@link Runnable} objects to the scheduler which will then be executed by when the {@link Scheduler} 
 * runs. This allows for easy management of robot operations.
 * <p>
 * The scheduler can work with simple {@link Runnable} objects, or tasks, which can be added to run once,
 * or run continuously.
 * <p>
 * For more complex operations, the scheduler can use {@link Action} objects. Those objects can be added
 * to the scheduler and then executed as well. Unlike simple tasks, actions might depend on {@link Subsystem}
 * objects for operations. The scheduler tracks the required systems of each action making sure that only one 
 * {@link Action} object runs on a {@link Subsystem} at any given time. 
 * <p>
 * In addition, the scheduler can allow {@link Subsystem} to hold default {@link Action} objects, which
 * run only if no {@link Action} is using the {@link Subsystem} at the moment. When the scheduler runs,
 * it checks all registered {@link Subsystem} objects to see if one does not have an action at the moment.
 * If it doesn't and a default action is defined, the default action is started. Systems are registered
 * by calling {@link #registerSystem(Subsystem)}, but this occurs in the {@link Subsystem} constructor.
 * <p>
 * The scheduler has 4 run modes: 
 * <ul>
 * 	<li> {@link #MODE_DISABLED}: do nothing when running </li>
 * 	<li> {@link #MODE_TASKS}: run only tasks ( {@link Runnable} object) </li>
 * 	<li> {@link #MODE_ACTIONS}: run only {@link Action} objects an update {@link Subsystem} objects </li>
 * 	<li> {@link #MODE_FULL}: run both tasks and actions. A combination of {@link #MODE_TASKS} and {@link #MODE_ACTIONS} </li>
 * </ul>
 * The mode can be set by calling {@link #setMode(byte)}.
 * <p>
 * To run the scheduler, it is necessary to manually call {@link #run()} periodically.
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
	
	/**
	 * An event trigger for the scheduler.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.2.1
	 */
	public static interface Trigger{
		boolean checkTrigger();
		void fire();
	}
	public static abstract class ActionTrigger implements Trigger{
		protected final BooleanSource condition;
		protected final Action action;
		
		public ActionTrigger(BooleanSource condition, Action action) {
			this.condition = condition;
			this.action = action;
		}
		
		public final Action getAction(){
			return action;
		}
		public final BooleanSource getConditionSource(){
			return condition;
		}
	}
	public static class StartActionTrigger extends ActionTrigger{

		public StartActionTrigger(BooleanSource condition, Action action) {
			super(condition, action);
		}

		@Override
		public boolean checkTrigger() {
			return !action.isRunning() && condition.get();
		}
		@Override
		public void fire() {
			action.start();
		}
	}
	public static class CancelActionTrigger extends ActionTrigger{

		public CancelActionTrigger(BooleanSource condition, Action action) {
			super(condition, action);
		}

		@Override
		public boolean checkTrigger() {
			return action.isRunning() && condition.get();
		}
		@Override
		public void fire() {
			action.cancel();
		}
	}
	public static class ToggleActionTrigger extends ActionTrigger{

		public ToggleActionTrigger(BooleanSource condition, Action action) {
			super(condition, action);
		}

		@Override
		public boolean checkTrigger() {
			return condition.get();
		}
		@Override
		public void fire() {
			if(!action.isRunning())
				action.start();
			else
				action.cancel();
		}
	}
	
	public static final byte MODE_DISABLED = 0x0;
	public static final byte MODE_TASKS = 0x1;
	public static final byte MODE_ACTIONS = 0x2;
	public static final byte MODE_FULL = MODE_ACTIONS | MODE_TASKS;
	
	private static Scheduler instance;
	
	private byte mode = MODE_FULL;
	
	private Vector<Action> actions = new Vector<Action>();
	private Vector<Trigger> triggers = new Vector<Trigger>();
	private Set<Subsystem> systems = new HashSet<Subsystem>();
	private Vector<TaskWrapper> tasks = new Vector<TaskWrapper>();
	
	private Scheduler(){}
	
	/**
	 * Runs the scheduler. 
	 */
	public void run(){
		if(isDisabled()) return;
		
		if(!triggers.isEmpty()){
			Trigger trigger = null;
			for(Enumeration<Trigger> triggerEnum = triggers.elements(); triggerEnum.hasMoreElements();){
				trigger = triggerEnum.nextElement();
				if(trigger.checkTrigger())
					trigger.fire();
			}
		}
		
		if(isMode(MODE_TASKS) && !tasks.isEmpty()){
			TaskWrapper taskWrapper = null;
			for(Enumeration<TaskWrapper> taskEnum = tasks.elements(); taskEnum.hasMoreElements();){
				taskWrapper = taskEnum.nextElement();
				if(!taskWrapper.run() || taskWrapper.removeOnFinish)
					tasks.remove(taskWrapper);
			}
		}
		
		if(isMode(MODE_ACTIONS) && !actions.isEmpty()){			
			Action action = null;
			for(Enumeration<Action> actionEnum = actions.elements(); actionEnum.hasMoreElements();){
				action = actionEnum.nextElement();
				if(!action.run())
					remove(action);
			}
		}
		
		if(isMode(MODE_ACTIONS) && !systems.isEmpty()){
			Subsystem system = null;
			for(Iterator<Subsystem> systemEnum = systems.iterator(); systemEnum.hasNext();){
				system = systemEnum.next();
				if(!system.hasCurrentAction())
					system.startDefaultAction();
			}
		}
	}
	
	/**
	 * Adds a new {@link Trigger} object to the {@link Scheduler}.
	 * When running, the scheduler will check the trigger's condition. If
	 * it is true, the trigger will will be fired.
	 * 
	 * The {@link Scheduler} must not be disabled in order
	 * for triggers to be checked.
	 * 
	 * @param trigger a new trigger.
	 */
	public void addTrigger(Trigger trigger){
		triggers.add(trigger);
	}
	
	/**
	 * Adds a new {@link Runnable} to be executed continuously until manually removed.
	 * 
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
	 * 
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
	 * 
	 * @param runnable task to remove
	 * @return true if the task was in execution, false otherwise
	 */
	public boolean remove(Runnable runnable){
		return tasks.remove(runnable);
	}
	
	/**
	 * Registers a {@link Subsystem} to this {@link Scheduler}. Allows for activation of
	 * default {@link Action} for this system.
	 * 
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
	 * Adds a new trigger to the scheduler which starts an action
	 * on a given condition.
	 * 
	 * @param condition trigger condition.
	 * @param action action to start.
	 */
	public void startOnTrigger(BooleanSource condition, Action action){
		addTrigger(new StartActionTrigger(condition, action));
	}
	/**
	 * Adds a new trigger to the scheduler which cancels an action
	 * on a given condition.
	 * 
	 * @param condition trigger condition.
	 * @param action action to cancel.
	 */
	public void cancelOnTrigger(BooleanSource condition, Action action){
		addTrigger(new CancelActionTrigger(condition, action));
	}
	/**
	 * Adds a new trigger to the scheduler which toggles an action
	 * on a given condition. If the action is running, it is canceled.
	 * If the action is not running, it is started.
	 * 
	 * @param condition trigger condition.
	 * @param action action to toggle.
	 */
	public void toggleOnTrigger(BooleanSource condition, Action action){
		addTrigger(new ToggleActionTrigger(condition, action));
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
	 * 
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