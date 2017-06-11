package edu.flash3388.flashlib.robot;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.util.FlashUtil;

public class Scheduler {
	
	private static Scheduler instance;
	
	private boolean disabled = false;
	
	private Vector<Action> actions = new Vector<Action>();
	private Vector<System> systems = new Vector<System>();
	private Vector<ScheduledTask> tasks = new Vector<ScheduledTask>();
	
	private Scheduler(){}
	
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
	
	public void add(ScheduledTask task){
		tasks.addElement(task);
	}
	public void remove(ScheduledTask task){
		tasks.remove(task);
	}
	public void registerSystem(System system){
		systems.add(system);
	}
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
	public void remove(Action action){
		actions.removeElement(action);
		
		Enumeration<System> requirements = action.getRequirements();
		while(requirements.hasMoreElements())
			requirements.nextElement().setCurrentAction(null);
		
		action.removed();
	}
	public void removeAllActions(){
		Enumeration<Action> actionEnum = actions.elements();
		for (;actionEnum.hasMoreElements();)
			remove(actionEnum.nextElement());
	}
	
	public void disable(boolean disable){
		this.disabled = disable;
	}
	public boolean isDisabled(){
		return disabled;
	}
	
	public static void disableScheduler(boolean disable){
		if(!schedulerHasInstance()) return;
		getInstance().disable(disable);
	}
	public static void runScheduler(){
		if(!schedulerHasInstance()) return;
		getInstance().run();
	}
	public static boolean schedulerHasInstance(){
		return instance != null;
	}
	public static Scheduler getInstance(){
		return instance;
	}
	public static void init(){
		if(instance == null)
			instance = new Scheduler();
	}
}