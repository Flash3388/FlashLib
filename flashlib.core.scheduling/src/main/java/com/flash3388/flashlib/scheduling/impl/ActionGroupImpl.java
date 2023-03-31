package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionConfigurationEditor;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ActionFlag;
import com.flash3388.flashlib.scheduling.ActionFuture;
import com.flash3388.flashlib.scheduling.ActionGroup;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.ConfiguringFailedException;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.Scheduler;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;

public class ActionGroupImpl implements ActionGroup, ActionInterface {

    private final WeakReference<Scheduler> mScheduler;
    private final ActionConfiguration mConfiguration;
    private final Logger mLogger;
    private final GroupPolicy mGroupPolicy;
    private final Collection<ActionInterface> mActions;

    private final Queue<ActionAndConfiguration> mActionsToExecute;
    private final Collection<ExecutionContext> mRunningActions;

    private Runnable mWhenInterrupted;
    private boolean mForceFinish;

    public ActionGroupImpl(Scheduler scheduler,
                           ActionConfiguration configuration,
                           Logger logger,
                           GroupPolicy groupPolicy,
                           Collection<ActionInterface> actions,
                           Queue<ActionAndConfiguration> actionsToExecute,
                           Collection<ExecutionContext> runningActions) {
        mScheduler = new WeakReference<>(scheduler);
        mConfiguration = configuration;
        mLogger = logger;
        mGroupPolicy = groupPolicy;
        mActions = actions;
        mActionsToExecute = actionsToExecute;
        mRunningActions = runningActions;
        mWhenInterrupted = null;
        mForceFinish = false;
    }

    public ActionGroupImpl(Scheduler scheduler,
                           ActionConfiguration configuration,
                           Logger logger,
                           GroupPolicy groupPolicy) {
        this(scheduler,
                configuration,
                logger,
                groupPolicy,
                new ArrayList<>(5),
                new ArrayDeque<>(5),
                new ArrayList<>(2));
    }

    @Override
    public ActionGroup add(ActionInterface action) {
        Objects.requireNonNull(action, "action is null");
        mActions.add(action);

        return this;
    }

    @Override
    public ActionGroup add(ActionInterface... actions) {
        Objects.requireNonNull(actions, "actions is null");
        return add(Arrays.asList(actions));
    }

    @Override
    public ActionGroup add(Collection<? extends ActionInterface> actions) {
        Objects.requireNonNull(actions, "actions is null");
        actions.forEach(this::add);

        return this;
    }

    @Override
    public ActionGroup whenInterrupted(Runnable runnable) {
        if (mWhenInterrupted != null) {
            mLogger.debug("whenInterrupted callback overridden for ActionGroup");
        }

        mWhenInterrupted = runnable;
        return this;
    }

    @Override
    public ActionFuture start() {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        return scheduler.start(this, mConfiguration);
    }

    @Override
    public void configure(ActionConfigurationEditor editor) {
        boolean runWhenDisabled = editor.getFlags().contains(ActionFlag.RUN_ON_DISABLED);

        for (ActionInterface action : mActions) {
            ActionConfiguration configuration = new ActionConfiguration();
            configuration = configureAction(action, configuration);

            if (!mGroupPolicy.shouldAllowRequirementCollisions()) {
                if (!Collections.disjoint(editor.getRequirements(),
                        configuration.getRequirements())) {
                    throw new IllegalArgumentException("Actions cannot share requirements");
                }
            }

            boolean actionRunWhenDisabled = configuration.getFlags().contains(ActionFlag.RUN_ON_DISABLED);
            if (mActions.isEmpty()) {
                runWhenDisabled = actionRunWhenDisabled;
            } else {
                runWhenDisabled &= actionRunWhenDisabled;
            }

            editor.addRequirements(configuration.getRequirements());
            mActionsToExecute.add(new ActionAndConfiguration(action, configuration));
        }

        if (runWhenDisabled) {
            editor.addFlags(ActionFlag.RUN_ON_DISABLED);
        } else {
            editor.removeFlags(ActionFlag.RUN_ON_DISABLED);
        }
    }

    @Override
    public void initialize(ActionControl control) {
        mForceFinish = false;

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
        boolean wasInterrupted = reason != FinishReason.FINISHED;
        if (wasInterrupted && mWhenInterrupted != null) {
            mWhenInterrupted.run();
        }

        if (wasInterrupted || mForceFinish) {
            for (ExecutionContext context : mRunningActions) {
                context.interrupt();
            }
        }

        mActionsToExecute.clear();
        mRunningActions.clear();
    }

    private boolean startNextAction(ActionControl control) {
        if (mActionsToExecute.isEmpty()) {
            return false;
        }

        ActionAndConfiguration action = mActionsToExecute.poll();
        ExecutionContext context = control.newExecutionContext(action.action, action.configuration);
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

    private ActionConfiguration configureAction(ActionInterface action, ActionConfiguration configuration) {
        try {
            mLogger.debug("Configuring new action class={}", action.getClass().getSimpleName());

            ActionConfigurationEditor editor = new ActionConfigurationEditor(configuration);
            action.configure(editor);
            return editor.save();
        } catch (Throwable t) {
            mLogger.warn("Error while configuring action", t);
            throw new ConfiguringFailedException(action, t);
        }
    }

    static class ActionAndConfiguration {
        final ActionInterface action;
        final ActionConfiguration configuration;

        private ActionAndConfiguration(ActionInterface action, ActionConfiguration configuration) {
            this.action = action;
            this.configuration = configuration;
        }
    }
}
