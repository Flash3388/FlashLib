package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.scheduling.triggers.Triggers;
import com.flash3388.flashlib.time.Clock;
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

    /**
     * Creates a canceling action for an action. This is an {@link InstantAction} which calls {@link Action#cancel()}
     * for a given action when started.
     *
     * @param action action to cancel
     * @return canceling action
     */
    public static Action cancelingAction(Action action){
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
        return new ActionBase() {
            private final Clock mClock = RunningRobot.getInstance().getClock();
            private Time mLastRun;

            @Override
            public void initialize() {
                mLastRun = Time.INVALID;
            }

            @Override
            public void execute() {
                Time now = mClock.currentTime();
                if (!mLastRun.isValid() || now.sub(mLastRun).largerThanOrEquals(period)) {
                    mLastRun = now;

                    runnable.run();
                }
            }
        };
    }

    public static Action sequential(Action... actions) {
        return new SequentialActionGroup()
                .add(actions);
    }

    public static Action parallel(Action... actions) {
        return new ParallelActionGroup()
                .add(actions);
    }

    public static Action conditional(BooleanSupplier condition, Action action) {
        Triggers.onCondition(condition)
                .whenActive(action);

        return action;
    }
}
