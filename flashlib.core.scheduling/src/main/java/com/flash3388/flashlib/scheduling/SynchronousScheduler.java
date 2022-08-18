package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.triggers.Trigger;
import com.flash3388.flashlib.scheduling.triggers.TriggerActivationAction;
import com.flash3388.flashlib.scheduling.triggers.TriggerImpl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * A single-thread implementation of the {@link Scheduler}. Executes all actions in the thread which performs
 * calls to the scheduler. Should not be used from multiple threads as this will lead to undefined behaviour.
 * <p>
 *     Calls to {@link #run(SchedulerMode)} will actually run the actions.
 * </p>
 * <p>
 *     Calls to {@link #start(Action)} will not start the actions immediately, but rather on the next invocation
 *     of {@link #run(SchedulerMode)}
 * </p>
 * <p>
 *     Calls to {@link #cancel(Action)}, {@link #cancelActionsIf(Predicate)} and {@link #cancelAllActions()} will
 *     stop the action on invocation.
 * </p>
 *
 * @deprecated use {@link com.flash3388.flashlib.scheduling.impl.NewSynchronousScheduler}
 */
@Deprecated
public class SynchronousScheduler implements Scheduler {

    private final RequirementsControl mRequirementsControl;
    private final ActionControl mActionControl;
    private final SchedulerIteration mSchedulerIteration;

    public SynchronousScheduler(Clock clock, Logger logger) {
        mRequirementsControl = new RequirementsControl(logger);
        mActionControl = new ActionControl(clock, mRequirementsControl, logger);
        mSchedulerIteration = new SchedulerIteration(mActionControl, mRequirementsControl, logger);
    }

    public SynchronousScheduler(Clock clock) {
        this(clock, Logging.stub());
    }

    @Override
    public void start(Action action) {
        Objects.requireNonNull(action, "action is null");
        mActionControl.startAction(action);
    }

    @Override
    public void cancel(Action action) {
        Objects.requireNonNull(action, "action is null");
        mActionControl.cancelAction(action);
    }

    @Override
    public boolean isRunning(Action action) {
        Objects.requireNonNull(action, "action is null");
        return mActionControl.isActionRunning(action);
    }

    @Override
    public Time getActionRunTime(Action action) {
        Objects.requireNonNull(action, "action is null");
        return mActionControl.getActionRunTime(action);
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        mActionControl.cancelActionsIf(predicate);
    }

    @Override
    public void cancelAllActions() {
        mActionControl.cancelAllActions();
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
        Objects.requireNonNull(mode, "mode is null");
        mSchedulerIteration.run(mode);
    }

    @Override
    public Trigger newTrigger(BooleanSupplier condition) {
        TriggerImpl trigger = new TriggerImpl();

        Action action = new TriggerActivationAction(this, condition, trigger)
                .requires(trigger);
        start(action);

        return trigger;
    }
}
