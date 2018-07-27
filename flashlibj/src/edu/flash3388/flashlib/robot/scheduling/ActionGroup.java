package edu.flash3388.flashlib.robot.scheduling;

import java.util.*;

/**
 * Provides a series of scheduling to run in a order. Action can run sequentially or parallel to one another.
 * <p>
 * Sequential scheduling cannot run if another action from the group is active currently. Parallel action will run at all
 * conditions.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class ActionGroup extends Action {

	public enum ActionTiming {
		SEQUENTIAL,
		PARALLEL
	}
	
	private final Collection<Action> mActions;
	private final ActionTiming mTiming;

	private final Queue<Action> mActionsQueue;
	private final Collection<Action> mCurrentlyRunningActions;
	
	/**
	 * Creates a new empty action group
	 */
	public ActionGroup(ActionTiming timing) {
		this(timing, new ArrayList<Action>());
	}

	public ActionGroup(ActionTiming timing, Collection<Action> actions) {
		mTiming = timing;
		mActions = actions;

		mActionsQueue = new ArrayDeque<Action>();
		mCurrentlyRunningActions = new ArrayList<Action>();
	}
	
	/**
	 * Adds an action to run sequentially with a timeout in seconds.
	 * 
	 * @param action action to run
	 * @param timeout timeout in seconds for the action
	 * @return this instance
	 */
	public ActionGroup add(Action action, double timeout){
		return add(new TimedAction(action, timeout));
	}

	/**
	 * Adds an action to run.
	 * 
	 * @param action action to run
	 * @return this instance
	 */
	public ActionGroup add(Action action){
		mActions.add(action);
		return this;
	}

	/**
	 * Adds an array of scheduling to run.
	 * 
	 * @param actions action to run
	 * @return this instance
	 */
	public ActionGroup add(Collection<Action> actions){
		mActions.addAll(actions);
		return this;
	}

	/**
	 * Adds an empty action to run for few seconds.
	 * 
	 * @param seconds seconds for the empty action to run
	 * @return this instance
	 */
	public ActionGroup addWaitAction(double seconds){
		return add(Action.EMPTY, seconds);
	}
	
	@Override
	protected void initialize(){
		mActionsQueue.addAll(mActions);
	}

	@Override
	protected void execute() {
		tryStartNextAction();
		handleCurrentActions();
	}

	@Override
	protected boolean isFinished() {
		return mActionsQueue.isEmpty() && mCurrentlyRunningActions.isEmpty();
	}

	@Override
	protected void end() {
		for (Action action : mCurrentlyRunningActions) {
			action.cancel();
		}

		mCurrentlyRunningActions.clear();
		mActionsQueue.clear();
	}

	private void tryStartNextAction() {
		if (mActionsQueue.isEmpty()) {
			return;
		}

		if (mTiming == ActionTiming.SEQUENTIAL && mCurrentlyRunningActions.isEmpty()) {
			startNextAction();
		} else if (mTiming == ActionTiming.PARALLEL) {
			startNextAction();
		}
	}

	private void startNextAction() {
		Action nextAction = mActionsQueue.poll();
		if (nextAction == null) {
			return;
		}

		nextAction.start();

		mCurrentlyRunningActions.add(nextAction);
	}

	private void handleCurrentActions() {
		if (mCurrentlyRunningActions.isEmpty()) {
			return;
		}

		List<Action> currentlyRunning = new ArrayList<Action>(mCurrentlyRunningActions);

		for (Action action : currentlyRunning) {
			if (!action.isRunning()) {
				mCurrentlyRunningActions.remove(action);
			}
		}
	}
}
