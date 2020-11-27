package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.concurrent.SeparateThreadExecutor;
import com.flash3388.flashlib.util.concurrent.ServiceIntervalExecutor;
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
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

/**
 * A scheduler implementation that runs actions in a thread separate from the main robot thread. Actions are not
 * touched in the robot main thread at all. This causes to some delays in updates to certain robot status. For example,
 * when calling {@link #start(Action)} the action may not actually start for a few milliseconds (until the scheduling thread
 * runs). The same is true about {@link #cancel(Action)} which won't cancel the action until the scheduling thread gets to it. This leads
 * to possible delays in several status methods, like {@link #isRunning(Action)}:
 * <pre>
 *     action.cancel()
 *     assert !action.isRunning();
 * </pre>
 * <p>
 *     This will cause an {@link AssertionError} as the action hasn't actually finished yet. In addition,
 *     subsequent calls to {@link #cancel(Action)} may also work even though they shouldn't. Such a case won't cause 
 *     an error in the scheduler, but users should beware. 
 * </p>
 * <pre>
 *     action.cancel();
 *     action.start();
 * </pre>
 * <p>
 *     The above code snippet is possible although may cause weird states, as technically the previous action is still
 *     running and has not necessarily been canceled yet, so first the old action needs to be canceled. Only then
 *     will the new action be posted to the scheduling thread.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public class SeparateThreadScheduler implements Scheduler {

    private final Clock mClock;
    private final Logger mLogger;
    private final StsUserRequests mUserRequests;
    private final StsSchedulerStatus mSchedulerStatus;

    private final Set<Action> mRunningActions;
    private final Map<Requirement, Action> mRunningOnRequirements;
    private final Map<Subsystem, Action> mDefaultActions;
    
    private final Collection<Action> mMarkedForCancel;
    private final Collection<Action> mCancelReplaceActions;

    SeparateThreadScheduler(Executor executor, Clock clock, Logger logger,
                            StsUserRequests userRequests, StsSchedulerStatus schedulerStatus,
                            Set<Action> runningActions, Map<Requirement, Action> runningOnRequirements,
                            Map<Subsystem, Action> defaultActions) {
        mClock = clock;
        mLogger = logger;
        mUserRequests = userRequests;
        mSchedulerStatus = schedulerStatus;
        mRunningActions = runningActions;
        mRunningOnRequirements = runningOnRequirements;
        mDefaultActions = defaultActions;

        mMarkedForCancel = new HashSet<>();
        mCancelReplaceActions = new HashSet<>();

        executor.execute(new StsSchedulingTask(mUserRequests, mSchedulerStatus, mClock, mLogger));
    }

    public SeparateThreadScheduler(Executor executor, Clock clock, Logger logger) {
        this(executor, clock, logger, new StsUserRequests(), new StsSchedulerStatus(),
                new HashSet<>(5), new HashMap<>(5), 
                new HashMap<>(2));
    }

    public SeparateThreadScheduler(Time runInterval, ExecutorService executorService, Clock clock, Logger logger) {
        this(new ServiceIntervalExecutor(executorService, runInterval), clock, logger);
    }

    public SeparateThreadScheduler(Time runInterval, Clock clock, Logger logger) {
        this(new SeparateThreadExecutor(runInterval, "scheduler-thread"), clock, logger);
    }

    @Override
    public void start(Action action) {
        Objects.requireNonNull(action, "action is null");

        if (mMarkedForCancel.contains(action)) {
            mCancelReplaceActions.add(action);
        } else {
            if (mRunningActions.contains(action)) {
                throw new IllegalStateException("action is running");
            }

            mRunningActions.add(action);

            Collection<Requirement> requirements = action.getConfiguration().getRequirements();
            updateRequirementsWithAction(action, requirements);

            mUserRequests.actionToStart(action);
        }
    }

    @Override
    public void cancel(Action action) {
        Objects.requireNonNull(action, "action is null");

        if (mMarkedForCancel.contains(action)) {
            if (mCancelReplaceActions.contains(action)) {
                mCancelReplaceActions.remove(action);
            } else {
                return;
            }
        }

        if (!mRunningActions.contains(action)) {
            throw new IllegalStateException("action not running");
        }

        cancelAction(action);
    }

    @Override
    public boolean isRunning(Action action) {
        Objects.requireNonNull(action, "action is null");

        return mRunningActions.contains(action);
    }

    @Override
    public Time getActionRunTime(Action action) {
        Objects.requireNonNull(action, "action is null");

        if (!mRunningActions.contains(action)) {
            throw new IllegalStateException("action not running");
        }

        Time startTime = mSchedulerStatus.getStartTime(action);
        return mClock.currentTime().sub(startTime);
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");

        for (Action action : mRunningActions) {
            if (predicate.test(action)) {
                cancel(action);
            }
        }
    }

    @Override
    public void cancelAllActions() {
        mCancelReplaceActions.clear();
        cancelActions(mRunningActions);
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
            cancel(old);
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

        mUserRequests.updateCurrentMode(mode);

        Collection<Action> actionsFinished = mSchedulerStatus.getAndClearActionsFinished();
        if (!actionsFinished.isEmpty()) {
            mLogger.debug("Scheduler finished actions {}", actionsFinished);
        }

        mRunningActions.removeAll(actionsFinished);
        mMarkedForCancel.removeAll(actionsFinished);
        
        for (Action action : actionsFinished) {
            updateRequirementsActionFinished(action.getConfiguration().getRequirements());
        }

        for (Iterator<Action> iterator = mCancelReplaceActions.iterator(); iterator.hasNext();) {
            Action action = iterator.next();
            if (!mRunningActions.contains(action)) {
                start(action);
            }
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
                cancelAction(action);
            }
        }
    }

    private void updateRequirementsWithAction(Action action, Collection<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            Action old = mRunningOnRequirements.put(requirement, action);
            if (old != null && mRunningActions.contains(old)) {
                mLogger.warn("Conflict on requirement {} by old {} and new {}. New receives priority", requirement, old, action);
                cancelAction(action);
            }
        }
    }

    private void updateRequirementsActionFinished(Collection<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            mRunningOnRequirements.remove(requirement);
        }
    }
    
    private void cancelAction(Action action) {
        mUserRequests.actionToCancel(action);
    }

    private void cancelActions(Collection<Action> actions) {
        mUserRequests.actionsToCancel(actions);
    }
}
