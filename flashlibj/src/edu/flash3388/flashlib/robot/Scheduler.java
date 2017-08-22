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
	}
	
	private boolean disabled = false;
	
	private Vector<Action> actions = new Vector<Action>();
	private Vector<SubSystem> systems = new Vector<SubSystem>();
	private Vector<TaskWrapper> tasks = new Vector<TaskWrapper>();
	
	/**
	 * Runs the scheduler. 
	 */
	public void run(){
		if(disabled) return;
		
		if(tasks.size() > 0){
			TaskWrapper taskWrapper = null;
			for(Enumeration<TaskWrapper> taskEnum = tasks.elements(); taskEnum.hasMoreElements();){
				taskWrapper = taskEnum.nextElement();
				if(!taskWrapper.run() || taskWrapper.removeOnFinish)
					tasks.remove(taskWrapper);
			}
		}
		
		if(actions.size() > 0){
			Action action = null;
			for(Enumeration<Action> actionEnum = actions.elements(); actionEnum.hasMoreElements();){
				action = actionEnum.nextElement();
				if(!action.run())
					remove(action);
			}
		}
		
		if(systems.size() > 0){
			SubSystem system = null;
			for(Enumeration<SubSystem> systemEnum = systems.elements(); systemEnum.hasMoreElements();){
				system = systemEnum.nextElement();
				if(!system.hasCurrentAction() && !RobotState.isRobotDisabled())
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
	
	void registerSystem(SubSystem system){
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
		if(disabled) return false;
		
		Enumeration<SubSystem> requirements = action.getRequirements();
		while(requirements.hasMoreElements()){
			SubSystem system = requirements.nextElement();
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
			Enumeration<SubSystem> requirements = action.getRequirements();
			while(requirements.hasMoreElements())
				requirements.nextElement().setCurrentAction(null);
			
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
}