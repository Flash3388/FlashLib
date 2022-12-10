package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.ActionGroupType;
import com.flash3388.flashlib.scheduling.triggers.Trigger;
import com.flash3388.flashlib.scheduling.triggers.Triggers;
import com.flash3388.flashlib.time.Time;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public final class Actions {

    private Actions() {}

    /**
     * Creates an action which does nothing.
     *
     * @return an empty action.
     */
    public static Action empty() {
        return new ActionBase() {
            @Override
            public void execute() { }
        };
    }

    /**
     * Creates an empty action which runs for a specified amount of time,
     * Only finishing when that time is reached.
     *
     * Use this action to wait for a given amount of time.
     *
     * @param waitTime time to run
     * @return action
     */
    public static Action wait(Time waitTime) {
        return empty().configure()
                .setTimeout(waitTime)
                .save();
    }

    /**
     * Creates a builder for creating a {@link GenericAction}.
     *
     * @return builder.
     */
    public static GenericAction.Builder builder() {
        return new GenericAction.Builder();
    }

    /**
     * Creates a canceling action for an action. This is an {@link InstantAction} which calls {@link Action#cancel()}
     * for a given action when started.
     *
     * @param action action to cancel
     * @return canceling action
     */
    public static Action canceling(Action action){
        Objects.requireNonNull(action, "action is null");
        return new GenericAction.Builder()
                .onExecute(()-> {
                    if (action.isRunning()) {
                        action.cancel();
                    }
                })
                .isFinished(()-> true)
                .build();
    }

    /**
     * Creates an action which runs a given runnable, ending
     * immediately after the runnable has finished.
     *
     * @param runnable runnable to run
     * @return action
     */
    public static Action instant(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null");
        return new GenericAction.Builder()
                .onExecute(runnable)
                .isFinished(()-> true)
                .build();
    }

    /**
     * Creates an action which repeatedly executes a given runnable, until
     * it ends.
     *
     * The action is not configured with a specified end point,
     * use {@link Action#withTimeout(Time)}, add requirements or interrupt the action
     * manually to end it.
     *
     * @param runnable runnable which runs in the execution phase.
     * @return action
     */
    public static Action fromRunnable(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null");
        return new GenericAction.Builder()
                .onExecute(runnable)
                .build();
    }

    /**
     * Creates an action which executes a given runnable at a specified
     * period, meaning that the runnable executes every few time units, as
     * configured.
     *
     * The action is not configured with a specified end point,
     * use {@link Action#withTimeout(Time)}, add requirements or interrupt the action
     * manually to end it.
     *
     * @param runnable runnable which runs in the execution phase.
     * @param period period of execution
     * @return action
     */
    public static Action periodic(Runnable runnable, Time period) {
        Objects.requireNonNull(runnable, "runnable is null");
        return new PeriodicAction(runnable, period);
    }

    /**
     * Creates a group which executes the given actions one after the other, in order (i.e. when one
     * is finished, the other starts, thus called sequential).
     *
     * The group will end when the last action in the order has finished.
     *
     * @param actions actions to group
     * @return action group
     */
    public static ActionGroup sequential(Action... actions) {
        return GlobalDependencies.getScheduler().newActionGroup(ActionGroupType.SEQUENTIAL)
                .add(actions);
    }

    /**
     * Creates a group which executes the given actions at the same (i.e. the actions run together, thus parallel).
     *
     * The group will end when all the actions are finished.
     *
     * @param actions actions to group
     * @return action group
     */
    public static ActionGroup parallel(Action... actions) {
        return GlobalDependencies.getScheduler().newActionGroup(ActionGroupType.PARALLEL)
                .add(actions);
    }

    /**
     * Creates a group which executes the given actions at the same (i.e. the actions run together, thus parallel).
     *
     * The group will end when one of the actions finishes execution, no matter which.
     *
     * @param actions actions to group
     * @return action group
     */
    public static ActionGroup race(Action... actions) {
        return GlobalDependencies.getScheduler().newActionGroup(ActionGroupType.PARALLEL_RACE)
                .add(actions);
    }

    /**
     * Creates a trigger which starts a given action when a condition is met.
     *
     * @param condition condition
     * @param action action to start on condition
     * @return trigger
     */
    public static Trigger onCondition(BooleanSupplier condition, Action action) {
        Trigger trigger = Triggers.onCondition(condition);
        trigger.whenActive(action);

        return trigger;
    }
}
