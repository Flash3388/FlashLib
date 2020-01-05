package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;

public class SequentialActionGroup extends Action {

    private final Clock mClock;

    private final Collection<Action> mActions;
    private final Queue<ActionContext> mActionQueue;

    private ActionContext mCurrentAction;
    private boolean mRunWhenDisabled;

    public SequentialActionGroup(Scheduler scheduler, Clock clock) {
        super(scheduler);
        mClock = clock;

        mActions = new ArrayList<>(3);
        mActionQueue = new ArrayDeque<>(3);

        mCurrentAction = null;
        mRunWhenDisabled = false;
    }

    public SequentialActionGroup(Clock clock) {
        this(RunningRobot.INSTANCE.get().getScheduler(), clock);
    }

    public SequentialActionGroup() {
        this(RunningRobot.INSTANCE.get().getClock());
    }

    /**
     * Adds an action to run.
     *
     * @param action action to run
     * @return this instance
     */
    public SequentialActionGroup add(Action action){
        Objects.requireNonNull(action, "action is null");

        validateNotRunning();

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
    public SequentialActionGroup add(Action... actions){
        Objects.requireNonNull(actions, "actions is null");
        return add(Arrays.asList(actions));
    }

    /**
     * Adds an array of scheduling to run.
     *
     * @param actions action to run
     * @return this instance
     */
    public SequentialActionGroup add(Collection<Action> actions){
        Objects.requireNonNull(actions, "actions is null");
        actions.forEach(this::add);

        return this;
    }

    @Override
    protected final void initialize() {
        mActions.forEach((action) -> mActionQueue.add(new ActionContext(action, mClock)));

        startNextAction();
    }

    @Override
    protected final void execute() {
        if (mCurrentAction == null) {
            startNextAction();
        }

        handleCurrentAction();
    }

    @Override
    protected boolean isFinished() {
        return mCurrentAction == null && mActionQueue.isEmpty();
    }

    @Override
    protected final void end() {

    }

    @Override
    protected final void interrupted() {
        if (mCurrentAction != null) {
            mCurrentAction.runCanceled();
        }
    }

    @Override
    public final boolean runWhenDisabled() {
        return mRunWhenDisabled;
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
