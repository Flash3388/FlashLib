package edu.flash3388.flashlib.robot.scheduling.actions;

import edu.flash3388.flashlib.robot.RunningRobot;
import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Scheduler;
import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.time.Time;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

/**
 * Provides a series of scheduling to run in a order. Action can run sequentially or parallel to one another.
 * <p>
 * Sequential scheduling cannot run if another action from the group is active currently. Parallel action will run at all
 * conditions.
 * </p>
 *
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class ActionGroup extends Action {
	
	private final Collection<Action> mActions;
	private final ExecutionOrder mExecutionOrder;

	private final Queue<Action> mActionsQueue;
	private final Collection<Action> mCurrentlyRunningActions;
	
	/**
	 * Creates a new empty action group
     *
     * @param executionOrder action execution order
	 */
	public ActionGroup(ExecutionOrder executionOrder) {
		this(executionOrder, new ArrayList<>());
	}

	public ActionGroup(ExecutionOrder executionOrder, Collection<Action> actions) {
	    this(RunningRobot.INSTANCE.get().getScheduler(), RunningRobot.INSTANCE.get().getClock(), executionOrder, actions);
    }

	/* package */ ActionGroup(Scheduler scheduler, Clock clock, ExecutionOrder executionOrder, Collection<Action> actions) {
	    super(scheduler, clock, Time.INVALID);

		mExecutionOrder = executionOrder;
		mActions = actions;

		mActionsQueue = new ArrayDeque<>();
		mCurrentlyRunningActions = new ArrayList<>();
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
	 * @param actions actions to run
	 * @return this instance
	 */
	public ActionGroup add(Action... actions){
		return add(Arrays.asList(actions));
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
	 * Adds an empty action to run for a given time of milliseconds.
	 * 
	 * @param time time for the empty action to run
	 * @return this instance
	 */
	public ActionGroup addWaitAction(Time time){
	    Action action = Actions.empty();
	    action.setTimeout(time);

		return add(action);
	}

	/*package*/ boolean areAnyActionsRunning() {
	    return !mCurrentlyRunningActions.isEmpty();
    }
	
	@Override
	protected final void initialize(){
		mActionsQueue.addAll(mActions);
	}

	@Override
	protected final void execute() {
		tryStartNextAction();
		handleCurrentActions();
	}

	@Override
	protected final boolean isFinished() {
		return mActionsQueue.isEmpty() && mCurrentlyRunningActions.isEmpty();
	}

    @Override
    protected final void interrupted() {
        end();
    }

    @Override
	protected final void end() {
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

		if (mExecutionOrder.canStartNextAction(this)) {
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

		List<Action> currentlyRunning = new ArrayList<>(mCurrentlyRunningActions);

		for (Action action : currentlyRunning) {
			if (!action.isRunning()) {
				mCurrentlyRunningActions.remove(action);
			}
		}
	}
}
