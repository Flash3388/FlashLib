package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

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
@SuppressWarnings("jol")
public class ActionGroup extends Action {
	
	private final Collection<Action> mActions;
	private final ExecutionOrder mExecutionOrder;

	private final Queue<Action> mActionsQueue;
	private final Collection<Action> mCurrentlyRunningActions;

	private final Logger mLogger;

	private Runnable mRunWhenInterrupted;
	private boolean mRunWhenDisabled;
	
	/**
	 * Creates a new empty action group
     *
     * @param executionOrder action execution order
	 */
	public ActionGroup(ExecutionOrder executionOrder) {
		this(executionOrder, new ArrayList<>());
	}

	public ActionGroup(ExecutionOrder executionOrder, Collection<Action> actions) {
	    this(RunningRobot.INSTANCE.get().getScheduler(), RunningRobot.INSTANCE.get().getClock(), executionOrder, actions, RunningRobot.INSTANCE.get().getLogger());
    }

    /* package */ ActionGroup(Scheduler scheduler, Clock clock, ExecutionOrder executionOrder, Collection<Action> actions) {
        this(scheduler, clock, executionOrder, actions, Logging.stub());
    }

	/* package */ ActionGroup(Scheduler scheduler, Clock clock, ExecutionOrder executionOrder, Collection<Action> actions, Logger logger) {
	    super(scheduler, clock, Time.INVALID);

		mExecutionOrder = Objects.requireNonNull(executionOrder, "executionOrder is null");
        mLogger = logger;

		mActions = new ArrayList<>();
		add(Objects.requireNonNull(actions, "actions is null"));

		mActionsQueue = new ArrayDeque<>();
		mCurrentlyRunningActions = new ArrayList<>();

		mRunWhenDisabled = true;
	}

	/**
	 * Adds an action to run.
	 * 
	 * @param action action to run
	 * @return this instance
	 */
	public ActionGroup add(Action action){
	    Objects.requireNonNull(action, "action is null");

	    validateNotRunning();

        if (mExecutionOrder == ExecutionOrder.PARALLEL &&
                !Collections.disjoint(getRequirements(), action.getRequirements())) {
            throw new IllegalArgumentException("Actions in Parallel execution cannot share requirements");
        }

        mActions.add(action);
        action.setParent(this);

        mRunWhenDisabled &= action.runWhenDisabled();

        return this;
	}

	/**
	 * Adds an array of scheduling to run.
	 * 
	 * @param actions actions to run
	 * @return this instance
	 */
	public ActionGroup add(Action... actions){
	    Objects.requireNonNull(actions, "actions is null");
		return add(Arrays.asList(actions));
	}

    /**
     * Adds an array of scheduling to run.
     *
     * @param actions action to run
     * @return this instance
     */
    public ActionGroup add(Collection<Action> actions){
        Objects.requireNonNull(actions, "actions is null");
        actions.forEach(this::add);

        return this;
    }

    public ActionGroup whenInterrupted(Runnable runnable) {
	    mRunWhenInterrupted = Objects.requireNonNull(runnable, "runnable is null");
	    return this;
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
        whenInterrupted();
    }

    @Override
	protected final void end() {
		for (Action action : mCurrentlyRunningActions) {
			action.markCanceled();
			action.removed();
		}

		mCurrentlyRunningActions.clear();
		mActionsQueue.clear();
	}

    @Override
    protected final boolean runWhenDisabled() {
        return mRunWhenDisabled;
    }

    private void whenInterrupted() {
	    if (mRunWhenInterrupted != null) {
	        mRunWhenInterrupted.run();
        }
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

		handleConflicts(nextAction);
		nextAction.markStarted();

		mCurrentlyRunningActions.add(nextAction);
	}

    private void handleConflicts(Action nextAction) {
        Set<Subsystem> requirements = nextAction.getRequirements();
        for (Action action : mCurrentlyRunningActions) {
            if (!Collections.disjoint(requirements, action.getRequirements())) {
                action.markCanceled();

                mLogger.warn("Requirements conflict in ActionGroup between {} and new action {}",
                        action.toString(), nextAction.toString());
            }
        }
    }

    private void handleCurrentActions() {
		if (mCurrentlyRunningActions.isEmpty()) {
			return;
		}

		mCurrentlyRunningActions.removeIf((action) -> {
	        if (!action.run()) {
	            action.removed();
	            return true;
            }

            return false;
        });
	}
}
