package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.scheduling2.ActionsControl;
import com.flash3388.flashlib.scheduling2.Requirement;
import com.flash3388.flashlib.scheduling2.SchedulerMode;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.Collection;

public class ActionContext {

    private final Action mAction;
    private final Configuration mConfiguration;
    private final Status mStatus;
    private final Clock mClock;
    private final Logger mLogger;
    private final Control mControl;

    private boolean mIsConfigured;

    private Step mStep;

    public ActionContext(Action action,
                         Configuration configuration,
                         Status status,
                         ActionsControl actionsControl,
                         Clock clock, Logger logger) {
        mAction = action;
        mConfiguration = configuration;
        mStatus = status;
        mClock = clock;
        mLogger = logger;
        mControl = new ControlImpl(actionsControl, status);

        mStep = null;
        mIsConfigured = false;
    }

    public boolean isConfigured() {
        return mIsConfigured;
    }

    public void configure() {
        Configuration configuration = new ConfigurationImpl();
        mAction.configure(configuration);
        configuration.copyTo(mConfiguration);

        mStep = new Step.InitializationStep();

        mIsConfigured = true;
    }

    public boolean run(SchedulerMode mode) {
        if (mode.isDisabled() &&
                !mConfiguration.shouldRunWhenDisabled()) {
            mLogger.debug("Action {} running in disabled. Canceling", mAction);
            mStatus.cancel();
        }

        try {
            if (mStatus.isCanceled()) {
                mStep = mStep.onCancel(this);
                if (mStep == null) {
                    return false;
                }
            }

            mStep = mStep.execute(this);
        } catch (Throwable t) {
            mStatus.markErrored(t);
            mStep = mStep.onError(this);

            mLogger.error("Error while executing action", t);
        }

        return mStep != null;
    }

    public Collection<? extends Requirement> getRequirements() {
        return mConfiguration.getRequirements();
    }

    public void cancel() {
        mStatus.cancel();
    }

    @Override
    public String toString() {
        return String.format("%s{name=%s}",
                getClass().getSimpleName().isEmpty() ?
                        getClass().getName() :
                        getClass().getSimpleName(),
                mConfiguration.getName());
    }

    void initializeAction() {
        mStatus.markStarted(mClock.currentTime());
        mAction.initialize(mControl);
    }

    void executeAction() {
        mAction.execute(mControl);
    }

    void endAction() {
        mAction.end(mControl);
    }

    boolean isActionFinished() {
        return mControl.isFinished();
    }

    void actionFinished() {
        mStatus.markFinished();
    }

    boolean isActionTimeout() {
        Time now = mClock.currentTime();
        Time timeout = mConfiguration.getTimeout();

        return timeout.isValid() && now.sub(mStatus.getStartTime()).after(timeout);
    }

    void interruptAction() {
        mControl.markInterrupted();
    }
}
