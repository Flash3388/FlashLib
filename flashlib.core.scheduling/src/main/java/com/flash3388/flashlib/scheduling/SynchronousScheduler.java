package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.impl.SynchronousActionContext;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class SynchronousScheduler implements Scheduler {

    private final Clock mClock;
    private final Logger mLogger;

    private final Map<Action, SynchronousActionContext> mActionsContexts;
    private final Map<Requirement, Action> mRunningOnRequirements;
    private final Map<Subsystem, Action> mDefaultActions;

    SynchronousScheduler(Clock clock, Logger logger,
                         Map<Action, SynchronousActionContext> actionsContexts, Map<Requirement, Action> runningOnRequirements,
                         Map<Subsystem, Action> defaultActions) {
        mClock = clock;
        mLogger = logger;

        mActionsContexts = actionsContexts;
        mRunningOnRequirements = runningOnRequirements;
        mDefaultActions = defaultActions;
    }

    public SynchronousScheduler(Clock clock, Logger logger) {
        this(clock, logger, new HashMap<>(5), new HashMap<>(5), new HashMap<>(2));
    }

    @Override
    public void start(Action action) {
        Objects.requireNonNull(action, "action is null");

        if (mActionsContexts.containsKey(action)) {
            throw new IllegalStateException("action is running");
        }

        SynchronousActionContext context = new SynchronousActionContext(action, mClock);
        updateRequirementsWithAction(action, context.getConfiguration().getRequirements());
        context.startRun();
        mActionsContexts.put(action, context);
    }

    @Override
    public void cancel(Action action) {
        Objects.requireNonNull(action, "action is null");

        SynchronousActionContext context = mActionsContexts.remove(action);
        if (context != null) {
            cancelAction(context);
        } else {
            throw new IllegalStateException("action not running");
        }
    }

    @Override
    public boolean isRunning(Action action) {
        Objects.requireNonNull(action, "action is null");

        return mActionsContexts.containsKey(action);
    }

    @Override
    public Time getActionRunTime(Action action) {
        Objects.requireNonNull(action, "action is null");

        SynchronousActionContext context = mActionsContexts.get(action);
        if (context != null) {
            return context.getRunTime();
        } else {
            throw new IllegalStateException("action not running");
        }
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");

        for (Iterator<Map.Entry<Action, SynchronousActionContext>> entryIterator = mActionsContexts.entrySet().iterator();
             entryIterator.hasNext();) {
            Map.Entry<Action, SynchronousActionContext> entry = entryIterator.next();
            Action action = entry.getKey();
            SynchronousActionContext context = entry.getValue();

            if (predicate.test(action)) {
                cancelAction(context);
                entryIterator.remove();
            }
        }
    }

    @Override
    public void cancelAllActions() {
        for (Iterator<Map.Entry<Action, SynchronousActionContext>> entryIterator = mActionsContexts.entrySet().iterator();
             entryIterator.hasNext();) {
            Map.Entry<Action, SynchronousActionContext> entry = entryIterator.next();
            SynchronousActionContext context = entry.getValue();

            cancelAction(context);
            entryIterator.remove();
        }
    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {
        Objects.requireNonNull(subsystem, "subsystem is null");
        Objects.requireNonNull(action, "action is null");

        if (!action.getConfiguration().getRequirements().contains(subsystem)) {
            throw new IllegalArgumentException("missing requirement on subsystem");
        }

        Action old = mDefaultActions.put(subsystem, action);
        if (old != null) {
            SynchronousActionContext context = mActionsContexts.remove(old);
            if (context != null) {
                cancelAction(context);
            }
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

        startDefaultActions(mode);
        runActions(mode);
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

                SynchronousActionContext context = mActionsContexts.get(action);
                if (!context.getConfiguration().getRequirements().contains(subsystem)) {
                    mLogger.warn("Subsystem {} requirement removed from its default action {}. Removing action", subsystem, action);
                    mActionsContexts.remove(action);
                    mDefaultActions.remove(subsystem);
                }
            }
        }
    }

    private void runActions(SchedulerMode mode) {
        for (Iterator<Map.Entry<Action, SynchronousActionContext>> entryIterator = mActionsContexts.entrySet().iterator();
             entryIterator.hasNext();) {
            Map.Entry<Action, SynchronousActionContext> entry = entryIterator.next();
            SynchronousActionContext context = entry.getValue();

            if (mode.isDisabled() && !context.getConfiguration().shouldRunWhenDisabled()) {
                mLogger.debug("Mode {} is disabled and action {} is not approved. Canceling", mode, entry.getKey());
                cancelAction(context);
                entryIterator.remove();
            } else {
                try {
                    if (!context.run()) {
                        actionFinished(context);
                        entryIterator.remove();
                    }
                } catch (Throwable t) {
                    mLogger.error(String.format("Error while running an action %s", entry.getKey()), t);
                    cancelAction(context);
                    entryIterator.remove();
                }
            }
        }
    }

    private void cancelAction(SynchronousActionContext context) {
        if (context.isRunning()) {
            try {
                context.cancelAndFinish();
            } catch (Throwable t) {
                mLogger.error(String.format("Error while ending an action %s", context), t);
            }
        }

        actionFinished(context);
    }

    private void actionFinished(SynchronousActionContext context) {
        updateRequirementsActionFinished(context.getConfiguration().getRequirements());
    }

    private void updateRequirementsWithAction(Action action, Collection<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            Action old = mRunningOnRequirements.put(requirement, action);
            if (old != null) {
                SynchronousActionContext context = mActionsContexts.remove(old);
                if (context != null) {
                    mLogger.warn("Conflict on requirement {} by old {} and new {}. New receives priority", requirement, old, action);
                    cancelAction(context);
                }
            }
        }
    }

    private void updateRequirementsActionFinished(Collection<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            mRunningOnRequirements.remove(requirement);
        }
    }
}
