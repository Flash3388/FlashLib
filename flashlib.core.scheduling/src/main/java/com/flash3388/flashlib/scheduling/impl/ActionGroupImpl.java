package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.scheduling.actions.ActionGroup;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;

public class ActionGroupImpl extends ActionBase implements ActionGroup {

    private final Logger mLogger;
    private final GroupPolicy mGroupPolicy;
    private final Collection<Action> mActions;

    private final Queue<Action> mActionsToExecute;
    private final Collection<ExecutionContext> mRunningActions;

    private Runnable mWhenInterrupted;
    private boolean mForcedEarlyFinish;

    public ActionGroupImpl(Scheduler scheduler,
                           Logger logger,
                           GroupPolicy groupPolicy,
                           Collection<Action> actions,
                           Queue<Action> actionsToExecute,
                           Collection<ExecutionContext> runningActions) {
        super(scheduler);
        mLogger = logger;
        mGroupPolicy = groupPolicy;
        mActions = actions;
        mActionsToExecute = actionsToExecute;
        mRunningActions = runningActions;
        mWhenInterrupted = null;
        mForcedEarlyFinish = false;
    }

    public ActionGroupImpl(Scheduler scheduler, Logger logger, GroupPolicy groupPolicy) {
        this(scheduler, logger, groupPolicy,
                new ArrayList<>(5),
                new ArrayDeque<>(5),
                new ArrayList<>(2));
    }

    @Override
    public ActionGroup add(Action action) {
        Objects.requireNonNull(action, "action is null");

        ActionConfiguration configuration = action.getConfiguration();

        if (!mGroupPolicy.shouldAllowRequirementCollisions()) {
            if (!Collections.disjoint(getConfiguration().getRequirements(),
                    configuration.getRequirements())) {
                throw new IllegalArgumentException("Actions cannot share requirements");
            }
        }

        boolean runWhenDisabled = getConfiguration().shouldRunWhenDisabled();

        if (mActions.isEmpty()) {
            runWhenDisabled = configuration.shouldRunWhenDisabled();
        } else {
            runWhenDisabled &= configuration.shouldRunWhenDisabled();
        }

        configure()
                .setRunWhenDisabled(runWhenDisabled)
                .requires(configuration.getRequirements())
                .save();

        mActions.add(action);

        return this;
    }

    @Override
    public ActionGroup add(Action... actions) {
        Objects.requireNonNull(actions, "actions is null");
        return add(Arrays.asList(actions));
    }

    @Override
    public ActionGroup add(Collection<Action> actions) {
        Objects.requireNonNull(actions, "actions is null");
        actions.forEach(this::add);

        return this;
    }

    @Override
    public ActionGroup andThen(Action... actions) {
        if (!mGroupPolicy.shouldExecuteActionsInParallel()) {
            return add(actions);
        } else {
            return super.andThen(actions);
        }
    }

    @Override
    public ActionGroup alongWith(Action... actions) {
        if (mGroupPolicy.shouldExecuteActionsInParallel()) {
            return add(actions);
        } else {
            return super.alongWith(actions);
        }
    }

    @Override
    public ActionGroup raceWith(Action... actions) {
        if (mGroupPolicy.shouldExecuteActionsInParallel() &&
                mGroupPolicy.shouldStopOnFirstActionFinished()) {
            return add(actions);
        } else {
            return super.raceWith(actions);
        }
    }

    @Override
    public ActionGroup whenInterrupted(Runnable runnable) {
        if (mWhenInterrupted != null) {
            mLogger.debug("whenInterrupted callback overridden for ActionGroup: {}", getConfiguration().getName());
        }

        mWhenInterrupted = runnable;
        return this;
    }

    @Override
    public void initialize(ActionControl control) {
        mForcedEarlyFinish = false;

        mActionsToExecute.addAll(mActions);

        if (mGroupPolicy.shouldExecuteActionsInParallel()) {
            // start all actions
            //noinspection StatementWithEmptyBody
            while (startNextAction(control));
        } else {
            startNextAction(control);
        }
    }

    @Override
    public void execute(ActionControl control) {
        if (handleCurrentActions()) {
            // action finished
            if (mGroupPolicy.shouldStopOnFirstActionFinished()) {
                mForcedEarlyFinish = true;
                control.finish();
                return;
            }
        }

        if (!mGroupPolicy.shouldExecuteActionsInParallel() && mRunningActions.isEmpty()) {
            startNextAction(control);
        }

        if (mRunningActions.isEmpty() && mActionsToExecute.isEmpty()) {
            control.finish();
        }
    }

    @Override
    public void end(FinishReason reason) {
        if (reason.isInterrupt() || mForcedEarlyFinish) {
            for (ExecutionContext context : mRunningActions) {
                context.interrupt();
            }
        }

        if (reason.isInterrupt() && mWhenInterrupted != null) {
            mWhenInterrupted.run();
        }

        mActionsToExecute.clear();
        mRunningActions.clear();
    }

    private boolean startNextAction(ActionControl control) {
        if (mActionsToExecute.isEmpty()) {
            return false;
        }

        Action action = mActionsToExecute.poll();
        ExecutionContext context = control.createExecutionContext(action);
        context.start();
        mRunningActions.add(context);

        return true;
    }

    private boolean handleCurrentActions() {
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
}
