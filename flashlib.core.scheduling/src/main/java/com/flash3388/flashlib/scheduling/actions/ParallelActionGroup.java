package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.time.Clock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;

public class ParallelActionGroup extends ActionGroupBase {

    private final Clock mClock;

    private final Collection<ActionContext> mCurrentActions;
    private final ActionContextRunner mContextRunner;

    ParallelActionGroup(Clock clock, Collection<Action> actions, Collection<ActionContext> currentActions) {
        super(actions, false);

        mClock = clock;
        mCurrentActions = currentActions;

        mContextRunner = new ActionContextRunner();
        mRunWhenDisabled = false;
    }

    public ParallelActionGroup(Clock clock) {
        this(clock, new ArrayList<>(3), new ArrayList<>(2));
    }

    public ParallelActionGroup() {
        this(GlobalDependencies.getClock());
    }

    @Override
    public ParallelActionGroup add(Action action) {
        super.add(action);
        return this;
    }

    @Override
    public ParallelActionGroup add(Action... actions) {
        super.add(actions);
        return this;
    }

    @Override
    public ParallelActionGroup add(Collection<Action> actions) {
        super.add(actions);
        return this;
    }

    @Override
    public ParallelActionGroup alongWith(Action... actions) {
        return add(actions);
    }

    @Override
    public final void initialize() {
        for (Action action : mActions) {
            ActionContext actionContext = new ActionContext(action, mClock);
            actionContext.prepareForRun();

            mCurrentActions.add(actionContext);
        }
    }

    @Override
    public final void execute() {
        if (mCurrentActions.isEmpty()) {
            return;
        }

        mCurrentActions.removeIf(mContextRunner);
    }

    @Override
    public boolean isFinished() {
        return mCurrentActions.isEmpty();
    }

    @Override
    public void end(boolean wasInterrupted) {
        if (wasInterrupted) {
            for (ActionContext context : mCurrentActions) {
                context.runCanceled();
            }
        }
    }
}
