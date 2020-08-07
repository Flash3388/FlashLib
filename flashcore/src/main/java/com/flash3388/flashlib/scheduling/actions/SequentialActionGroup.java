package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;

public class SequentialActionGroup extends ActionBase implements ActionGroup {

    private final Clock mClock;

    private final Collection<Action> mActions;
    private final Queue<ActionContext> mActionQueue;

    private ActionContext mCurrentAction;
    private boolean mRunWhenDisabled;

    SequentialActionGroup(Scheduler scheduler, Clock clock,
                          Collection<Action> actions, Queue<ActionContext> actionQueue) {
        super(scheduler);
        mClock = clock;

        mActions = actions;
        mActionQueue = actionQueue;

        mCurrentAction = null;
        mRunWhenDisabled = false;
    }

    public SequentialActionGroup(Clock clock) {
        this(RunningRobot.getInstance().getScheduler(), clock,
                new ArrayList<>(3), new ArrayDeque<>(3));
    }

    public SequentialActionGroup() {
        this(RunningRobot.getInstance().getClock());
    }

    /**
     * Adds an action to run.
     *
     * @param action action to run
     * @return this instance
     */
    @Override
    public SequentialActionGroup add(Action action) {
        Objects.requireNonNull(action, "action is null");

        ActionConfiguration configuration = action.getConfiguration();

        if (mActions.isEmpty()) {
            mRunWhenDisabled = configuration.shouldRunWhenDisabled();
        } else {
            mRunWhenDisabled &= configuration.shouldRunWhenDisabled();
        }

        configure()
                .setRunWhenDisabled(mRunWhenDisabled)
                .save();

        mActions.add(action);

        return this;
    }

    /**
     * Adds an array of scheduling to run.
     *
     * @param actions actions to run
     * @return this instance
     */
    @Override
    public SequentialActionGroup add(Action... actions) {
        Objects.requireNonNull(actions, "actions is null");
        return add(Arrays.asList(actions));
    }

    /**
     * Adds an array of scheduling to run.
     *
     * @param actions action to run
     * @return this instance
     */
    @Override
    public SequentialActionGroup add(Collection<Action> actions) {
        Objects.requireNonNull(actions, "actions is null");
        actions.forEach(this::add);

        return this;
    }

    @Override
    public final void initialize() {
        mActions.forEach((action) -> mActionQueue.add(new ActionContext(action, mClock)));
        startNextAction();
    }

    @Override
    public final void execute() {
        if (mCurrentAction == null) {
            startNextAction();
        }

        handleCurrentAction();
    }

    @Override
    public boolean isFinished() {
        return mCurrentAction == null && mActionQueue.isEmpty();
    }

    @Override
    public void end(boolean wasInterrupted) {
        if (wasInterrupted && mCurrentAction != null) {
            mCurrentAction.runCanceled();
        }
    }

    private void startNextAction() {
        if (mActionQueue.isEmpty()) {
            return;
        }

        mCurrentAction = mActionQueue.poll();
        mCurrentAction.prepareForRun();
    }

    private void handleCurrentAction() {
        if (mCurrentAction == null) {
            return;
        }

        if (!mCurrentAction.run()) {
            mCurrentAction.runFinished();
            mCurrentAction = null;
        }
    }
}
