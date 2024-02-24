package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionFlag;
import com.flash3388.flashlib.scheduling.actions.ActionGroup;
import com.flash3388.flashlib.scheduling.triggers.ManualTrigger;
import com.flash3388.flashlib.scheduling.triggers.Trigger;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class EmptyScheduler implements Scheduler {


    @Override
    public ScheduledAction start(Action action) {
        return null;
    }

    @Override
    public void cancel(Action action) {

    }

    @Override
    public boolean isRunning(Action action) {
        return false;
    }

    @Override
    public ExecutionState getExecutionStateOf(Action action) {
        return null;
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
    }

    @Override
    public void cancelActionsIfWithoutFlag(ActionFlag flag) {

    }

    @Override
    public void cancelAllActions() {

    }

    @Override
    public DefaultActionRegistration setDefaultAction(Subsystem subsystem, Action action) {
        return null;
    }

    @Override
    public Optional<DefaultActionRegistration> getDefaultActionRegistration(Subsystem subsystem) {
        return Optional.empty();
    }

    @Override
    public Optional<Action> getActionRunningOnRequirement(Requirement requirement) {
        return Optional.empty();
    }

    @Override
    public void run(SchedulerMode mode) {

    }

    @Override
    public Trigger newTrigger(BooleanSupplier condition) {
        return null;
    }

    @Override
    public ManualTrigger newManualTrigger() {
        return null;
    }

    @Override
    public ActionGroup newActionGroup(ActionGroupType type) {
        return null;
    }
}
