package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class ParallelRaceActionGroup extends ActionGroupBase {

    private final Clock mClock;
    private final Logger mLogger;

    private final Collection<ActionContext> mCurrentActions;
    private final Predicate<ActionContext> mContextRunner;

    private ActionContext mFinishedCommand;

    ParallelRaceActionGroup(Clock clock, Logger logger, Collection<Action> actions, Collection<ActionContext> currentActions) {
        super(actions, false);

        mClock = clock;
        mLogger = logger;
        mCurrentActions = currentActions;

        mContextRunner = (actionContext)-> {
            if (!actionContext.run()) {
                actionContext.runFinished();
                mLogger.debug("ActionGroup {} finished action {}", ParallelRaceActionGroup.this, actionContext);

                return true;
            }

            return false;
        };
        mRunWhenDisabled = false;
        mFinishedCommand = null;
    }

    public ParallelRaceActionGroup(Clock clock, Logger logger) {
        this(clock, logger, new ArrayList<>(3), new ArrayList<>(2));
    }

    public ParallelRaceActionGroup() {
        this(GlobalDependencies.getClock(), GlobalDependencies.getLogger());
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
            mLogger.debug("ActionGroup {} started action {}", this, actionContext);

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
                mLogger.debug("ActionGroup {} interrupted, canceling action {}", this, context);
                context.runCanceled();
            }
        }

        if (!wasInterrupted && mFinishedCommand != null) {
            mFinishedCommand.runFinished();
            mLogger.debug("ActionGroup {} finished action {}", this, mFinishedCommand);
        }
    }
}
