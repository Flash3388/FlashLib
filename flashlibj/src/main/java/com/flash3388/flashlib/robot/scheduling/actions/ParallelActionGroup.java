package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class ParallelActionGroup extends Action {

    private final Clock mClock;

    private final Collection<Action> mActions;
    private final Collection<ActionContext> mCurrentActions;

    private boolean mRunWhenDisabled;

    public ParallelActionGroup(Scheduler scheduler, Clock clock) {
        super(scheduler);
        mClock = clock;

        mActions = new ArrayList<>(3);
        mCurrentActions = new ArrayList<>(2);

        mRunWhenDisabled = false;
    }

    public ParallelActionGroup(Clock clock) {
        this(RunningRobot.INSTANCE.get().getScheduler(), clock);
    }

    public ParallelActionGroup() {
        this(RunningRobot.INSTANCE.get().getClock());
    }

    /**
     * Adds an action to run.
     *
     * @param action action to run
     * @return this instance
     */
    public ParallelActionGroup add(Action action){
        Objects.requireNonNull(action, "action is null");

        validateNotRunning();

        if (!Collections.disjoint(getRequirements(), action.getRequirements())) {
            throw new IllegalArgumentException("Actions in Parallel execution cannot share requirements");
        }

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
    public ParallelActionGroup add(Collection<Action> actions){
        Objects.requireNonNull(actions, "actions is null");
        actions.forEach(this::add);

        return this;
    }

    @Override
    protected final void initialize() {
        for (Action action : mActions) {
            ActionContext actionContext = new ActionContext(action, mClock);
            actionContext.prepareForRun();

            mCurrentActions.add(actionContext);
        }
    }

    @Override
    protected final void execute() {
        if (mCurrentActions.isEmpty()) {
            return;
        }

        mCurrentActions.removeIf((ctx) -> {
            if (!ctx.run()) {
                ctx.runFinished();

                return true;
            }

            return false;
        });
    }

    @Override
    protected boolean isFinished() {
        return mCurrentActions.isEmpty();
    }

    @Override
    protected final void end() {

    }

    @Override
    protected final void interrupted() {
        for (ActionContext context : mCurrentActions) {
            context.runCanceled();
        }
    }

    @Override
    public final boolean runWhenDisabled() {
        return mRunWhenDisabled;
    }
}
