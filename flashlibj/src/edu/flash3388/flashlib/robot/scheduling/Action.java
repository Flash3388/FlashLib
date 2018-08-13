package edu.flash3388.flashlib.robot.scheduling;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
 * It is possible to define dependencies for scheduling. Basically, if an action is using a {@link Subsystem} object
 * of our robot, it is necessary to insure that no other action will use the same object, so that it won't confuse
 * that system. To do that, users must explicitly call {@link #requires(Subsystem)} and pass a system which is
 * used (multiple systems can be required). By doing so, the {@link Scheduler} now knows to stop any other action
 * with at least one similar system requirement. If an action is running and another starts with a similar system
 * requirement, the previous action is canceled, invoking a call to {@link #interrupted()}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Action {
	
	/**
	 * An action which does nothing
	 */
	public static final Action EMPTY = new Action(){
		@Override
		protected void execute() {}
		@Override
		protected void end() {}
	};

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

	private final Set<Subsystem> mRequirements;

	private boolean mIsInitialized;
	private boolean mIsCanceled;
	private boolean mIsRunning;

	private int mTimeoutMs;
	private int mStartTime;

	/**
	 * Creates a new action.
	 */
	public Action(int timeoutMs){
		this();
		setTimeoutMs(timeoutMs);
	}

	public Action(double timeoutSeconds) {
		this();
		setTimeoutSeconds(timeoutSeconds);
	}

	public Action() {
		mRequirements = new HashSet<Subsystem>(2);

		mIsRunning = false;
		mIsCanceled = false;
		mIsInitialized = false;

		mTimeoutMs = -1;
		mStartTime = -1;
	}
	
	/**
	 * Starts the action. If the Scheduler was initialized, than the action is added to the
	 * Scheduler for running. If the action is running than it is not added.
	 */
	public void start(){
		if(!mIsRunning){
			mIsInitialized = false;
			mIsCanceled = false;
			mIsRunning = true;

			Scheduler.getInstance().add(this);
		}
	}

	/**
	 * Cancels the operation of the action if it is running.
	 */
	public void cancel(){
		if(isRunning()) {
			mIsCanceled = true;
		}
	}

	/**
	 * Gets whether or not an action has been canceled. Meaning it did not reach {@link #end()}.
	 * @return true if the action was canceled, false otherwise
	 */
	public boolean isCanceled(){
		return mIsCanceled;
	}

	/**
	 * Gets whether or not an action is running.
	 * @return true if the action is running, false otherwise
	 */
	public boolean isRunning(){
		return mIsRunning;
	}

	/**
	 * Gets the running timeout of the action in milliseconds.
	 * @return the timeout in milliseconds, or a negative value if there is no timeout.
	 */
	public int getTimeoutMs(){
		return mTimeoutMs;
	}

	/**
	 * Sets the running timeout for this action in milliseconds. When started, if the timeout is not 0 or negative
	 * time is counted. If the timeout is reached, the action is canceled.
	 * @param timeoutMs timeout in milliseconds
	 */
	public void setTimeoutMs(int timeoutMs){
		mTimeoutMs = timeoutMs;
	}

	/**
	 * Sets the running timeout for this action in seconds. When started, if the timeout is not 0 or negative
	 * time is counted. If the timeout is reached, the action is canceled.
	 * @param timeoutSeconds timeout in seconds
	 */
	public void setTimeoutSeconds(double timeoutSeconds){
		setTimeoutMs((int)(timeoutSeconds * 0.001));
	}

	/**
	 * Cancels the timeout set for this action. Done by setting the timeout to a negative value.
	 */
	public void cancelTimeout(){
		mTimeoutMs = -1;
	}

	/**
	 * Gets whether or not the action has timed out. Time out is defined when the robot started running and timeout
	 * is defined.
	 * @return true if the action timeout, false otherwise
	 */
	public boolean hasTimeoutReached(){
		return mStartTime > 0 && mTimeoutMs > 0 && (FlashUtil.millisInt() - mStartTime)
				>= mTimeoutMs;
	}

	/**
	 * Adds a System that is used by this action.
	 * @param subsystem a system used by this action
	 */
	public void requires(Subsystem subsystem){
		mRequirements.add(subsystem);
	}

	/**
	 * Adds Systems that are used by this action.
	 * @param subsystems an array of systems used by this action
	 */
	public void requires(Subsystem... subsystems){
		mRequirements.addAll(Arrays.asList(subsystems));
	}

	/**
	 * Resets the requirements of this action
	 */
	public void resetRequirements(){
		mRequirements.clear();
	}

	/**
	 * Gets the {@link Subsystem}s which are used by this action.
	 * @return {@link Set} of the used {@link Subsystem}s.
	 */
	public Set<Subsystem> getRequirements(){
		return Collections.unmodifiableSet(mRequirements);
	}


	/**
	 * Copies the requirements used by another action to this one.
	 * @param action action to copy requirements from
	 */
	public void copyRequirements(Action action){
		for (Subsystem subsystem : action.getRequirements()) {
			requires(subsystem);
		}
	}

	public boolean doesRequire(Subsystem subsystem) {
		return mRequirements.contains(subsystem);
	}

	void copyActionProperties(Action action) {
		copyRequirements(action);
		mTimeoutMs = action.mTimeoutMs;
	}

	void removed(){
		if(mIsInitialized){
			if(isCanceled()) {
				interrupted();
			}
			else {
				end();
			}
		}

		mIsInitialized = false;
		mIsCanceled = false;
		mIsRunning = false;
		mStartTime = -1;
	}

	boolean run(){
		if(hasTimeoutReached()) {
			cancel();
		}

		if(isCanceled()) {
			return false;
		}

		if(!mIsInitialized) {
			mIsInitialized = true;
			mStartTime = FlashUtil.millisInt();
			initialize();
		}

		execute();

		return !isFinished();
	}
	
	/**
	 * Called once when the action is started.
	 */
	protected void initialize() {}

	/**
	 * Returns true when the action should end.
	 * @return true when the action should end, false otherwise.
	 */
	protected boolean isFinished() {
		return false;
	}

	/**
	 * Called when the action was before {@link #isFinished()} returns true.
	 * <p>
	 * Calls {@link #end()} now. Override to execute something else.
	 * </p>
	 */
	protected void interrupted() {
		end();
	}
	
	/**
	 * Called repeatedly during the execution of the action.
	 */
	protected abstract void execute();

	/**
	 * Called when {@link #isFinished()} returns true.
	 */
	protected abstract void end();
}
