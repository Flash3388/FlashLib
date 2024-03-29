package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.ActionGroupType;
import com.flash3388.flashlib.scheduling.ExecutionState;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionFlag;
import com.flash3388.flashlib.scheduling.actions.ActionGroup;
import com.flash3388.flashlib.scheduling.impl.triggers.ConditionBasedTrigger;
import com.flash3388.flashlib.scheduling.impl.triggers.GenericTrigger;
import com.flash3388.flashlib.scheduling.impl.triggers.ManualBasedTrigger;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerActionControllerImpl;
import com.flash3388.flashlib.scheduling.triggers.ManualTrigger;
import com.flash3388.flashlib.scheduling.triggers.Trigger;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.logging.Logging;
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

public class SingleThreadedScheduler implements Scheduler {

    private static final Logger LOGGER = Logging.getLogger("Scheduler");
    
    private final Clock mClock;
    private final FlashLibMainThread mMainThread;

    private final StoredObject mRootObject;
    private final Map<Action, RunningActionContext> mPendingActions;
    private final Map<Action, RunningActionContext> mRunningActions;
    private final Collection<Action> mActionsToRemove;
    private final Collection<GenericTrigger> mTriggers;
    private final Map<Requirement, Action> mRequirementsUsage;
    private final Map<Subsystem, Action> mDefaultActions;

    private boolean mCanModifyRunningActions;
    private long mActionIdNext;

    SingleThreadedScheduler(Clock clock,
                            FlashLibMainThread mainThread,
                            StoredObject rootObject,
                            Map<Action, RunningActionContext> pendingActions,
                            Map<Action, RunningActionContext> runningActions,
                            Collection<Action> actionsToRemove,
                            Collection<GenericTrigger> triggers,
                            Map<Requirement, Action> requirementsUsage,
                            Map<Subsystem, Action> defaultActions) {
        mClock = clock;
        mMainThread = mainThread;
        mRootObject = rootObject;
        mPendingActions = pendingActions;
        mRunningActions = runningActions;
        mActionsToRemove = actionsToRemove;
        mTriggers = triggers;
        mRequirementsUsage = requirementsUsage;
        mDefaultActions = defaultActions;

        mCanModifyRunningActions = true;
        mActionIdNext = 0;
    }

    public SingleThreadedScheduler(Clock clock, StoredObject rootObject, FlashLibMainThread mainThread) {
        this(
                clock,
                mainThread, rootObject,
                new LinkedHashMap<>(5),
                new LinkedHashMap<>(10),
                new ArrayList<>(2),
                new ArrayList<>(15),
                new HashMap<>(10),
                new HashMap<>(5));
    }

    @Override
    public void start(Action action) {
        mMainThread.verifyCurrentThread();

        if (mPendingActions.containsKey(action) || mRunningActions.containsKey(action)) {
            throw new IllegalArgumentException("Action already started");
        }

        StoredObject object = mRootObject.getChild(String.valueOf(++mActionIdNext));
        RunningActionContext context = new RunningActionContext(action, object, mClock, LOGGER);

        if (!tryStartingAction(context)) {
            mPendingActions.put(action, context);
            LOGGER.debug("Action {} pending", context);
        }
    }

    @Override
    public void cancel(Action action) {
        mMainThread.verifyCurrentThread();

        RunningActionContext context = mPendingActions.remove(action);
        if (context != null) {
            LOGGER.debug("Action {} removed (from pending)", context);
            return;
        }

        if (mCanModifyRunningActions) {
            context = mRunningActions.remove(action);
            if (context != null) {
                cancelAndEnd(context);
                return;
            }
        } else if (mRunningActions.containsKey(action)) {
            mActionsToRemove.add(action);
            LOGGER.debug("Action placed for later removal");
            return;
        }

        throw new IllegalArgumentException("Action not running");
    }

    @Override
    public boolean isRunning(Action action) {
        mMainThread.verifyCurrentThread();

        return getExecutionStateOf(action).isRunning();
    }

    @Override
    public ExecutionState getExecutionStateOf(Action action) {
        mMainThread.verifyCurrentThread();

        if (mPendingActions.containsKey(action)) {
            return ExecutionState.pending();
        }

        RunningActionContext context = mRunningActions.get(action);
        if (context != null) {
            Time runTime = context.getRunTime();
            Time timeLeft = context.getTimeLeft();
            return ExecutionState.executing(runTime, timeLeft);
        }

        return ExecutionState.notRunning();
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
        mMainThread.verifyCurrentThread();

        if (!mCanModifyRunningActions) {
            throw new IllegalStateException("cannot cancel actions in bulk while action modification is disabled");
        }

        mPendingActions.values().removeIf(context -> predicate.test(context.getAction()));

        mCanModifyRunningActions = false;
        try {
            for (Iterator<RunningActionContext> iterator = mRunningActions.values().iterator(); iterator.hasNext();) {
                RunningActionContext context = iterator.next();

                if (predicate.test(context.getAction())) {
                    cancelAndEnd(context);
                    iterator.remove();
                }
            }
        } finally {
            mCanModifyRunningActions = true;
        }
    }

    @Override
    public void cancelActionsIfWithoutFlag(ActionFlag flag) {
        cancelActionsIf((action)-> !action.getConfiguration().hasFlags(flag));
    }

