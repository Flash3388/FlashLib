package com.flash3388.flashlib.scheduling.threading;

import com.beans.Property;
import com.beans.properties.atomic.AtomicProperty;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class MultiThreadedScheduler implements Scheduler, AutoCloseable {

    private static final int DEFAULT_WORKER_COUNT = 3;

    private final MtWorkers mWorkersControl;
    private final Logger mLogger;

    private final MtRequirementsControl mRequirementsControl;
    private final MtActionsControl mActionsControl;
    private final Property<SchedulerMode> mCurrentMode;
    private final Set<Action> mActionsToStart;

    MultiThreadedScheduler(MtWorkers workersControl, Logger logger,
                                  MtRequirementsControl requirementsControl, MtActionsControl actionsControl,
                                  Property<SchedulerMode> currentMode, Set<Action> actionsToStart) {
        mWorkersControl = workersControl;
        mLogger = logger;
        mRequirementsControl = requirementsControl;
        mActionsControl = actionsControl;
        mCurrentMode = currentMode;
        mActionsToStart = actionsToStart;

        mWorkersControl.runWorkers(()-> new MtSchedulerWorker(mActionsControl, mCurrentMode, mLogger));
    }

    public MultiThreadedScheduler(MtWorkers workersControl, Clock clock, Logger logger) {
        mWorkersControl = workersControl;
        mLogger = logger;
        mRequirementsControl = new MtRequirementsControl(logger);
        mActionsControl = new MtActionsControl(mRequirementsControl, clock, logger);
        mCurrentMode = new AtomicProperty<>();
        mActionsToStart = new HashSet<>();

        mWorkersControl.runWorkers(()-> new MtSchedulerWorker(mActionsControl, mCurrentMode, mLogger));
    }

    public MultiThreadedScheduler(Clock clock, Logger logger) {
        this(new MtDaemonThreadWorkers(DEFAULT_WORKER_COUNT), clock, logger);
    }

    public MultiThreadedScheduler(Clock clock) {
        this(clock, Logging.stub());
    }

    @Override
    public void start(Action action) {
        Objects.requireNonNull(action, "action is null");

        mActionsToStart.add(action);
    }

    @Override
    public void cancel(Action action) {
        Objects.requireNonNull(action, "action is null");

        if(!mActionsToStart.remove(action)) {
            mActionsControl.cancelAction(action);
        }
    }

    @Override
    public boolean isRunning(Action action) {
        Objects.requireNonNull(action, "action is null");

        return mActionsToStart.contains(action) ||
                mActionsControl.isActionRunning(action);
    }

    @Override
    public Time getActionRunTime(Action action) {
        Objects.requireNonNull(action, "action is null");

        if (mActionsToStart.contains(action)) {
            return Time.milliseconds(0);
        }

        return mActionsControl.getActionRunTime(action);
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");

        mActionsToStart.removeIf(predicate);
        mActionsControl.cancelActionsIf(predicate);
    }

    @Override
    public void cancelAllActions() {
        mActionsToStart.clear();
        mActionsControl.cancelAllActions();
    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {
        Objects.requireNonNull(subsystem, "subsystem is null");
        Objects.requireNonNull(action, "action is null");

        mRequirementsControl.setDefaultActionOnSubsystem(subsystem, action);
    }

    @Override
    public Optional<Action> getActionRunningOnRequirement(Requirement requirement) {
        Objects.requireNonNull(requirement, "requirement is null");
        return mRequirementsControl.getActionOnRequirement(requirement);
    }

    @Override
    public void run(SchedulerMode mode) {
        mCurrentMode.set(mode);

        mActionsControl.processFinishedActions();

        mActionsControl.startActions(mActionsToStart);
        mActionsToStart.clear();

        startDefaultActions(mode);
    }

    @Override
    public void close() {
        mWorkersControl.stopWorkers();
    }

    private void startDefaultActions(SchedulerMode mode) {
        for (Map.Entry<Subsystem, Action> entry : mRequirementsControl.getDefaultActionsToStart().entrySet()) {
            try {
                Action action = entry.getValue();

                if (mode.isDisabled() &&
                        !action.getConfiguration().shouldRunWhenDisabled()) {
                    continue;
                }

                mLogger.debug("Starting default action for {}", entry.getKey());
                action.start();
            } catch (Throwable t) {
                mLogger.error("Error when starting default action", t);
            }
        }
    }
}
