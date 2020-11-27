package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.SynchronousActionContext;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class StsSchedulingTask implements Runnable {

    private final StsUserRequests mUserRequests;
    private final StsSchedulerStatus mSchedulerStatus;
    private final Clock mClock;
    private final Logger mLogger;

    private final Map<Action, SynchronousActionContext> mActionsContexts;

    private final Collection<Action> mFinishedCached;
    private final Map<Action, Time> mActionStartTimesCached;

    public StsSchedulingTask(StsUserRequests userRequests, StsSchedulerStatus schedulerStatus, Clock clock, Logger logger) {
        mUserRequests = userRequests;
        mSchedulerStatus = schedulerStatus;
        mClock = clock;
        mLogger = logger;

        mActionsContexts = new HashMap<>();

        mFinishedCached = new ArrayList<>(3);
        mActionStartTimesCached = new HashMap<>(5);
    }

    @Override
    public void run() {
        startNewActions();
        cancelRequestedActions();
        runActions();
    }

    private void startNewActions() {
        Collection<Action> actionsToStart = mUserRequests.getAndClearActionsToStart();
        if (!actionsToStart.isEmpty()) {
            mLogger.debug("Scheduler starting actions {}", actionsToStart);
        }

        mActionStartTimesCached.clear();
        boolean disabled = mUserRequests.isCurrentModeDisable();

        for (Action action : actionsToStart) {
            if (disabled && !action.getConfiguration().shouldRunWhenDisabled()) {
                mLogger.debug("Mode disabled=true, not starting action {}", action);
                continue;
            }

            SynchronousActionContext context = new SynchronousActionContext(action, mClock);
            context.startRun();
            mActionsContexts.put(action, context);
            mActionStartTimesCached.put(action, context.getStartTime());
        }

        if (!mActionStartTimesCached.isEmpty()) {
            mSchedulerStatus.actionsStarted(mActionStartTimesCached);
        }
    }

    private void cancelRequestedActions() {
        Collection<Action> actionsToCancel = mUserRequests.getAndClearActionsToCancel();
        if (!actionsToCancel.isEmpty()) {
            mLogger.debug("Scheduler canceling actions {}", actionsToCancel);
        }

        for (Action action : actionsToCancel) {
            SynchronousActionContext context = mActionsContexts.remove(action);
            if (context != null) {
                cancelAction(context);
            }
        }
    }

    private void runActions() {
        mFinishedCached.clear();

        for (Iterator<Map.Entry<Action, SynchronousActionContext>> entryIterator = mActionsContexts.entrySet().iterator();
             entryIterator.hasNext();) {
            Map.Entry<Action, SynchronousActionContext> entry = entryIterator.next();
            SynchronousActionContext context = entry.getValue();
            // TODO: LOCKING
            try {
                if (!context.run()) {
                    mFinishedCached.add(entry.getKey());
                    entryIterator.remove();
                }
            } catch (Throwable t) {
                mLogger.error(String.format("Error while running an action %s", context), t);
                cancelAction(context);
                mFinishedCached.add(entry.getKey());
                entryIterator.remove();
            }
        }

        if (!mFinishedCached.isEmpty()) {
            mSchedulerStatus.actionsFinished(mFinishedCached);
        }
    }

    private void cancelAction(SynchronousActionContext context) {
        // TODO: LOCKING
        if (context.isRunning()) {
            try {
                context.cancelAndFinish();
            } catch (Throwable t) {
                mLogger.error(String.format("Error while ending an action %s", context), t);
            }
        }
    }

    // for use in tests

    Map<Action, SynchronousActionContext> getActionsContexts() {
        return mActionsContexts;
    }

    Collection<Action> getFinishedCached() {
        return mFinishedCached;
    }

    Map<Action, Time> getActionStartTimesCached() {
        return mActionStartTimesCached;
    }
}
