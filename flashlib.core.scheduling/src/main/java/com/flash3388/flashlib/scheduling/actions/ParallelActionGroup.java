package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;

import java.util.ArrayList;
import java.util.Collection;

public class ParallelActionGroup extends ActionGroupBase {

    private final Clock mClock;

    private final Collection<SynchronousActionContext> mCurrentActions;

    ParallelActionGroup(Scheduler scheduler, Clock clock,
                        Collection<Action> actions, Collection<SynchronousActionContext> currentActions) {
        super(scheduler, actions, false);

        mClock = clock;
        mCurrentActions = currentActions;
    }

    public ParallelActionGroup() {
        this(GlobalDependencies.getScheduler(), GlobalDependencies.getClock(),
                new ArrayList<>(3), new ArrayList<>(2));
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
            SynchronousActionContext actionContext = new SynchronousActionContext(action, mClock);
            actionContext.startRun();

            mCurrentActions.add(actionContext);
        }
    }

    @Override
    public final void execute() {
        if (mCurrentActions.isEmpty()) {
            return;
        }

        mCurrentActions.removeIf(context -> !context.run());
    }

    @Override
    public boolean isFinished() {
        return mCurrentActions.isEmpty();
    }

    @Override
    public void end(boolean wasInterrupted) {
        if (wasInterrupted) {
            for (SynchronousActionContext context : mCurrentActions) {
                context.cancelAndFinish();
            }
        }
    }
}
