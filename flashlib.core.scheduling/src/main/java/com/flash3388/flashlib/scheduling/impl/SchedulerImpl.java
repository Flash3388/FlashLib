package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionConfigurationEditor;
import com.flash3388.flashlib.scheduling.ActionFlag;
import com.flash3388.flashlib.scheduling.ActionFuture;
import com.flash3388.flashlib.scheduling.ActionGroup;
import com.flash3388.flashlib.scheduling.ActionGroupType;
import com.flash3388.flashlib.scheduling.ActionHasPreferredException;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.ConfiguredAction;
import com.flash3388.flashlib.scheduling.ConfiguringFailedException;
import com.flash3388.flashlib.scheduling.ManualTrigger;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.Trigger;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerActivationAction;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerImpl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;


public class SchedulerImpl implements Scheduler {

    private static final Logger LOGGER = Logging.getLogger("Scheduler");

    private final FlashLibMainThread mMainThread;
    private final Clock mClock;
    private final StoredObject mRootObject;

    private final List<ActionContext> mPendingContexts;
    private final List<ActionContext> mRunningContexts;
    private final Map<Requirement, ActionContext> mRequirementsUsage;
    private final Map<Subsystem, ConfiguredAction> mDefaultActions;

    private boolean mCanModifyRunningActions;

    public SchedulerImpl(FlashLibMainThread mainThread,
                         Clock clock,
                         StoredObject rootObject,
                         List<ActionContext> pendingContexts,
                         List<ActionContext> runningContexts,
                         Map<Requirement, ActionContext> requirementsUsage,
                         Map<Subsystem, ConfiguredAction> defaultActions) {
        mMainThread = mainThread;
        mClock = clock;
        mRootObject = rootObject;
        mPendingContexts = pendingContexts;
        mRunningContexts = runningContexts;
        mRequirementsUsage = requirementsUsage;
        mDefaultActions = defaultActions;

        mCanModifyRunningActions = true;
    }

    public SchedulerImpl(FlashLibMainThread mainThread, Clock clock, StoredObject rootObject) {
        this(mainThread, clock,
                rootObject,
                new LinkedList<>(),
                new LinkedList<>(),
                new LinkedHashMap<>(),
                new LinkedHashMap<>());
    }

    @Override
    public ConfiguredAction newAction(ActionInterface action, ActionConfiguration configuration) {
        mMainThread.verifyCurrentThread();
        return new ConfiguredActionImpl(this, action, configuration);
    }

    @Override
    public Trigger newTrigger(BooleanSupplier condition) {
        mMainThread.verifyCurrentThread();

        TriggerImpl trigger = new TriggerImpl(this);
        start(new TriggerActivationAction(condition, trigger),
                new ActionConfigurationEditor()
                        .addRequirements(trigger)
                        .addFlags(ActionFlag.RUN_ON_DISABLED)
                        .save());

        return trigger;
    }

    @Override
    public ManualTrigger newManualTrigger() {
        mMainThread.verifyCurrentThread();
        return new TriggerImpl(this);
    }

    @Override
    public ActionGroup newActionGroup(ActionGroupType type, ActionConfiguration configuration) {
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

        return new ActionGroupImpl(this, configuration, LOGGER, policy);
    }

    @Override
    public ActionFuture start(ActionInterface action, ActionConfiguration configuration) {
        mMainThread.verifyCurrentThread();

        // configure action first
        configuration = configureAction(action, configuration);

        String id = UUID.randomUUID().toString();
        ActionState state = new ActionState(id, configuration);
        ActionContext context = new ActionContextImpl(action, state, mClock, LOGGER);
        ActionFuture future = new ActionFutureImpl(configuration, context, state, mClock, LOGGER);

        // try and acquire requirements and start
        if (!tryStartingAction(context)) {
            mPendingContexts.add(context);
            LOGGER.debug("Action {} pending", context);
        }

        return future;
    }

    @Override
    public void cancelActionsWithoutFlag(ActionFlag flag) {
        mMainThread.verifyCurrentThread();

        mPendingContexts.removeIf(context -> context.getConfiguration().getFlags().contains(flag));

        for (Iterator<ActionContext> iterator = mRunningContexts.iterator(); iterator.hasNext();) {
            ActionContext context = iterator.next();

            if (context.getConfiguration().getFlags().contains(flag)) {
                cancelAndEnd(context);
                iterator.remove();
            }
        }
    }

    @Override
    public void cancelAllActions() {
        mMainThread.verifyCurrentThread();

        mPendingContexts.clear();

        for (Iterator<ActionContext> iterator = mRunningContexts.iterator(); iterator.hasNext();) {
            ActionContext context = iterator.next();
            cancelAndEnd(context);
            iterator.remove();
        }
    }

