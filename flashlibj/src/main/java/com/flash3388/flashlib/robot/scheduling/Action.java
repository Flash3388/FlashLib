package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.CompareResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
 * action is canceled, invoking a call to {@link #interrupted()}. Set the timeout by calling {@link #setTimeout(Time)}.
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

    private final Scheduler mScheduler;
	private final Clock mClock;
    private final Set<Subsystem> mRequirements;

	private boolean mIsInitialized;
	private boolean mIsCanceled;
	private boolean mIsRunning;

	private Time mTimeout;
	private Time mStartTime;

	private Action mParent;

	public Action(Scheduler scheduler, Clock clock, Time timeout) {
	    mScheduler = Objects.requireNonNull(scheduler, "scheduler is null");
        mClock = Objects.requireNonNull(clock, "clock is null");
		mRequirements = new HashSet<>(2);

		mIsRunning = false;
		mIsCanceled = false;
		mIsInitialized = false;

		mTimeout = Objects.requireNonNull(timeout, "timeout is null");
        mStartTime = Time.INVALID;

        mParent = null;
	}

    public Action(Clock clock, Time timeout) {
	    this(RunningRobot.INSTANCE.get().getScheduler(), clock, timeout);
    }

	public Action(Clock clock) {
	    this(clock, Time.INVALID);
    }

    public Action() {
	    this(RunningRobot.INSTANCE.get().getClock());
    }
	
	/**
	 * Starts the action. If the Scheduler was initialized, than the action is added to the
	 * Scheduler for running. If the action is running than it is not added.
	 */
	public void start(){
	    validateNoParent();

		if(!mIsRunning){
			startAction();
			mScheduler.add(this);
		}
	}

	/**
	 * Cancels the operation of the action if it is running.
	 */
	public void cancel(){
	    validateNoParent();
		cancelAction();
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
	 * @return the timeout.
	 */
	public Time getTimeout(){
		return mTimeout;
	}

	/**
	 * Sets the running timeout for this action. When started, if the timeout is not 0 or negative
	 * time is counted. If the timeout is reached, the action is canceled.
	 * @param timeout timeout
     * @return this instance
	 */
	public Action setTimeout(Time timeout){
        Objects.requireNonNull(timeout, "timeout is null");

	    validateNotRunning();
        mTimeout = timeout;

        return this;
	}

	/**
	 * Cancels the timeout set for this action. Done by setting the timeout to an invalid value.
     * @return this instance
	 */
	public Action cancelTimeout(){
		return setTimeout(Time.INVALID);
	}

	/**
	 * Gets whether or not the action has timed out. Time out is defined when the robot started running and timeout
	 * is defined.
	 * @return true if the action timeout, false otherwise
	 */
	public boolean wasTimeoutReached(){
        validateRunning();
	    if (!mStartTime.isValid() || !mTimeout.isValid()) {
	        return false;
        }

		return (mClock.currentTime().sub(mStartTime)).compareTo(mTimeout) == CompareResult.GREATER_THAN.getValue();
	}

	/**
	 * Adds a System that is used by this action.
	 * @param subsystem a system used by this action
     * @return this instance
	 */
	public Action requires(Subsystem subsystem){
        Objects.requireNonNull(subsystem, "requirement is null");
	    return requires(Collections.singleton(subsystem));
	}

	/**
	 * Adds Systems that are used by this action.
	 * @param subsystems an array of systems used by this action
     * @return this instance
	 */
	public Action requires(Subsystem... subsystems){
        Objects.requireNonNull(subsystems, "requirements is null");
	    return requires(Arrays.asList(subsystems));
	}

    public Action requires(Collection<Subsystem> subsystems) {
        Objects.requireNonNull(subsystems, "requirements is null");

        validateNotRunning();
        mRequirements.addAll(subsystems);

        return this;
    }

	/**
	 * Resets the requirements of this action.
     * @return this instance.
	 */
	public Action resetRequirements(){
	    validateNotRunning();
		mRequirements.clear();

		return this;
	}

	/**
	 * Gets the {@link Subsystem}s which are used by this action.
	 * @return {@link Set} of the used {@link Subsystem}s.
	 */
	public Set<Subsystem> getRequirements(){
		return Collections.unmodifiableSet(mRequirements);
	}

	public boolean doesRequire(Subsystem subsystem) {
		return mRequirements.contains(subsystem);
	}

	void startAction() {
        if(!mIsRunning){
            mIsInitialized = false;
            mIsCanceled = false;
            mIsRunning = true;
        }
    }

    void cancelAction() {
	    if (isRunning()) {
	        mIsCanceled = true;
        }
    }

	void removed(){
		if(mIsInitialized){
			if(isCanceled()) {
				interrupted();
			} else {
				end();
			}
		}

		mIsInitialized = false;
		mIsCanceled = false;
		mIsRunning = false;
		mStartTime = Time.INVALID;
	}

	boolean run(){
		if(wasTimeoutReached()) {
			cancelAction();
		}

		if(isCanceled()) {
			return false;
		}

		if(!mIsInitialized) {
			mIsInitialized = true;
			mStartTime = mClock.currentTime();
			initialize();
		}

		execute();

		return !isFinished();
	}

	void setParent(Action parent) {
	    validateNoParent();
	    validateNotRunning();

	    mParent = Objects.requireNonNull(parent, "parent is null");
        mParent.requires(getRequirements());
    }

    protected final void validateNoParent() {
	    if (mParent != null) {
	        throw new IllegalStateException("Action has a parent");
        }
    }

    protected final void validateRunning() {
        if (!isRunning()) {
            throw new IllegalStateException("action not running");
        }
    }

    protected final void validateNotRunning() {
        if (isRunning()) {
            throw new IllegalStateException("action running");
        }
    }

    //--------------------------------------------------------------------
    //----------------------Implementable---------------------------------
    //--------------------------------------------------------------------

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
