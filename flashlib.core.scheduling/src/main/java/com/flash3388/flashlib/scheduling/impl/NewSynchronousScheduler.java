package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionHasPreferredException;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.triggers.Trigger;
import com.flash3388.flashlib.scheduling.triggers.TriggerActivationAction;
import com.flash3388.flashlib.scheduling.triggers.TriggerImpl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class NewSynchronousScheduler implements Scheduler {

    private final Clock mClock;
    private final Logger mLogger;

    private final Map<Action, RunningActionContext> mPendingActions;
    private final Map<Action, RunningActionContext> mRunningActions;
    private final Map<Requirement, Action> mRequirementsUsage;
    private final Map<Subsystem, Action> mDefaultActions;

    private final Collection<RunningActionContext> mTempCopy;
    private final Collection<Action> mTempToRemove;

    NewSynchronousScheduler(Clock clock, Logger logger,
                           Map<Action, RunningActionContext> pendingActions,
                           Map<Action, RunningActionContext> runningActions,
                           Map<Requirement, Action> requirementsUsage,
                           Map<Subsystem, Action> defaultActions) {
        mClock = clock;
        mLogger = logger;
        mPendingActions = pendingActions;
        mRunningActions = runningActions;
        mRequirementsUsage = requirementsUsage;
        mDefaultActions = defaultActions;

        mTempCopy = new ArrayList<>();
        mTempToRemove = new ArrayList<>();
    }

    public NewSynchronousScheduler(Clock clock, Logger logger) {
        this(clock, logger,
                new LinkedHashMap<>(5), new LinkedHashMap<>(10),
                new HashMap<>(10), new HashMap<>(5));
    }

    @Override
    public void start(Action action) {
        if (mPendingActions.containsKey(action) || mRunningActions.containsKey(action)) {
            throw new IllegalArgumentException("Action already started");
        }

        RunningActionContext context = new RunningActionContext(action, mLogger);

        if (!tryStartingAction(context)) {
            mPendingActions.put(action, context);
            mLogger.debug("Action {} pending", context);
        }
    }

    @Override
    public void cancel(Action action) {
        RunningActionContext context = mPendingActions.remove(action);
        if (context != null) {
            mLogger.debug("Action {} removed (from pending)", context);
            return;
        }

        context = mRunningActions.remove(action);
        if (context != null) {
            cancelAndEnd(context);
            return;
        }

        throw new IllegalArgumentException("Action not running");
    }

    @Override
    public boolean isRunning(Action action) {
        return mPendingActions.containsKey(action) || mRunningActions.containsKey(action);
    }

    @Override
    public Time getActionRunTime(Action action) {
        if (mPendingActions.containsKey(action)) {
            return Time.seconds(0);
        }

        RunningActionContext context = mRunningActions.get(action);
        if (context != null) {
            return context.getStartTime();
        }

        throw new IllegalArgumentException("Action not running");
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
        mPendingActions.values().removeIf(context -> predicate.test(context.getAction()));

        for (Iterator<RunningActionContext> iterator = mRunningActions.values().iterator(); iterator.hasNext();) {
            RunningActionContext context = iterator.next();

            if (predicate.test(context.getAction())) {
                cancelAndEnd(context);
                iterator.remove();
            }
        }
    }

    @Override
    public void cancelAllActions() {
        mPendingActions.clear();

        for (Iterator<RunningActionContext> iterator = mRunningActions.values().iterator(); iterator.hasNext();) {
            RunningActionContext context = iterator.next();
            cancelAndEnd(context);
            iterator.remove();
        }
    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {
        mDefaultActions.put(subsystem, action);
    }

    @Override
    public Optional<Action> getActionRunningOnRequirement(Requirement requirement) {
        return Optional.ofNullable(mRequirementsUsage.get(requirement));
    }

    @Override
    public void run(SchedulerMode mode) {
        mTempCopy.clear();
        mTempToRemove.clear();
        mTempCopy.addAll(mRunningActions.values());

        for (RunningActionContext context : mTempCopy) {
            if (mode.isDisabled() && !context.shouldRunInDisabled()) {
                context.markForCancellation();
                mLogger.warn("Action {} is not allowed to run in disabled. Cancelling", context);
            }

            if (context.iterate(mClock.currentTime())) {
                // finished execution
                removeFromRequirements(context);
                mTempToRemove.add(context.getAction());

                mLogger.debug("Action {} finished", context);
            }
        }

        mTempToRemove.forEach(mRunningActions::remove);

        mTempCopy.clear();
        mTempToRemove.clear();
        mTempCopy.addAll(mPendingActions.values());

        for (RunningActionContext context : mTempCopy) {
            if (tryStartingAction(context)) {
                mTempToRemove.add(context.getAction());
            }
        }

        mTempToRemove.forEach(mPendingActions::remove);

        if (!mode.isDisabled()) {
            for (Map.Entry<Subsystem, Action> entry : mDefaultActions.entrySet()) {
                if (canStartDefaultAction(entry.getValue())) {
                    start(entry.getValue());
                }
            }
        }
    }

    @Override
    public Trigger newTrigger(BooleanSupplier condition) {
        TriggerImpl trigger = new TriggerImpl();

        Action action = new TriggerActivationAction(this, condition, trigger)
                .requires(trigger);
        start(action);

        return trigger;
    }

    private Set<RunningActionContext> getConflictingOnRequirements(RunningActionContext context) {
        Set<RunningActionContext> conflicts = new HashSet<>();
        for (Requirement requirement : context.getRequirements()) {
            Action current = mRequirementsUsage.get(requirement);
            if (current != null) {
                RunningActionContext currentContext = mRunningActions.get(current);
                if (currentContext.isPreferred()) {
                    // cannot cancel it as it is the preferred one.
                    // will have to wait for it to finish

                    mLogger.warn("Action {} has conflict with (PREFERRED) {} on {}. Not canceling old, must wait for it to finish.",
                            context, current, requirement);

                    throw new ActionHasPreferredException();
                }

                conflicts.add(currentContext);
            }
        }

        return conflicts;
    }

    private void setOnRequirements(RunningActionContext context) {
        for (Requirement requirement : context.getRequirements()) {
            mRequirementsUsage.put(requirement, context.getAction());
        }
    }

    private void removeFromRequirements(RunningActionContext context) {
        for (Requirement requirement : context.getRequirements()) {
            mRequirementsUsage.remove(requirement);
        }
    }

    private boolean tryStartingAction(RunningActionContext context) {
        try {
            Set<RunningActionContext> conflicts = getConflictingOnRequirements(context);
            conflicts.forEach((conflict)-> {
                cancelAndEnd(conflict);
                mRunningActions.remove(conflict.getAction());

                mLogger.warn("Action {} has conflict with {}. Canceling old.",
                        context, conflict);
            });

            // no conflicts, let's start

            context.markStarted(mClock.currentTime());
            setOnRequirements(context);
            mRunningActions.put(context.getAction(), context);

            mLogger.debug("Action {} started running", context);

            return true;
        } catch (ActionHasPreferredException e) {
            return false;
        }
    }

    private boolean canStartDefaultAction(Action action) {
        for(Requirement requirement : action.getConfiguration().getRequirements()) {
            if (mRequirementsUsage.containsKey(requirement)) {
                return false;
            }
        }

        for (RunningActionContext context : mPendingActions.values()) {
            if (!Collections.disjoint(context.getRequirements(), action.getConfiguration().getRequirements())) {
                return false;
            }
        }

        return true;
    }

    private void cancelAndEnd(RunningActionContext context) {
        context.markForCancellation();
        context.iterate(mClock.currentTime());
        removeFromRequirements(context);

        mLogger.debug("Action {} finished", context);
    }
}
