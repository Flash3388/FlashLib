package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.time.Clock;

import java.util.ArrayList;
import java.util.Collection;

public class ParallelRaceActionGroup extends ActionGroupBase {

    private final Clock mClock;

    private final Collection<ActionContext> mCurrentActions;
    private final ActionContextRunner mContextRunner;

    private ActionContext mFinishedCommand;

    ParallelRaceActionGroup(Clock clock, Collection<Action> actions, Collection<ActionContext> currentActions) {
        super(actions, false);

        mClock = clock;
        mCurrentActions = currentActions;

        mContextRunner = new ActionContextRunner();
        mRunWhenDisabled = false;
        mFinishedCommand = null;
    }

    public ParallelRaceActionGroup(Clock clock) {
        this(clock, new ArrayList<>(3), new ArrayList<>(2));
    }

    public ParallelRaceActionGroup() {
        this(GlobalDependencies.getClock());
    }

    @Override
    public ParallelRaceActionGroup add(Action action) {
        super.add(action);
        return this;
    }

    @Override
    public ParallelRaceActionGroup add(Action... actions) {
        super.add(actions);
        return this;
    }

    @Override
    public ParallelRaceActionGroup add(Collection<Action> actions) {
        super.add(actions);
        return this;
    }

    @Override
    public ParallelRaceActionGroup raceWith(Action... actions) {
        return add(actions);
    }

    @Override
    public final void initialize() {
        for (Action action : mActions) {
            ActionContext actionContext = new ActionContext(action, mClock);
            actionContext.prepareForRun();

            mCurrentActions.add(actionContext);
        }

        mFinishedCommand = null;
    }

    @Override
    public final void execute() {
        if (mCurrentActions.isEmpty()) {
            return;
        }

        for (ActionContext context : mCurrentActions) {
            if (!mContextRunner.test(context)) {
                mFinishedCommand = context;
                break;
            }
        }
    }

    @Override
    public boolean isFinished() {
        return mFinishedCommand != null;
    }

    @Override
    public void end(boolean wasInterrupted) {
        for (ActionContext context : mCurrentActions) {
            if (wasInterrupted || !context.equals(mFinishedCommand)) {
                context.runCanceled();
            }
        }

        if (!wasInterrupted && mFinishedCommand != null) {
            mFinishedCommand.runFinished();
        }
    }
}
