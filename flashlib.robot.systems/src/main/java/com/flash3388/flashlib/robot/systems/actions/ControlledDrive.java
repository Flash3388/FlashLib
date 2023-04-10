package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.control.ClosedLoopController;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;

public class ControlledDrive extends ActionBase {

    private final Movable mMovable;
    private final ClosedLoopController mController;
    private final DoubleSupplier mProcessVariable;
    private final double mSetPoint;
    private final boolean mIsContinous;

    public ControlledDrive(Movable movable,
                           ClosedLoopController controller,
                           DoubleSupplier processVariable,
                           double setPoint,
                           boolean isContinous) {
        mMovable = movable;
        mController = controller;
        mProcessVariable = processVariable;
        mSetPoint = setPoint;
        mIsContinous = isContinous;
    }

    @Override
    public void initialize(ActionControl control) {
        mController.reset();
    }

    @Override
    public void execute(ActionControl control) {
        double processVariable = mProcessVariable.getAsDouble();
        double output = mController.applyAsDouble(processVariable, mSetPoint);

        mMovable.move(output);

        if (!mIsContinous && mController.isInTolerance(processVariable, mSetPoint)) {
            control.finish();
        }
    }

    @Override
    public void end(FinishReason reason) {
        mMovable.stop();
    }
}
