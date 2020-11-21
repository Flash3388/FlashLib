package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.impl.SynchronousActionContext;
import com.flash3388.flashlib.time.Clock;

import java.util.ArrayList;
import java.util.Collection;

public class ParallelRaceActionGroup extends ActionGroupBase {

    private final Clock mClock;
    private final Collection<SynchronousActionContext> mCurrentActions;

    private SynchronousActionContext mFinishedCommand;

    ParallelRaceActionGroup(Clock clock, Collection<Action> actions, Collection<SynchronousActionContext> currentActions) {
        super(actions, false);

        mClock = clock;
        mCurrentActions = currentActions;

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
            SynchronousActionContext actionContext = new SynchronousActionContext(action, mClock);
            actionContext.startRun();

            mCurrentActions.add(actionContext);
        }

        mFinishedCommand = null;
    }

    @Override
    public final void execute() {
        if (mCurrentActions.isEmpty()) {
            return;
        }

        for (SynchronousActionContext context : mCurrentActions) {
            if (!context.run()) {
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
        for (SynchronousActionContext context : mCurrentActions) {
            if (wasInterrupted || !context.equals(mFinishedCommand)) {
                context.cancelAndFinish();
            }
        }
    }
}
