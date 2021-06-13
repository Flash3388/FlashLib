package com.flash3388.flashlib.scheduling.mt;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionContext;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;

public class MtActionsControl {

    private final MtRequirementsControl mRequirementsControl;
    private final Clock mClock;
    private final Logger mLogger;

    private final Map<Action, ActionContext> mRunningActions;
    private final Queue<ActionContext> mRunningActionsContexts;
    private final Queue<ActionContext> mFinishedActionsContexts;

    MtActionsControl(MtRequirementsControl requirementsControl, Clock clock, Logger logger,
                     Map<Action, ActionContext> runningActions,
                     Queue<ActionContext> runningActionsContexts,
                     Queue<ActionContext> finishedActionsContexts) {
        mRequirementsControl = requirementsControl;
        mClock = clock;
        mLogger = logger;

        mRunningActions = runningActions;
        mRunningActionsContexts = runningActionsContexts;
        mFinishedActionsContexts = finishedActionsContexts;
    }

    public MtActionsControl(MtRequirementsControl requirementsControl, Clock clock, Logger logger) {
        this(requirementsControl, clock, logger,
                new HashMap<>(5),
                new LinkedBlockingQueue<>(5),
                new LinkedBlockingQueue<>(5));
    }

    public void startActions(Collection<Action> actions) {
        for (Action action : actions) {
            onActionStart(action);
        }
    }

    public void cancelAction(Action action) {
        ActionContext context = mRunningActions.get(action);
        if (context != null) {
            context.markCanceled();
            mRunningActionsContexts.remove(context);
            mFinishedActionsContexts.add(context);
        } else {
            throw new IllegalStateException("action not running");
        }
    }

    public void cancelActionsIf(Predicate<? super Action> predicate) {
        for (Action action : mRunningActions.keySet()) {
            if (predicate.test(action)) {
                cancelAction(action);
            }
        }
    }

    public void cancelAllActions() {
        for (Action action : mRunningActions.keySet()) {
            cancelAction(action);
        }
    }

    public boolean isActionRunning(Action action) {
        return mRunningActions.containsKey(action);
    }

    public Time getActionRunTime(Action action) {
        ActionContext context = mRunningActions.get(action);
        if (context != null) {
            return context.getRunTime();
        } else {
            throw new IllegalStateException("action not running");
        }
    }

    public ActionContext pollRunningAction() {
        return mRunningActionsContexts.poll();
    }

    public void pushRunningAction(ActionContext context) {
        mRunningActionsContexts.add(context);
    }

    public void pushFinishedAction(ActionContext context) {
        mFinishedActionsContexts.add(context);
    }

    public void processFinishedActions() {
        while (!mFinishedActionsContexts.isEmpty()) {
            ActionContext context = mFinishedActionsContexts.poll();
            if (context == null) {
                break;
            }

            if (mRunningActions.remove(context.getAction()) != null) {
                onActionFinished(context, true);
            }
        }
    }

    private void onActionStart(Action action) {
        if (mRunningActions.containsKey(action)) {
            mLogger.debug("Attempted to start action {} when already running", action);
            return;
        }

        Set<Action> conflictingActions = mRequirementsControl.updateRequirementsWithNewRunningAction(action);
        conflictingActions.forEach((conflictingAction)-> {
            ActionContext context = mRunningActions.remove(conflictingAction);
            if (context != null) {
                context.markCanceled();
                onActionFinished(context, false);
            }
        });

        ActionContext context = new ActionContext(action, mClock);
        context.prepareForRun();

        mRunningActions.put(action, context);
        mRunningActionsContexts.add(context);

        mLogger.debug("Started action {}", action);
    }

    private void onActionFinished(ActionContext context, boolean updateRequirements) {
        Action action = context.getAction();

        context.runFinished();
        if (updateRequirements) {
            mRequirementsControl.updateRequirementsNoCurrentAction(action);
        }

        mLogger.debug("Finished action {}", action);
    }
}
