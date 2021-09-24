package com.flash3388.flashlib.scheduling2;

import com.flash3388.flashlib.scheduling2.actions.Action;
import com.flash3388.flashlib.scheduling2.actions.ActionContext;
import com.flash3388.flashlib.scheduling2.actions.ActionExecutionBuilder;
import com.flash3388.flashlib.scheduling2.actions.ActionExecutionBuilderImpl;
import com.flash3388.flashlib.scheduling2.actions.ConfigurationImpl;
import com.flash3388.flashlib.scheduling2.actions.Status;
import com.flash3388.flashlib.scheduling2.actions.StatusImpl;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

public class SingleThreadScheduler implements Scheduler {

    private final Clock mClock;
    private final Logger mLogger;

    private final RequirementsControl mRequirementsControl;
    private final ActionsControl mActionsControl;

    public SingleThreadScheduler(Clock clock, Logger logger) {
        mClock = clock;
        mLogger = logger;

        mRequirementsControl = new RequirementsControl();
        mActionsControl = new ActionsControl(mRequirementsControl, clock, logger);
    }

    @Override
    public  Status start(Action action) {
        return mActionsControl.addActionPending(action, new ConfigurationImpl());
    }

    @Override
    public  ActionExecutionBuilder submit(Action action) {
        return new ActionExecutionBuilderImpl(mActionsControl, action);
    }

    @Override
    public void cancelAllActions() {
        mActionsControl.cancelAllActions();
    }

    @Override
    public  Status setDefaultAction(Requirement requirement, Action action) {
        Status status = new StatusImpl(mClock.currentTime());
        ActionContext context = new ActionContext(
                action, new ConfigurationImpl(), status,
                mActionsControl,
                mClock, mLogger);

        mRequirementsControl.setDefaultActionOnRequirement(requirement, context);
        return status;
    }

    @Override
    public void run(SchedulerMode mode) {
        mActionsControl.runActions(mode);

        mActionsControl.removeFinished();

        mActionsControl.startNewActions();
        mActionsControl.startDefaultSubsystemActions();
    }
}
