package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.impl.SchedulerStatus;
import com.flash3388.flashlib.scheduling.impl.SchedulingTask;
import com.flash3388.flashlib.scheduling.impl.UserRequests;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.concurrent.SeparateThreadExecutor;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public class SeparateThreadScheduler implements Scheduler {

    private final Clock mClock;
    private final Logger mLogger;
    private final UserRequests mUserRequests;
    private final SchedulerStatus mSchedulerStatus;

    private final Set<Action> mRunningActions;
    private final Map<Requirement, Action> mRunningOnRequirements;
    private final Map<Subsystem, Action> mDefaultActions;

    SeparateThreadScheduler(Executor executor, Clock clock, Logger logger,
                           UserRequests userRequests, SchedulerStatus schedulerStatus,
                           Set<Action> runningActions, Map<Requirement, Action> runningOnRequirements, Map<Subsystem, Action> defaultActions) {
        mClock = clock;
        mLogger = logger;
        mUserRequests = userRequests;
        mSchedulerStatus = schedulerStatus;
        mRunningActions = runningActions;
        mRunningOnRequirements = runningOnRequirements;
        mDefaultActions = defaultActions;

        executor.execute(new SchedulingTask(mUserRequests, mSchedulerStatus, mClock, mLogger));
    }

    public SeparateThreadScheduler(Executor executor, Clock clock, Logger logger) {
        this(executor, clock, logger, new UserRequests(), new SchedulerStatus(),
                new HashSet<>(5), new HashMap<>(5), new HashMap<>(2));
    }

    public SeparateThreadScheduler(Time runInterval, Clock clock, Logger logger) {
        this(new SeparateThreadExecutor(runInterval, "scheduler-thread"), clock, logger);
    }

    @Override
    public void start(Action action) {
        Objects.requireNonNull(action, "action is null");

        if (mRunningActions.contains(action)) {
            throw new IllegalStateException("action is running");
        }

        mRunningActions.add(action);

        Collection<Requirement> requirements = action.getConfiguration().getRequirements();
        updateRequirementsWithAction(action, requirements);

        mUserRequests.actionToStart(action);
    }

    @Override
    public void cancel(Action action) {
        Objects.requireNonNull(action, "action is null");

        if (!mRunningActions.contains(action)) {
            throw new IllegalStateException("action not running");
        }

        mRunningActions.remove(action);
        mUserRequests.actionToCancel(action);
    }

    @Override
    public boolean isRunning(Action action) {
        Objects.requireNonNull(action, "action is null");

        return mRunningActions.contains(action);
    }

    @Override
    public Time getActionRunTime(Action action) {
        Objects.requireNonNull(action, "action is null");

        throw new UnsupportedOperationException("for now");
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");

        for (Action action : mRunningActions) {
            if (predicate.test(action)) {
                mUserRequests.actionToCancel(action);
            }
        }
    }

    @Override
    public void cancelAllActions() {
        throw new UnsupportedOperationException("for now");
    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {
        Objects.requireNonNull(subsystem, "subsystem is null");
        Objects.requireNonNull(action, "action is null");

        if (!action.getConfiguration().getRequirements().contains(subsystem)) {
            throw new IllegalArgumentException("missing requirement on subsystem");
        }

        Action old = mDefaultActions.put(subsystem, action);
        if (old != null && mRunningActions.contains(old)) {
            mUserRequests.actionToCancel(old);
        }
    }

    @Override
    public Optional<Action> getActionRunningOnRequirement(Requirement requirement) {
        Objects.requireNonNull(requirement, "requirement is null");

        return Optional.ofNullable(mRunningOnRequirements.get(requirement));
    }

    @Override
    public void run(SchedulerMode mode) {
        Objects.requireNonNull(mode, "mode is null");

        Collection<Action> actionsFinished = mSchedulerStatus.getActionsFinished();
        if (!actionsFinished.isEmpty()) {
            mLogger.debug("Scheduler finished actions {}", actionsFinished);
        }

        mRunningActions.removeAll(actionsFinished);
        for (Action action : actionsFinished) {
            updateRequirementsActionFinished(action.getConfiguration().getRequirements());
        }

        startDefaultActions(mode);
        updateActionsByMode(mode);
    }

    private void startDefaultActions(SchedulerMode mode) {
        if (mode.isDisabled()) {
            return;
        }

        for (Map.Entry<Subsystem, Action> entry : mDefaultActions.entrySet()) {
            Subsystem subsystem = entry.getKey();
            Action action = entry.getValue();

            if (!mRunningOnRequirements.containsKey(subsystem)) {
                mLogger.debug("Subsystem {} has default {} and is free. Running action", subsystem, action);
                start(action);
            }
        }
    }

    private void updateActionsByMode(SchedulerMode mode) {
        for (Iterator<Action> iterator = mRunningActions.iterator(); iterator.hasNext();) {
            Action action = iterator.next();
            if (mode.isDisabled() && !action.getConfiguration().shouldRunWhenDisabled()) {
                mLogger.debug("Mode {} is disabled and action {} is not approved. Canceling", mode, action);
                iterator.remove();
                mUserRequests.actionToCancel(action);
            }
        }
    }

    private void updateRequirementsWithAction(Action action, Collection<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            Action old = mRunningOnRequirements.put(requirement, action);
            if (old != null && mRunningActions.contains(old)) {
                mLogger.warn("Conflict on requirement {} by old {} and new {}. New receives priority", requirement, old, action);
                mUserRequests.actionToCancel(action);
            }
        }
    }

    private void updateRequirementsActionFinished(Collection<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            mRunningOnRequirements.remove(requirement);
        }
    }
}
