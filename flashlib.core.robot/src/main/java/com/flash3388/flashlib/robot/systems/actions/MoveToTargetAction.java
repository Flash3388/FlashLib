package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.systems.FeedbackControlledMotorSystem;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;

public class MoveToTargetAction extends ActionBase {

    private final FeedbackControlledMotorSystem mSystem;
    private final DoubleSupplier mTarget;

    public MoveToTargetAction(FeedbackControlledMotorSystem system, DoubleSupplier target) {
        mSystem = system;
        mTarget = target;

        requires(system);
    }

    @Override
    public void initialize() {
        mSystem.reset();
    }

    @Override
    public void execute() {
        mSystem.moveTo(mTarget.getAsDouble());
    }

    @Override
    public boolean isFinished() {
        return mSystem.isAt(mTarget.getAsDouble());
    }

    @Override
    public void end(boolean wasInterrupted) {
        mSystem.stop();
    }
}
