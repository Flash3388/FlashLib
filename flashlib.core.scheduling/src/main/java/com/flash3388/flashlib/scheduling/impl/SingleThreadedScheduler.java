package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.Action;
import com.flash3388.flashlib.scheduling.ActionBuilder;
import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionGroupType;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.ActionFlag;
import com.flash3388.flashlib.scheduling.ActionGroup;
import com.flash3388.flashlib.scheduling.triggers.Trigger;
import com.flash3388.flashlib.scheduling.triggers.TriggerActivationAction;
import com.flash3388.flashlib.scheduling.triggers.TriggerImpl;
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
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class SingleThreadedScheduler implements Scheduler {

    private static final Logger LOGGER = Logging.getLogger("Scheduler");
    
    private final Clock mClock;
    private final FlashLibMainThread mMainThread;

    private final StoredObject mRootObject;
    private final Map<ActionInterface, RunningActionContext> mPendingActions;
    private final Map<ActionInterface, RunningActionContext> mRunningActions;
    private final Collection<ActionInterface> mActionsToRemove;
    private final Map<Requirement, ActionInterface> mRequirementsUsage;
    private final Map<Subsystem, ActionInterface> mDefaultActions;

    private boolean mCanModifyRunningActions;

    SingleThreadedScheduler(Clock clock,
                            FlashLibMainThread mainThread,
                            StoredObject rootObject,
                            Map<ActionInterface, RunningActionContext> pendingActions,
                            Map<ActionInterface, RunningActionContext> runningActions,
                            Collection<ActionInterface> actionsToRemove,
                            Map<Requirement, ActionInterface> requirementsUsage,
                            Map<Subsystem, ActionInterface> defaultActions) {
        mClock = clock;
        mMainThread = mainThread;
        mRootObject = rootObject;
        mPendingActions = pendingActions;
        mRunningActions = runningActions;
        mActionsToRemove = actionsToRemove;
        mRequirementsUsage = requirementsUsage;
        mDefaultActions = defaultActions;
        mCanModifyRunningActions = true;
    }

    public SingleThreadedScheduler(Clock clock, StoredObject rootObject, FlashLibMainThread mainThread) {
        this(
                clock,
                mainThread, rootObject,
                new LinkedHashMap<>(5),
                new LinkedHashMap<>(10),
                new ArrayList<>(2),
                new HashMap<>(10),
                new HashMap<>(5));
    }

    @Override
    public Action createAction(ActionInterface action, ActionConfiguration configuration) {
        return buildAction(action, configuration).build();
    }

    @Override
    public Action createAction(ActionInterface action) {
        return buildAction(action).build();
    }

    @Override
    public ActionBuilder buildAction(ActionInterface action, ActionConfiguration configuration) {
        return new ActionBuilderImpl(action, configuration);
    }

    @Override
    public ActionBuilder buildAction(ActionInterface action) {
        ActionConfiguration configuration = new ActionConfiguration();
        return buildAction(action, configuration);
    }

    @Override
    public void start(ActionInterface action) {
        mMainThread.verifyCurrentThread();

        if (mPendingActions.containsKey(action) || mRunningActions.containsKey(action)) {
            throw new IllegalArgumentException("Action already started");
        }

        StoredObject object = mRootObject.getChild(UUID.randomUUID().toString());
        RunningActionContext context = new RunningActionContext(action, new ObsrActionContext(object), mClock, LOGGER);

        if (!tryStartingAction(context)) {
            mPendingActions.put(action, context);
            LOGGER.debug("Action {} pending", context);
        }
    }

    @Override
    public void cancel(ActionInterface action) {
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
    public boolean isRunning(ActionInterface action) {
        mMainThread.verifyCurrentThread();

        return mPendingActions.containsKey(action) || mRunningActions.containsKey(action);
    }

    @Override
    public Time getActionRunTime(ActionInterface action) {
        mMainThread.verifyCurrentThread();

        if (mPendingActions.containsKey(action)) {
            return Time.seconds(0);
        }

        RunningActionContext context = mRunningActions.get(action);
        if (context != null) {
            return context.getRunTime();
        }

        throw new IllegalArgumentException("Action not running");
    }

    @Override
    public void cancelActionsIf(Predicate<? super ActionInterface> predicate) {
        mMainThread.verifyCurrentThread();

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
    public void cancelActionsIfWithoutFlag(ActionFlag flag) {
        cancelActionsIf((action)-> !action.getConfiguration().hasFlags(flag));
    }

    @Override
    public void cancelAllActions() {
        mMainThread.verifyCurrentThread();

        mPendingActions.clear();

        for (Iterator<RunningActionContext> iterator = mRunningActions.values().iterator(); iterator.hasNext();) {
            RunningActionContext context = iterator.next();
            cancelAndEnd(context);
            iterator.remove();
        }
    }

    @Override
    public void setDefaultAction(Subsystem subsystem, ActionInterface action) {
        mMainThread.verifyCurrentThread();

        mDefaultActions.put(subsystem, action);
    }

    @Override
    public Optional<ActionInterface> getActionRunningOnRequirement(Requirement requirement) {
        mMainThread.verifyCurrentThread();

        return Optional.ofNullable(mRequirementsUsage.get(requirement));
    }

    @Override
    public void run(SchedulerMode mode) {
        mMainThread.verifyCurrentThread();

        executeRunningActions(mode);

        for (Iterator<ActionInterface> iterator = mActionsToRemove.iterator(); iterator.hasNext();) {
            ActionInterface action = iterator.next();
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
            for (Map.Entry<Subsystem, ActionInterface> entry : mDefaultActions.entrySet()) {
                if (canStartDefaultAction(entry.getValue())) {
                    start(entry.getValue());
                }
            }
        }
    }

    @Override
    public Trigger newTrigger(BooleanSupplier condition) {
        mMainThread.verifyCurrentThread();

        TriggerImpl trigger = new TriggerImpl();

        ActionInterface action = new TriggerActivationAction(this, condition, trigger)
                .requires(trigger);
        start(action);

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
            ActionInterface current = mRequirementsUsage.get(requirement);
            if (current != null) {
                RunningActionContext currentContext = mRunningActions.get(current);
                if (currentContext.isPreferred()) {
                    // cannot cancel it as it is the preferred one.
                    // will have to wait for it to finish

                    LOGGER.warn("Action {} has conflict with (PREFERRED) {} on {}. Not canceling old, must wait for it to finish.",
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
        if (!mCanModifyRunningActions) {
            LOGGER.debug("Cannot modify running actions");
            return false;
        }

        try {
            Set<RunningActionContext> conflicts = getConflictingOnRequirements(context);
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
        } catch (ActionHasPreferredException e) {
            return false;
        }
    }

    private boolean canStartDefaultAction(ActionInterface action) {
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
        context.iterate();
        removeFromRequirements(context);

        LOGGER.debug("Action {} finished", context);
    }
}
