package edu.flash3388.flashlib.robot;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * An Action is something that can be executed by a System on the robot. When started, the action
 * is added to the Scheduler which is responsible for running it. The Action is ran until a condition is met
 * or an action using the same system is used.
 * 
 * <p>
 * An action has few running phases:
 * <ul>
 * 	<li> {@link #initialize()} called once at the start </li>
 * 	<li> {@link #execute()}: called repeatedly until {@link #isFinished()} returns true</li>
 * 	<li> {@link #end()}: called when the action is done ({@link #isFinished()} returns true) or</li>
 * 	<li> {@link #interrupted()}: called when another action starts running on the same system, or the action is
 * manual canceled <li>
 * </ul>
 * 
 * <p>
 * There are many different types of wrapper for action. Those wrapper allows for the modification of an action
 * for externally without changing the defined parameters of it:
 * <ul>
 * 	<li> {@link TimedAction}: wraps an action and sets a timeout for it</li>
 *  <li> {@link SystemAction}: wraps an action and sets a system requirement for it</li>
 *  <li> {@link ActionGroup}: a series of actions to be executed at a specified order</li>
 *  <li> {@link ConditionalAction}: selects which action to run according to a condition</li>
 *  <li> {@link InstantAction}: wraps an action so that {@link #execute()} runs once</li>
 *  <li> {@link RunnableAction}: executes a {@link Runnable} object during {@link #execute()} </li>
 *  <li> {@link InstantRunnableAction}: executes a {@link Runnable} object during {@link #execute()}. Execute runs once</li>
 *  <li> {@link CombinedAction}: allows to combine several actions to run together on a single system </li>
 *  <li> {@link SourceAction}: used by combined actions. Provide data from DoubleDataSource after running </li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see Scheduler
 * @see Subsystem
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

	private Vector<Subsystem> requirements = new Vector<Subsystem>(2);
	private boolean initialized = false;
	private boolean canceled = false;
	private boolean running = false;
	private long timeout = -1;
	private long start_time = -1;
	private String name;
	
	/**
	 * Creates a new action with a given name.
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
		if(!running && RobotFactory.getImplementation().getScheduler().add(this)){
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
		start_time = -1;
	}
	boolean run(){
		if(isTimedOut())
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
	public Enumeration<Subsystem> getRequirements(){
		return requirements.elements();
	}
	/**
	 * Gets the running timeout of the action in milliseconds. 
	 * @return the timeout in milliseconds, or a negative value if there is no timeout.
	 */
	public long getTimeOut(){
		return timeout;
	}
	
	/**
	 * Adds a System that is used by this action.
	 * @param subsystem a system used by this action
	 */
	protected void requires(Subsystem subsystem){
		requirements.addElement(subsystem);
	}
	/**
	 * Adds Systems that are used by this action.
	 * @param subsystems an array of systems used by this action
	 */
	protected void requires(Subsystem... subsystems){
		for(Subsystem s : subsystems)
			requirements.add(s);
	}
	/**
	 * Copies the requirements used by another action to this one.
	 * @param action action to copy requirements from
	 */
	protected void copyRequirements(Action action){
		for (Enumeration<Subsystem> sys = action.getRequirements(); sys.hasMoreElements(); )
			requires(sys.nextElement());
	}
	/**
	 * Resets the requirements of this action
	 */
	protected void resetRequirements(){
		requirements.clear();
	}
	/**
	 * Sets the running timeout for this action in milliseconds. When started, if the timeout is not 0 or negative
	 * time is counted. If the timeout is reached, the action is canceled.
	 * @param milliseconds timeout in milliseconds
	 */
	protected void setTimeOut(long milliseconds){
		timeout = milliseconds;
	}
	/**
	 * Gets whether or not the Scheduler should remove this action when the robot is disabled. Should be true.
	 * @return true if this action is to be removed
	 */
	protected boolean removeOnDisabled(){
		return true;
	}
	/**
	 * Gets whether or not the action has timed out. Time out is defined when the robot started running and timeout
	 * is defined. 
	 * @return true if the action timeout, false otherwise
	 */
	protected boolean isTimedOut(){
		return start_time != -1 && timeout != -1 && (FlashUtil.millis() - start_time) 
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
	 * Creates a canceling action for an action.
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
