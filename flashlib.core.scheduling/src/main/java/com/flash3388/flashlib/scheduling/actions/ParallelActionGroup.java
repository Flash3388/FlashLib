package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;

public class ParallelActionGroup extends ActionGroupBase {

    private final Clock mClock;
    private final Logger mLogger;

    private final Collection<ActionContext> mCurrentActions;
    private final Predicate<ActionContext> mContextRunner;

    ParallelActionGroup(Clock clock, Logger logger,
                        Collection<Action> actions, Collection<ActionContext> currentActions) {
        super(actions, false);

        mClock = clock;
        mLogger = logger;
        mCurrentActions = currentActions;

        mContextRunner = (actionContext)-> {
            if (!actionContext.run()) {
                actionContext.runFinished();
                mLogger.debug("ActionGroup {} finished action {}", ParallelActionGroup.this, actionContext);

                return true;
            }

            return false;
        };
        mRunWhenDisabled = false;
    }

    public ParallelActionGroup(Clock clock, Logger logger) {
        this(clock, logger, new ArrayList<>(3), new ArrayList<>(2));
    }

    public ParallelActionGroup() {
        this(GlobalDependencies.getClock(), GlobalDependencies.getLogger());
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
            mLogger.debug("ActionGroup {} started action {}", this, actionContext);

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
                mLogger.debug("ActionGroup {} interrupted, canceling action {}", this, context);
                context.runCanceled();
            }
        }
    }
}
