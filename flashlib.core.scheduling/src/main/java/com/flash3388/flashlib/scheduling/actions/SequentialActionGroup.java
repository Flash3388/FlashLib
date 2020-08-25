package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;

public class SequentialActionGroup extends ActionGroupBase {

    private final Clock mClock;

    private final Queue<ActionContext> mActionQueue;

    private ActionContext mCurrentAction;

    SequentialActionGroup(Scheduler scheduler, Clock clock,
                          Collection<Action> actions, Queue<ActionContext> actionQueue) {
        super(scheduler, actions, true);

        mClock = clock;
        mActionQueue = actionQueue;

        mCurrentAction = null;
        mRunWhenDisabled = false;
    }

    public SequentialActionGroup(Clock clock) {
        this(GlobalDependencies.getScheduler(), clock,
                new ArrayList<>(3), new ArrayDeque<>(3));
    }

    public SequentialActionGroup() {
        this(GlobalDependencies.getClock());
    }

    @Override
    public SequentialActionGroup add(Action action) {
        super.add(action);
        return this;
    }

    @Override
    public SequentialActionGroup add(Action... actions) {
        super.add(actions);
        return this;
    }

    @Override
    public SequentialActionGroup add(Collection<Action> actions) {
        super.add(actions);
        return this;
    }

    @Override
    public SequentialActionGroup andThen(Action... actions) {
        return add(actions);
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