    @Override
    public void cancelAllActions() {
        mMainThread.verifyCurrentThread();

        if (!mCanModifyRunningActions) {
            throw new IllegalStateException("cannot cancel actions in bulk while action modification is disabled");
        }

        mPendingActions.clear();

        mCanModifyRunningActions = false;
        try {
            for (Iterator<RunningActionContext> iterator = mRunningActions.values().iterator(); iterator.hasNext();) {
                RunningActionContext context = iterator.next();
                cancelAndEnd(context);
                iterator.remove();
            }
        } finally {
            mCanModifyRunningActions = true;
        }
    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {
        mMainThread.verifyCurrentThread();

        if (!action.getConfiguration().getRequirements().contains(subsystem)) {
            throw new IllegalArgumentException("action should have subsystem has requirement");
        }

        mDefaultActions.put(subsystem, action);
    }

    @Override
    public Optional<Action> getActionRunningOnRequirement(Requirement requirement) {
        mMainThread.verifyCurrentThread();

        return Optional.ofNullable(mRequirementsUsage.get(requirement));
    }

    @Override
    public void run(SchedulerMode mode) {
        mMainThread.verifyCurrentThread();

        executeTriggers();
        executeRunningActions(mode);

        for (Iterator<Action> iterator = mActionsToRemove.iterator(); iterator.hasNext();) {
            Action action = iterator.next();
            RunningActionContext context = mRunningActions.remove(action);
            if (context != null) {
                cancelAndEnd(context);
            }

            iterator.remove();
        }

        //noinspection Java8CollectionRemoveIf
        for (Iterator<RunningActionContext> iterator = mPendingActions.values().iterator(); iterator.hasNext();) {
            RunningActionContext context = iterator.next();

            if (tryStartingAction(context)) {
                iterator.remove();
            }
        }

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
        mMainThread.verifyCurrentThread();

        ConditionBasedTrigger trigger = new ConditionBasedTrigger(condition);
        mTriggers.add(trigger);

        return trigger;
    }

    @Override
    public ManualTrigger newManualTrigger() {
        mMainThread.verifyCurrentThread();

        ManualBasedTrigger trigger = new ManualBasedTrigger();
        mTriggers.add(trigger);

        return trigger;
    }

    @Override
    public ActionGroup newActionGroup(ActionGroupType type) {
        mMainThread.verifyCurrentThread();

        GroupPolicy policy;
        switch (type) {
            case SEQUENTIAL:
                policy = GroupPolicy.sequential();
                break;
            case PARALLEL:
                policy = GroupPolicy.parallel();
                break;
            case PARALLEL_RACE:
                policy = GroupPolicy.parallelRace();
                break;
            default:
                throw new IllegalArgumentException("unsupported group type");
        }

        return new ActionGroupImpl(this, LOGGER, policy);
    }

    private void executeTriggers() {
        // while in this method, calls to start and cancel methods cannot occur due
        // to modifications of the collections.
        // since this implementation is meant for single-thread, then such calls
        // can only occur from within other actions.
        mCanModifyRunningActions = false;
        try {
            TriggerActionControllerImpl controller = new TriggerActionControllerImpl();
            for (GenericTrigger trigger : mTriggers) {
                trigger.update(controller);
            }

            // calls to isRunning prior to cancel or start are valid here
            // as this scheduler is singlethreaded and thus this info is not
            // updated behind our backs.

            for (Action action : controller.getActionsToStopIfRunning()) {
                if (isRunning(action)) {
                    cancel(action);
                }
            }

            for (Action action : controller.getActionsToToggle()) {
                if (!isRunning(action)) {
                    start(action);
                } else {
                    cancel(action);
                }
            }

            for (Action action : controller.getActionsToStartIfRunning()) {
                if (!isRunning(action)) {
                    start(action);
                }
            }
        } finally {
            mCanModifyRunningActions = true;
        }
    }

    private void executeRunningActions(SchedulerMode mode) {
        // while in this method, calls to start and cancel methods cannot occur due
        // to modifications of the collections.
        // since this implementation is meant for single-thread, then such calls
        // can only occur from within other actions.
        mCanModifyRunningActions = false;
        try {
            for (Iterator<RunningActionContext> iterator = mRunningActions.values().iterator(); iterator.hasNext();) {
                RunningActionContext context = iterator.next();

                if (mode.isDisabled() && !context.shouldRunInDisabled()) {
                    context.markForCancellation();
                    LOGGER.warn("Action {} is not allowed to run in disabled. Cancelling", context);
                }

                if (context.iterate()) {
                    // finished execution
                    removeFromRequirements(context);
                    iterator.remove();

                    LOGGER.debug("Action {} finished", context);
                }
            }
        } finally {
            mCanModifyRunningActions = true;
        }
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

                    LOGGER.warn("Action {} has conflict with (PREFERRED) {} on {}. Not canceling old, must wait for it to finish.",
                            context, current, requirement);

                    return null;
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
        if (!mCanModifyRunningActions) {
            LOGGER.debug("Cannot modify running actions");
            return false;
        }

        Set<RunningActionContext> conflicts = getConflictingOnRequirements(context);
        if (conflicts == null) {
            return false;
        }

        conflicts.forEach((conflict)-> {
            cancelAndEnd(conflict);
            mRunningActions.remove(conflict.getAction());

            LOGGER.warn("Action {} has conflict with {}. Canceling old.",
                    context, conflict);
        });

        // no conflicts, let's start

        context.markStarted();
        setOnRequirements(context);
        mRunningActions.put(context.getAction(), context);

        LOGGER.debug("Action {} started running", context);

        return true;
    }

    private boolean canStartDefaultAction(Action action) {
        for (Requirement requirement : action.getConfiguration().getRequirements()) {
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
        context.iterate();
        removeFromRequirements(context);

        LOGGER.debug("Action {} finished", context);
    }
}
