package com.flash3388.flashlib.robot.scheduling;

import java.util.Collection;
import java.util.Objects;

public class SequentialActionGroup extends Action {

    private SequentialAction mFirstAction;

    private Action mCurrentAction;

    public SequentialActionGroup() {
        mFirstAction = null;
        mCurrentAction = null;
    }

    public SequentialActionGroup setFirst(SequentialAction action) {
        validateNotRunning();

        mFirstAction = Objects.requireNonNull(action, "action is null");
        mFirstAction.setParent(this);

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
            insureHasRequirements(mCurrentAction.getRequirements());
            if (mCurrentAction instanceof SequentialAction) {
                ((SequentialAction) mCurrentAction).resetRunNext();
            }

            mCurrentAction.startAction();
        } else {
            if (!mCurrentAction.run()) {
                mCurrentAction.removed();

                if (mCurrentAction instanceof SequentialAction) {
                    mCurrentAction = ((SequentialAction) mCurrentAction).getRunNext();
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
            mCurrentAction.cancelAction();
            mCurrentAction.removed();
        }
    }

    private void insureHasRequirements(Collection<Subsystem> requirements) {
        if (!getRequirements().containsAll(requirements)) {
            throw new IllegalStateException("SequentialActionGroup does not have requirement for action");
        }
    }
}
