package edu.flash3388.flashlib.robot;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Represents an action to be executed by the robot. This class cannot be instantiated.
 * 
 * @author Tom Tzook
 */
public abstract class Action {
	
	public static final Action EMPTY = new Action(){
		@Override
		protected void execute() {}

		@Override
		protected void end() {
		}
	};

	private Vector<System> requirements = new Vector<System>(2);
	private boolean initialized = false;
	private boolean canceled = false;
	private boolean running = false;
	private long timeout = -1;
	private long start_time = -1;
	private String name;
	
	public Action(String name){
		this.name = name;
	}
	public Action(){
		this("");
	}
	
	public Action addRequirement(System sys){
		requires(sys);
		return this;
	}
	public Action setTimeout(double sec){
		return setTimeout((long)(sec * 1000));
	}
	public Action setTimeout(long millis){
		setTimeOut(millis);
		return this;
	}
	
	public void start(){
		initialized = false;
		canceled = false;
		running = true;
		Scheduler.getInstance().add(this);
	}
	public void cancel(){
		if(running)
			canceled = true;
	}
	void removed(){
		if(initialized){
			if(canceled)
				interrupted();
			else end();
		}
		initialized = false;
		canceled = false;
		running = false;
		start_time = -1;
	}
	boolean run(){
		if((RobotState.isRobotDisabled() && removeOnDisabled()) || isTimedOut())
			cancel();
		if(canceled)
			return false;
		if(!initialized){
			initialized = true;
			start_time = FlashUtil.millis();
			initialize();
		}
		execute();
		return !isFinished();
	}
	
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public boolean isCanceled(){
		return canceled;
	}
	public boolean isRunning(){
		return running;
	}
	public Enumeration<System> getRequirements(){
		return requirements.elements();
	}
	
	protected void requires(System... subsystems){
		for(System s : subsystems)
			requirements.add(s);
	}
	protected void resetRequirements(){
		requirements.clear();
	}
	protected void requires(System subsystem){
		requirements.addElement(subsystem);
	}
	protected void setTimeOut(long milliseconds){
		timeout = milliseconds;
	}
	protected long getTimeOut(){
		return timeout;
	}
	protected boolean removeOnDisabled(){
		return true;
	}
	protected boolean isTimedOut(){
		return start_time != -1 && timeout != -1 && (FlashUtil.millis() - start_time) 
				>= timeout;
	}
	
	protected void initialize(){ }
	protected boolean isFinished(){ return false;}
	protected void interrupted(){ end();}
	
	protected abstract void execute();
	protected abstract void end();
	
	public static Action stopAction(Action action){
		return new InstantAction(){
			@Override
			protected void execute() {
				if(action.isRunning())
					action.cancel();
			}
		};
	}
}
