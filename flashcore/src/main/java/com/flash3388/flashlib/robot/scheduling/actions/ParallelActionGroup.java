package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.time.Clock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;

public class ParallelActionGroup extends ActionBase implements ActionGroup {

    private final Clock mClock;

    private final Collection<Action> mActions;
    private final Collection<ActionContext> mCurrentActions;
    private final ContextRunner mContextRunner;

    private boolean mRunWhenDisabled;

    ParallelActionGroup(Clock clock, Collection<Action> actions, Collection<ActionContext> currentActions) {
        mClock = clock;
        mActions = actions;
        mCurrentActions = currentActions;

        mContextRunner = new ContextRunner();
        mRunWhenDisabled = false;
    }

    public ParallelActionGroup(Clock clock) {
        this(clock, new ArrayList<>(3), new ArrayList<>(2));
    }

    public ParallelActionGroup() {
        this(RunningRobot.getInstance().getClock());
    }

    /**
     * Adds an action to run.
     *
     * @param action action to run
     * @return this instance
     */
    @Override
    public ParallelActionGroup add(Action action){
        Objects.requireNonNull(action, "action is null");

        ActionConfiguration configuration = action.getConfiguration();

        if (!Collections.disjoint(getConfiguration().getRequirements(),
                configuration.getRequirements())) {
            throw new IllegalArgumentException("Actions in Parallel execution cannot share requirements");
        }

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
    public ParallelActionGroup add(Action... actions){
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
    public ParallelActionGroup add(Collection<Action> actions){
        Objects.requireNonNull(actions, "actions is null");
        actions.forEach(this::add);

        return this;
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

    private static class ContextRunner implements Predicate<ActionContext> {

        @Override
        public boolean test(ActionContext actionContext) {
            if (!actionContext.run()) {
                actionContext.runFinished();

                return true;
            }

            return false;
        }
    }
}