    @Override
    public void setDefaultAction(Subsystem subsystem, ActionInterface action, ActionConfiguration configuration) {
        mMainThread.verifyCurrentThread();

        mDefaultActions.put(subsystem, newAction(action, configuration));
    }

    @Override
    public void run(SchedulerMode mode) {
        mMainThread.verifyCurrentThread();

        executeRunningActions(mode);

        //noinspection Java8CollectionRemoveIf
        for (Iterator<ActionContext> iterator = mPendingContexts.iterator(); iterator.hasNext();) {
            ActionContext context = iterator.next();

            if (tryStartingAction(context)) {
                iterator.remove();
            }
        }

        if (!mode.isDisabled()) {
            for (Map.Entry<Subsystem, ConfiguredAction> entry : mDefaultActions.entrySet()) {
                if (canStartDefaultAction(entry.getValue())) {
                    entry.getValue().start();
                }
            }
        }
    }

    private ActionConfiguration configureAction(ActionInterface action, ActionConfiguration configuration) {
        try {
            LOGGER.debug("Configuring new action class={}", action.getClass().getSimpleName());

            ActionConfigurationEditor editor = new ActionConfigurationEditor(configuration);
            action.configure(editor);
            return editor.save();
        } catch (Throwable t) {
            LOGGER.warn("Error while configuring action", t);
            throw new ConfiguringFailedException(action, t);
        }
    }

    private void executeRunningActions(SchedulerMode mode) {
        // while in this method, calls to start and cancel methods cannot occur due
        // to modifications of the collections.
        // since this implementation is meant for single-thread, then such calls
        // can only occur from within other actions.
        mCanModifyRunningActions = false;
        try {
            for (Iterator<ActionContext> iterator = mRunningContexts.iterator(); iterator.hasNext();) {
                ActionContext context = iterator.next();

                if (mode.isDisabled() && !context.shouldRunInDisabled()) {
                    context.markCancelled();
                    LOGGER.warn("Action {} is not allowed to run in disabled. Cancelling", context);
                }

                context.execute();
                if (context.isFinished()) {
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

    private Set<ActionContext> getConflictingOnRequirements(ActionContext context) {
        Set<ActionContext> conflicts = new HashSet<>();
        for (Requirement requirement : context.getRequirements()) {
            ActionContext current = mRequirementsUsage.get(requirement);
            if (current != null) {
                if (current.isPreferred()) {
                    // cannot cancel it as it is the preferred one.
                    // will have to wait for it to finish

                    LOGGER.warn("Action {} has conflict with (PREFERRED) {} on {}. Not canceling old, must wait for it to finish.",
                            context, current, requirement);

                    throw new ActionHasPreferredException();
                }

                conflicts.add(current);
            }
        }

        return conflicts;
    }

    private void setOnRequirements(ActionContext context) {
        for (Requirement requirement : context.getRequirements()) {
            mRequirementsUsage.put(requirement, context);
        }
    }

    private void removeFromRequirements(ActionContext context) {
        for (Requirement requirement : context.getRequirements()) {
            mRequirementsUsage.remove(requirement);
        }
    }

    private boolean tryStartingAction(ActionContext context) {
        if (!mCanModifyRunningActions) {
            LOGGER.debug("Cannot modify running actions");
            return false;
        }

        try {
            Set<ActionContext> conflicts = getConflictingOnRequirements(context);
            conflicts.forEach((conflict)-> {
                cancelAndEnd(conflict);
                mRunningContexts.remove(conflict);

                LOGGER.warn("Action {} has conflict with {}. Canceling old.",
                        context, conflict);
            });

            // no conflicts, let's start

            context.markStarted();
            setOnRequirements(context);
            mRunningContexts.add(context);

            LOGGER.debug("Action {} started running", context);

            return true;
        } catch (ActionHasPreferredException e) {
            return false;
        }
    }

    private boolean canStartDefaultAction(ConfiguredAction action) {
        for(Requirement requirement : action.getConfiguration().getRequirements()) {
            if (mRequirementsUsage.containsKey(requirement)) {
                return false;
            }
        }

        for (ActionContext context : mPendingContexts) {
            if (!Collections.disjoint(context.getRequirements(), action.getConfiguration().getRequirements())) {
                return false;
            }
        }

        return true;
    }

    private void cancelAndEnd(ActionContext context) {
        context.markCancelled();
        context.execute();
        removeFromRequirements(context);

        LOGGER.debug("Action {} finished", context);
    }
}
