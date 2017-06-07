package edu.flash3388.flashlib.robot;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.util.FlashUtil;

public class Scheduler {
	
	public static class ThreadFollower{
		
		private static final int MAX_ATTEMPTS = 3;
		
		private static long FEED_TIMEOUT = -1;
		private long last_feed = -1;
		private int initialization_attempts = 0;
		
		public void reset(){
			FEED_TIMEOUT = -1;
			last_feed = -1;
			initialization_attempts = 0;
		}
		public void feed(){
			long millis = FlashUtil.millis();
			if(last_feed < 0)
				last_feed = millis;
			else if(initialization_attempts < MAX_ATTEMPTS){
				FEED_TIMEOUT += millis - last_feed;
				initialization_attempts++;
				
				if(initialization_attempts == MAX_ATTEMPTS)
					FEED_TIMEOUT /= MAX_ATTEMPTS;
			}
			
			last_feed = millis;
		}
		public boolean check(){
			if(initialization_attempts < MAX_ATTEMPTS) return true;
			return FlashUtil.millis() - last_feed < FEED_TIMEOUT;
		}
	}
	
	private static class SchedulerTask implements Runnable{
		
		private boolean run = true;
		private Scheduler scheduler;
		
		@Override
		public void run() {
			scheduler = Scheduler.getInstance();
			scheduler.follower.feed();
			while(run){
				scheduler.iterate();
				FlashUtil.delay(DELAY_MILLISECONDS);
				scheduler.follower.feed();
			}
		}
		
		public void stop(){
			run = false;
		}
	}
	
	private static final byte DELAY_MILLISECONDS = 25;
	
	private static Scheduler instance;
	
	private ThreadFollower follower;
	private SchedulerTask task;
	private Thread run_thread;
	private boolean disabled = false, threaded = false;
	
	private Vector<Action> actions = new Vector<Action>();
	private Vector<System> systems = new Vector<System>();
	private Vector<ScheduledTask> tasks = new Vector<ScheduledTask>();
	
	private Scheduler(boolean threaded){
		if(threaded)
			startThread();
	}
	
	private void iterate(){
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
	
	public void run(){
		if(!threaded)
			iterate();
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
	
	public void stopThread(){
		task.stop();
		threaded = false;
	}
	public void startThread(){
		if(threaded) return;
		
		follower = new ThreadFollower();
		task = new SchedulerTask();
		run_thread = new Thread(task, "FLASHLib-Scheduler");
		run_thread.setPriority(Thread.MAX_PRIORITY / 2);
		run_thread.start();	
		
		threaded = true;
	}
	public ThreadFollower getFollower(){
		return follower;
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
		init(true);
	}
	protected static void init(boolean threaded){
		if(instance == null)
			instance = new Scheduler(threaded);
	}
}