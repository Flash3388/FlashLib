package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.robot.scheduling.Subsystem;
import com.flash3388.flashlib.time.Time;

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
    private final Set<Subsystem> mRequirements;

	private boolean mIsCanceled;
	private boolean mIsRunning;

	private Time mTimeout;

	private Action mParent;

	public Action(Scheduler scheduler, Time timeout) {
	    mScheduler = Objects.requireNonNull(scheduler, "scheduler is null");
		mRequirements = new HashSet<>(1);

		mIsRunning = false;
		mIsCanceled = false;

		mTimeout = Objects.requireNonNull(timeout, "timeout is null");

        mParent = null;
	}

    public Action(Scheduler scheduler) {
	    this(scheduler, Time.INVALID);
    }

    public Action(Time timeout) {
	    this(RunningRobot.getInstance().getScheduler(), timeout);
    }

	public Action() {
	    this(Time.INVALID);
    }
	
	/**
	 * Start the action if it is not running, adding it to the scheduler.
     *
     * @throws IllegalStateException if this action has a parent action.
	 */
	public void start(){
	    validateNoParent();

		if(!mIsRunning){
			markStarted();
			mScheduler.add(this);
		}
	}

	/**
	 * Cancels this action if it is not running.
     *
     * @throws IllegalStateException if this action has a parent action.
	 */
	public void cancel(){
	    validateNoParent();
		markCanceled();
	}

	/**
	 * Gets whether or not an action has been canceled. Meaning it did not reach {@link #end()}.
     * This could occur if one of several things happen:
     * <ul>
     *     <li>A call to {@link #cancel()} was made</li>
     *     <li>A timeout was defined for this action, and that timeout has reached</li>
     *     <li>Another action with conflicting requirements was started on the same {@link Scheduler}</li>
     *     <li>The {@link Scheduler} has being requested to remove the action</li>
     * </ul>
     *
	 * @return <b>true</b> if the action was canceled, <b>false</b> otherwise
	 */
	public boolean isCanceled(){
		return mIsCanceled;
	}

	/**
	 * Gets whether or not the action is running.
     *
	 * @return <b>true</b> if the action is running, <b>false</b> otherwise
	 */
	public boolean isRunning(){
		return mIsRunning;
	}

	/**
	 * Gets the defined timeout for this action. If no timeout was defined,
     * then this will return {@link Time#INVALID}.
     *
	 * @return the timeout or {@link Time#INVALID} if not defined.
	 */
	public Time getTimeout(){
		return mTimeout;
	}

	/**
	 * Sets the run timeout for this action. If the given timeout is valid ({@link Time#isValid()}), then this
     * action will stop running (be canceled) once that timeout was reached.
     *
	 * @param timeout timeout to set, or {@link Time#INVALID} to cancel timeout.
     * @return this instance
     *
     * @throws IllegalStateException if the action is running.
	 */
	public Action setTimeout(Time timeout){
        Objects.requireNonNull(timeout, "timeout is null");

	    validateNotRunning();
        mTimeout = timeout;

        return this;
	}

	/**
	 * Cancels the timeout set for this action. Calls {@link #setTimeout(Time)} with {@link Time#INVALID}
     *
     * @return this instance
     *
     * @throws IllegalStateException if the action is running.
	 */
	public Action cancelTimeout(){
		return setTimeout(Time.INVALID);
	}

	/**
	 * Adds a {@link Subsystem} requirement for this action.
     *
	 * @param subsystem a system used by this action
     * @return this instance
     *
     * @throws IllegalStateException if the action is running.
	 */
	public Action requires(Subsystem subsystem){
        Objects.requireNonNull(subsystem, "requirement is null");
	    return requires(Collections.singleton(subsystem));
	}

	/**
	 * Adds {@link Subsystem} requirements for this action.
     *
	 * @param subsystems an array of systems used by this action
     * @return this instance
     *
     * @throws IllegalStateException if the action is running.
	 */
	public Action requires(Subsystem... subsystems){
        Objects.requireNonNull(subsystems, "requirements is null");
	    return requires(Arrays.asList(subsystems));
	}

    /**
     * Adds {@link Subsystem} requirements for this action.
     *
     * @param subsystems collection of subsystems to add.
     * @return this instance.
     *
     * @throws IllegalStateException if the action is running.
     */
    public Action requires(Collection<Subsystem> subsystems) {
        Objects.requireNonNull(subsystems, "requirements is null");

        validateNotRunning();
        mRequirements.addAll(subsystems);

        return this;
    }

	/**
	 * Resets the requirements of this action.
     *
     * @return this instance.
     *
     * @throws IllegalStateException if the action is running.
	 */
	public Action resetRequirements(){
	    validateNotRunning();
		mRequirements.clear();

		return this;
	}

	/**
	 * Gets the {@link Subsystem}s which are used by this action.
     *
	 * @return {@link Set} of the used {@link Subsystem}s.
	 */
	public Set<Subsystem> getRequirements(){
		return Collections.unmodifiableSet(mRequirements);
	}

	void markStarted() {
        if(!mIsRunning){
            mIsCanceled = false;
            mIsRunning = true;
        }
    }

    void markCanceled() {
	    if (isRunning()) {
	        mIsCanceled = true;
        }
    }

	void removed(){
		mIsCanceled = false;
		mIsRunning = false;
	}

	void setParent(Action parent) {
	    validateNoParent();
	    validateNotRunning();

	    mParent = Objects.requireNonNull(parent, "parent is null");
        mParent.requires(getRequirements());
    }

    void validateNoParent() {
	    if (mParent != null) {
	        throw new IllegalStateException("Action has a parent");
        }
    }

    void validateRunning() {
        if (!isRunning()) {
            throw new IllegalStateException("action not running");
        }
    }

    void validateNotRunning() {
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

    /**
     * @return returns <b>false</b> if this action can run when disabled,
     *      <b>true</b>.
     */
	public boolean runWhenDisabled() {
	    return false;
    }
}
