package edu.flash3388.flashlib.robot;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * An Action is something that can be executed on the robot. This can include any operation on the robot,
 * depending on what users want. When started, the action is added to the {@link Scheduler} which is responsible
 * for executing the action. To start an action, call {@link #start()}. This class is abstract.
 * <p>
 * An action has few running phases:
 * <ul>
 * 	<li> {@link #initialize()} called once at the start </li>
 * 	<li> {@link #execute()}: called repeatedly until {@link #isFinished()} returns true</li>
 * 	<li> {@link #end()}: called when the action is done ({@link #isFinished()} returns true) or</li>
 * 	<li> {@link #interrupted()}: called when this action stops running and {@link #isFinished()} did not
 * return true. <li>
 * </ul>
 * To indicate whether an action should stop running, users can override the {@link #isFinished()} method and
 * return true to stop the action, or false to keep running. To manually stop an action, {@link #cancel()} can
 * be called.
 * <p>
 * An action can have a timeout. If the time since the action started running has passed a given timeout, the
 * action is canceled, invoking a call to {@link #interrupted()}. Set the timeout by calling {@link #setTimeout(int)}.
 * <p>
 * It is possible to define dependencies for actions. Basically, if an action is using a {@link Subsystem} object
 * of our robot, it is necessary to insure that no other action will use the same object, so that it won't confuse
 * that system. To do that, users must explicitly call {@link #requires(Subsystem)} and pass a system which is
 * used (multiple systems can be required). By doing so, the {@link Scheduler} now knows to stop any other action
 * with at least one similar system requirement. If an action is running and another starts with a similar system
 * requirement, the previous action is canceled, invoking a call to {@link #interrupted()}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Action{
	
	/**
	 * An action which does nothing
	 */
	public static final Action EMPTY = new Action(){
		@Override
		protected void execute() {}
		@Override
		protected void end() {}
	};

	private Set<Subsystem> requirements = new HashSet<Subsystem>(2);
	private boolean initialized = false;
	private boolean canceled = false;
	private boolean running = false;
	private int timeout = -1;
	private int startTime = -1;
	private String name;
	
	/**
	 * Creates a new action with a given name.
	 * 
	 * @param name name of the action
	 */
	public Action(String name){
		this.name = name;
	}
	/**
	 * Creates a new action with an empty name.
	 */
	public Action(){
		this("");
	}
	
	/**
	 * Starts the action. If the Scheduler was initialized, than the action is added to the
	 * Scheduler for running. If the action is running than it is not added.
	 */
	public void start(){
		if(!running && Scheduler.getInstance().add(this)){
			initialized = false;
			canceled = false;
			running = true;
		}
	}
	/**
	 * Cancels the operation of the action if it is running.
	 */
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
		startTime = -1;
	}
	boolean run(){
		if(isTimedout())
			cancel();
		if(canceled)
			return false;
		if(!initialized){
			initialized = true;
			startTime = FlashUtil.millisInt();
			initialize();
		}
		execute();
		return !isFinished();
	}
	
	/**
	 * Gets the name of the action.
	 * @return the name of the action
	 */
	public String getName(){
		return name;
	}
	/**
	 * Sets the name of the action.
	 * @param name name of the action
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Gets whether or not an action has been canceled. Meaning it did not reach {@link #end()}.
	 * @return true if the action was canceled, false otherwise
	 */
	public boolean isCanceled(){
		return canceled;
	}
	/**
	 * Gets whether or not an action is running.
	 * @return true if the action is running, false otherwise
	 */
	public boolean isRunning(){
		return running;
	}
	/**
	 * Gets the running timeout of the action in milliseconds. 
	 * @return the timeout in milliseconds, or a negative value if there is no timeout.
	 */
	public int getTimeout(){
		return timeout;
	}
	/**
	 * Sets the running timeout for this action in milliseconds. When started, if the timeout is not 0 or negative
	 * time is counted. If the timeout is reached, the action is canceled.
	 * @param timeout timeout in milliseconds
	 */
	public void setTimeout(int timeout){
		this.timeout = timeout;
	}
	/**
	 * Sets the running timeout for this action in seconds. When started, if the timeout is not 0 or negative
	 * time is counted. If the timeout is reached, the action is canceled.
	 * @param timeout timeout in seconds
	 */
	public void setTimeout(double timeout){
		setTimeout((int)(timeout * 0.001));
	}
	/**
	 * Cancels the timeout set for this action. Done by setting the timeout to a negative value.
	 */
	public void cancelTimeout(){
		setTimeout(-1);
	}
	/**
	 * Adds a System that is used by this action.
	 * @param subsystem a system used by this action
	 */
	public void requires(Subsystem subsystem){
		requirements.add(subsystem);
	}
	/**
	 * Adds Systems that are used by this action.
	 * @param subsystems an array of systems used by this action
	 */
	public void requires(Subsystem... subsystems){
		for(Subsystem s : subsystems)
			requires(s);
	}
	/**
	 * Resets the requirements of this action
	 */
	public void resetRequirements(){
		requirements.clear();
	}	
	/**
	 * Gets the {@link Subsystem}s which are used by this action.
	 * @return {@link Iterator} of the used {@link Subsystem}s.
	 */
	public Iterator<Subsystem> getRequirements(){
		return requirements.iterator();
	}
	
	
	/**
	 * Copies the requirements used by another action to this one.
	 * @param action action to copy requirements from
	 */
	protected void copyRequirements(Action action){
		for (Iterator<Subsystem> sys = action.getRequirements(); sys.hasNext(); )
			requires(sys.next());
	}
	/**
	 * Gets whether or not the action has timed out. Time out is defined when the robot started running and timeout
	 * is defined. 
	 * @return true if the action timeout, false otherwise
	 */
	protected boolean isTimedout(){
		return startTime > 0 && timeout > 0 && (FlashUtil.millisInt() - startTime) 
				>= timeout;
	}
	
	/**
	 * Called once when the action is started.
	 */
	protected void initialize(){ }
	/**
	 * Returns true when the action should end.
	 * @return true when the action should end, false otherwise.
	 */
	protected boolean isFinished(){ return false;}
	/**
	 * Called when the action was before {@link #isFinished()} returns true.
	 * <p>
	 * Calls {@link #end()} now. Override to execute something else.
	 * </p>
	 */
	protected void interrupted(){ end();}
	
	/**
	 * Called repeatedly during the execution of the action.
	 */
	protected abstract void execute();
	/**
	 * Called when {@link #isFinished()} returns true.
	 */
	protected abstract void end();
	
	/**
	 * Creates a canceling action for an action. This is an {@link InstantAction} which calls {@link #cancel()}
	 * for a given action when started.
	 * 
	 * @param action action to cancel
	 * @return canceling action
	 */
	public static Action stopAction(Action action){
		return new InstantAction(){
			@Override
			public void execute() {
				if(action.isRunning())
					action.cancel();
			}
		};
	}
}
