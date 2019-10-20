package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

import java.util.Collection;
import java.util.Objects;

public class SequentialActionGroup extends Action {

    private SequentialAction mFirstAction;

    private Action mCurrentAction;
    private boolean mRunWhenDisabled;

    public SequentialActionGroup() {
        this(null);
    }

    public SequentialActionGroup(SequentialAction firstAction) {
        this(RunningRobot.INSTANCE.get().getScheduler(), RunningRobot.INSTANCE.get().getClock(), firstAction);
    }

    SequentialActionGroup(Scheduler scheduler, Clock clock, SequentialAction firstAction) {
        super(scheduler, clock, Time.INVALID);

        if (firstAction != null) {
            first(firstAction);
        }

        mCurrentAction = null;
        mRunWhenDisabled = false;
    }

    public SequentialActionGroup first(SequentialAction action) {
        validateNotRunning();

        mFirstAction = Objects.requireNonNull(action, "action is null");
        mFirstAction.setParent(this);

        mRunWhenDisabled = mFirstAction.runWhenDisabled();

        return this;
    }

    @Override
    protected final void initialize() {
        mCurrentAction = mFirstAction;
    }

    @Override
    protected final void execute() {
        if (mCurrentAction == null) {
            return;
        }

        if (!mCurrentAction.isRunning()) {
            ensureHasRequirements(mCurrentAction.getRequirements());
            if (mCurrentAction instanceof SequentialAction) {
                ((SequentialAction) mCurrentAction).resetRunNext();
            }

            mCurrentAction.markStarted();
        } else {
            if (!mCurrentAction.run()) {
                mCurrentAction.removed();

                if (mCurrentAction instanceof SequentialAction) {
                    mCurrentAction = ((SequentialAction) mCurrentAction).getRunNext();
                    if (mCurrentAction != null) {
                        mRunWhenDisabled &= mCurrentAction.runWhenDisabled();
                    }
                } else {
                    mCurrentAction = null;
                }
            }
        }
    }

    @Override
    protected final boolean isFinished() {
        return mCurrentAction == null;
    }

    @Override
    protected final void end() {

    }

    @Override
    protected final void interrupted() {
        if (mCurrentAction != null && mCurrentAction.isRunning()) {
            mCurrentAction.markCanceled();
            mCurrentAction.removed();
        }
    }

    @Override
    protected final boolean runWhenDisabled() {
        return mRunWhenDisabled;
    }

    private void ensureHasRequirements(Collection<Subsystem> requirements) {
        if (!getRequirements().containsAll(requirements)) {
            throw new IllegalStateException("SequentialActionGroup does not have requirement for action");
        }
    }
}
