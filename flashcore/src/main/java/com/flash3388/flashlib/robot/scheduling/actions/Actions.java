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
    @SuppressWarnings("AnonymousInnerClassWithTooManyMethods")
    public static Action empty() {
        return new Action() {
            @Override
            protected void execute() {
            }

            @Override
            protected void end() {
            }
        };
    }

    public static Action wait(Time waitTime) {
        Action action = empty();
        action.setTimeout(waitTime);
        return action;
    }

    /**
     * Creates a canceling action for an action. This is an {@link InstantAction} which calls {@link Action#cancel()}
     * for a given action when started.
     *
     * @param action action to cancel
     * @return canceling action
     */
    public static Action stopAction(Action action){
        Objects.requireNonNull(action, "action is null");

        return new InstantAction(){
            @Override
            public void execute() {
                if(action.isRunning())
                    action.cancel();
            }
        };
    }

    public static Action instantAction(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null");
        return new InstantAction() {
            @Override
            protected void execute() {
                runnable.run();
            }
        };
    }

    @SuppressWarnings("AnonymousInnerClassWithTooManyMethods")
    public static Action runnableAction(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null");
        return new Action() {
            @Override
            protected void execute() {
                runnable.run();
            }
            @Override
            protected void end() {}
        };
    }

    public static Action periodic(Runnable runnable, Time period) {
        Objects.requireNonNull(runnable, "runnable is null");
        return new Action() {
            private final Clock mClock = RunningRobot.getInstance().getClock();
            private Time mLastRun;

            @Override
            protected void initialize() {
                mLastRun = Time.INVALID;
            }

            @Override
            protected void execute() {
                Time now = mClock.currentTime();
                if (!mLastRun.isValid() || now.sub(mLastRun).largerThanOrEquals(period)) {
                    mLastRun = now;

                    runnable.run();
                }
            }

            @Override
            protected void end() { }
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
