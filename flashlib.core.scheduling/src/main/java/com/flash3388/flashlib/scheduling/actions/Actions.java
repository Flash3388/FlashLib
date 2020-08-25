package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.Trigger;
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

    public static Action wait(Time waitTime) {
        return empty().configure()
                .setTimeout(waitTime)
                .save();
    }

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

    public static Action instant(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null");
        return new GenericAction.Builder()
                .onExecute(runnable)
                .isFinished(()-> true)
                .build();
    }

    public static Action fromRunnable(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null");
        return new GenericAction.Builder()
                .onExecute(runnable)
                .build();
    }

    public static Action periodic(Runnable runnable, Time period) {
        Objects.requireNonNull(runnable, "runnable is null");
        return new PeriodicAction(runnable, period);
    }

    public static SequentialActionGroup sequential(Action... actions) {
        return new SequentialActionGroup()
                .add(actions);
    }

    public static ParallelActionGroup parallel(Action... actions) {
        return new ParallelActionGroup()
                .add(actions);
    }

    public static Trigger onCondition(BooleanSupplier condition, Action action) {
        Trigger trigger = Triggers.onCondition(condition);
        trigger.whenActive(action);

        return trigger;
    }

    public static Action when(BooleanSupplier condition) {
        return new ConditionalAction(condition, null, null);
    }
}
