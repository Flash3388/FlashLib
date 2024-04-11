package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.ActionGroupType;
import com.flash3388.flashlib.scheduling.ActionRejectedException;
import com.flash3388.flashlib.scheduling.DefaultActionRegistration;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.ExecutionState;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.ScheduledAction;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.scheduling.actions.ActionFlag;
import com.flash3388.flashlib.scheduling.actions.ActionGroup;
import com.flash3388.flashlib.scheduling.impl.triggers.ConditionBasedTrigger;
import com.flash3388.flashlib.scheduling.impl.triggers.GenericTrigger;
import com.flash3388.flashlib.scheduling.impl.triggers.ManualBasedTrigger;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerActionControllerImpl;
import com.flash3388.flashlib.scheduling.triggers.ManualTrigger;
import com.flash3388.flashlib.scheduling.triggers.Trigger;
import com.flash3388.flashlib.time.Clock;
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
    private final Map<Action, ExecutionContext> mPendingActions;
    private final Map<Action, ExecutionContext> mRunningActions;
    private final Collection<Action> mActionsToRemove;
    private final Collection<GenericTrigger> mTriggers;
    private final Map<Requirement, Action> mRequirementsUsage;
    private final Map<Subsystem, RegisteredDefaultAction> mDefaultActions;

    private final TriggerActionControllerImpl mTriggerActionController;
    private boolean mCanModifyRunningActions;
    private long mActionIdNext;

    SingleThreadedScheduler(Clock clock,
                            FlashLibMainThread mainThread,
                            StoredObject rootObject,
                            Map<Action, ExecutionContext> pendingActions,
                            Map<Action, ExecutionContext> runningActions,
                            Collection<Action> actionsToRemove,
                            Collection<GenericTrigger> triggers,
                            Map<Requirement, Action> requirementsUsage,
                            Map<Subsystem, RegisteredDefaultAction> defaultActions) {
        mClock = clock;
        mMainThread = mainThread;
        mRootObject = rootObject;
        mPendingActions = pendingActions;
        mRunningActions = runningActions;
        mActionsToRemove = actionsToRemove;
        mTriggers = triggers;
        mRequirementsUsage = requirementsUsage;
        mDefaultActions = defaultActions;

        mTriggerActionController = new TriggerActionControllerImpl();
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
    public ScheduledAction start(Action action) {
        mMainThread.verifyCurrentThread();

        if (mPendingActions.containsKey(action) || mRunningActions.containsKey(action)) {
            throw new IllegalArgumentException("Action already started");
        }

        return startAction(action);
    }

    @Override
    public void cancel(Action action) {
        mMainThread.verifyCurrentThread();

        ExecutionContext context = mPendingActions.remove(action);
        if (context != null) {
            context.interrupt();
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

        ExecutionContext context = mRunningActions.get(action);
        if (context != null) {
            return context.getState();
        }

        return ExecutionState.notRunning();
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
        mMainThread.verifyCurrentThread();

        if (!mCanModifyRunningActions) {
            throw new IllegalStateException("cannot cancel actions in bulk while action modification is disabled");
        }

        for (Iterator<ExecutionContext> iterator = mPendingActions.values().iterator(); iterator.hasNext();) {
            ExecutionContext context = iterator.next();

            if (predicate.test(context.getAction())) {
                iterator.remove();
                context.interrupt();
                LOGGER.debug("Action {} removed (from pending)", context);
            }
        }

        mCanModifyRunningActions = false;
        try {
            for (Iterator<ExecutionContext> iterator = mRunningActions.values().iterator(); iterator.hasNext();) {
                ExecutionContext context = iterator.next();

                if (predicate.test(context.getAction())) {
                    iterator.remove();
                    cancelAndEnd(context);
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

        for (Iterator<ExecutionContext> iterator = mPendingActions.values().iterator(); iterator.hasNext();) {
            ExecutionContext context = iterator.next();

            iterator.remove();
            context.interrupt();
            LOGGER.debug("Action {} removed (from pending)", context);
        }

        // since we iterate over the running actions, we should allow modifications to them.
        mCanModifyRunningActions = false;
        try {
            for (Iterator<ExecutionContext> iterator = mRunningActions.values().iterator(); iterator.hasNext();) {
                ExecutionContext context = iterator.next();
                iterator.remove();
                cancelAndEnd(context);
            }
        } finally {
            mCanModifyRunningActions = true;
        }
    }

    @Override
    public DefaultActionRegistration setDefaultAction(Subsystem subsystem, Action action) {
        mMainThread.verifyCurrentThread();

        ActionConfiguration configuration = new ActionConfiguration(action.getConfiguration());
        if (!configuration.getRequirements().contains(subsystem)) {
            throw new IllegalArgumentException("action should have subsystem has requirement");
        }

        long id = ++mActionIdNext;
        StoredObject object = mRootObject.getChild(String.valueOf(id));
        ObsrActionContext obsrActionContext = new ObsrActionContext(
                object,
                id,
                action,
                configuration,
                false);

        RegisteredDefaultAction registeredAction = new RegisteredDefaultAction(id, action, configuration, obsrActionContext, subsystem);
        RegisteredDefaultAction lastRegistered = mDefaultActions.put(subsystem, registeredAction);
        if (lastRegistered != null) {
            LOGGER.warn("Default action {} for subsystem {} was replaced with {}",
                    lastRegistered.getAction(), subsystem, action);
            lastRegistered.removed();
        }

        return new DefaultActionRegistrationImpl(registeredAction);
    }

    @Override
    public Optional<DefaultActionRegistration> getDefaultActionRegistration(Subsystem subsystem) {
        mMainThread.verifyCurrentThread();

        RegisteredDefaultAction registered = mDefaultActions.get(subsystem);
        if (registered == null) {
            return Optional.empty();
        }

        return Optional.of(new DefaultActionRegistrationImpl(registered));
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
            ExecutionContext context = mRunningActions.remove(action);

            iterator.remove();

            if (context != null) {
                cancelAndEnd(context);
            }
        }

        for (Iterator<ExecutionContext> iterator = mPendingActions.values().iterator(); iterator.hasNext();) {
            ExecutionContext context = iterator.next();

            // the action may have being marked for cancellation from an outside source
            ExecutionState state = context.getState();
            if (state.isFinished()) {
                iterator.remove();
                LOGGER.debug("Action {} removed (from pending)", context);
                continue;
            }

            if (tryStartingAction(context)) {
                iterator.remove();
            }
        }

        if (!mode.isDisabled()) {
            for (Map.Entry<Subsystem, RegisteredDefaultAction> entry : mDefaultActions.entrySet()) {
                RegisteredDefaultAction action = entry.getValue();
                if (canStartDefaultAction(action.getConfiguration())) {
                    try {
                        // we shouldn't allow pending here because canStartDefaultAction verifies that we won't
                        // be pending, and we shouldn't start default actions if they would be pending.
                        ScheduledAction scheduledAction = startAction(
                                action.getId(),
                                action.getAction(),
                                action.getConfiguration(),
                                action.getObsrActionContext(),
                                false);
                        action.updateStarted(scheduledAction);
                    } catch (ActionRejectedException e) {
                        // this should never occur, but if it did, then it's likely a bug
                        LOGGER.error("Default action was rejected during start attempt {}", action.getAction(), e);
                    }
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

    private ScheduledAction startAction(long id,
                                        Action action,
                                        ActionConfiguration configuration,
                                        ObsrActionContext obsrActionContext,
                                        boolean allowPending) {
        ExecutionContext context = new ExecutionContextImpl(
                id,
                action,
                configuration,
                obsrActionContext,
                mClock,
                LOGGER);
        ScheduledAction future = new ScheduledActionImpl(context, obsrActionContext);


        if (!tryStartingAction(context)) {
            if (allowPending) {
                mPendingActions.put(action, context);
                LOGGER.debug("Action {} pending", context);
            } else {
                throw new ActionRejectedException(action, "cannot acquire requirements for action");
            }
        }

        return future;
    }

    private ScheduledAction startAction(Action action) {
        long id = ++mActionIdNext;
        ActionConfiguration configuration = new ActionConfiguration(action.getConfiguration());
        StoredObject object = mRootObject.getChild(String.valueOf(id));
        ObsrActionContext obsrActionContext = new ObsrActionContext(
                object,
                id,
                action,
                configuration,
                true);

        return startAction(id, action, configuration, obsrActionContext, true);
    }

    private void executeTriggers() {
        mCanModifyRunningActions = false;
        try {
            mTriggerActionController.clear();
            for (GenericTrigger trigger : mTriggers) {
                trigger.update(mTriggerActionController);
            }

            // calls to isRunning prior to cancel or start are valid here
            // as this scheduler is singlethreaded and thus this info is not
            // updated behind our backs.

            for (Action action : mTriggerActionController.getActionsToStopIfRunning()) {
                if (isRunning(action)) {
                    LOGGER.debug("A trigger has requested to cancel action {}, which is running, cancelling", action);
                    cancel(action);
                }
            }

            for (Action action : mTriggerActionController.getActionsToToggle()) {
                if (!isRunning(action)) {
                    LOGGER.debug("A trigger has requested to toggle action {}, which is not running, starting", action);
                    start(action);
                } else {
                    LOGGER.debug("A trigger has requested to toggle action {}, which is running, cancelling", action);
                    cancel(action);
                }
            }

            for (Action action : mTriggerActionController.getActionsToStartIfNotRunning()) {
                if (!isRunning(action)) {
                    LOGGER.debug("A trigger has requested to start action {}, which is not running, starting", action);
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
            for (Iterator<ExecutionContext> iterator = mRunningActions.values().iterator(); iterator.hasNext();) {
                ExecutionContext context = iterator.next();
                ExecutionContext.ExecutionResult result = context.execute(mode);

                if (result == ExecutionContext.ExecutionResult.FINISHED) {
                    iterator.remove();

                    // finished execution
                    onActionEnd(context);
                }
            }
        } finally {
            mCanModifyRunningActions = true;
        }
    }

    private Set<ExecutionContext> getConflictingOnRequirements(ExecutionContext context) {
        Set<ExecutionContext> conflicts = new HashSet<>();
        for (Requirement requirement : context.getConfiguration().getRequirements()) {
            Action current = mRequirementsUsage.get(requirement);
            if (current != null) {
                ExecutionContext currentContext = mRunningActions.get(current);
                if (currentContext.getConfiguration().isPreferred()) {
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

    private void setOnRequirements(ExecutionContext context) {
        for (Requirement requirement : context.getConfiguration().getRequirements()) {
            mRequirementsUsage.put(requirement, context.getAction());
        }
    }

    private void removeFromRequirements(ExecutionContext context) {
        for (Requirement requirement : context.getConfiguration().getRequirements()) {
            mRequirementsUsage.remove(requirement);
        }
    }

    private boolean tryStartingAction(ExecutionContext context) {
        if (!mCanModifyRunningActions) {
            LOGGER.debug("Cannot modify running actions");
            return false;
        }

        Set<ExecutionContext> conflicts = getConflictingOnRequirements(context);
        if (conflicts == null) {
            LOGGER.debug("New action is unable to supersede running conflicts");
            return false;
        }

        conflicts.forEach((conflict)-> {
            LOGGER.warn("Action {} has conflict with {}. Canceling old.",
                    context, conflict);

            cancelAndEnd(conflict);
            mRunningActions.remove(conflict.getAction());
        });

        // no conflicts, let's start

        setOnRequirements(context);
        mRunningActions.put(context.getAction(), context);
        context.start();

        return true;
    }

    private boolean canStartDefaultAction(ActionConfiguration configuration) {
        for (Requirement requirement : configuration.getRequirements()) {
            if (mRequirementsUsage.containsKey(requirement)) {
                return false;
            }
        }

        for (ExecutionContext context : mPendingActions.values()) {
            if (!Collections.disjoint(context.getConfiguration().getRequirements(), configuration.getRequirements())) {
                return false;
            }
        }

        return true;
    }

    private void cancelAndEnd(ExecutionContext context) {
        context.interrupt();
        onActionEnd(context);
    }

    private void onActionEnd(ExecutionContext context) {
        removeFromRequirements(context);
    }
}
