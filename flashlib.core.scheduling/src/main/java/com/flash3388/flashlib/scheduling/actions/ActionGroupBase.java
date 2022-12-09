package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.Scheduler;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;

public abstract class ActionGroupBase extends ActionBase implements ActionGroup {

    private final Scheduler mScheduler;
    private final Logger mLogger;
    private final Collection<Action> mActions;
    private final boolean mAllowRequirementCollisions;

    private final Queue<Action> mActionsToExecute;
    private final Collection<ExecutionContext> mRunningActions;

    private boolean mRunWhenDisabled;
    private Runnable mWhenInterrupted;

    protected ActionGroupBase(Scheduler scheduler, Logger logger, Collection<Action> actions, boolean allowRequirementCollisions) {
        super(scheduler);
        mScheduler = scheduler;
        mLogger = logger;
        mActions = actions;
        mAllowRequirementCollisions = allowRequirementCollisions;
        mActionsToExecute = new ArrayDeque<>();
        mRunningActions = new ArrayList<>();
        mRunWhenDisabled = false;
        mWhenInterrupted = null;
    }

    protected ActionGroupBase(Logger logger, Collection<Action> actions, boolean allowRequirementCollisions) {
        this(GlobalDependencies.getScheduler(), logger, actions, allowRequirementCollisions);
    }

    protected ActionGroupBase(Collection<Action> actions, boolean allowRequirementCollisions) {
        this(GlobalDependencies.getLogger(), actions, allowRequirementCollisions);
    }

    @Override
    public ActionGroupBase add(Action action){
        Objects.requireNonNull(action, "action is null");

        ActionConfiguration configuration = action.getConfiguration();

        if (!mAllowRequirementCollisions) {
            if (!Collections.disjoint(getConfiguration().getRequirements(),
                    configuration.getRequirements())) {
                throw new IllegalArgumentException("Actions cannot share requirements");
            }
        }

        if (mActions.isEmpty()) {
            mRunWhenDisabled = configuration.shouldRunWhenDisabled();
        } else {
            mRunWhenDisabled &= configuration.shouldRunWhenDisabled();
        }

        configure()
                .setRunWhenDisabled(mRunWhenDisabled)
                .requires(configuration.getRequirements())
                .save();

        mActions.add(action);

        return this;
    }

    @Override
    public ActionGroupBase add(Action... actions){
        Objects.requireNonNull(actions, "actions is null");
        return add(Arrays.asList(actions));
    }

    @Override
    public ActionGroupBase add(Collection<Action> actions){
        Objects.requireNonNull(actions, "actions is null");
        actions.forEach(this::add);

        return this;
    }

    @Override
    public ActionGroupBase whenInterrupted(Runnable runnable) {
        if (mWhenInterrupted != null) {
            mLogger.debug("whenInterrupted callback overridden for ActionGroup: {}", getConfiguration().getName());
        }

        mWhenInterrupted = runnable;
        return this;
    }

    @Override
    public void initialize() {
        mActionsToExecute.addAll(mActions);
    }

    @Override
    public void execute() {
        if (handleCurrentActions()) {
            onActionFinished();
        }
    }

    @Override
    public boolean isFinished() {
        return mActionsToExecute.isEmpty();
    }

    @Override
    public void end(boolean wasInterrupted) {
        if (wasInterrupted && mWhenInterrupted != null) {
            mWhenInterrupted.run();
        }

        if (wasInterrupted) {
            for (ExecutionContext context : mRunningActions) {
                context.interrupt();
            }
        }
    }

    protected boolean startNextAction() {
        if (mActionsToExecute.isEmpty()) {
            return false;
        }

        Action action = mActionsToExecute.poll();
        ExecutionContext context = mScheduler.createExecutionContext(this, action);
        mRunningActions.add(context);

        return true;
    }

    protected boolean handleCurrentActions() {
        if (mRunningActions.isEmpty()) {
            return false;
        }

        boolean actionFinished = false;

        for (Iterator<ExecutionContext> iterator = mRunningActions.iterator(); iterator.hasNext();) {
            ExecutionContext context = iterator.next();

            if (context.execute() == ExecutionContext.ExecutionResult.FINISHED) {
                iterator.remove();
                actionFinished = true;
            }
        }

        return actionFinished;
    }

    protected abstract void onActionFinished();
}
