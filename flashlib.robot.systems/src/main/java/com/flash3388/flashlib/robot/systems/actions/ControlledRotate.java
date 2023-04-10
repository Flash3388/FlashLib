package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.control.ClosedLoopController;
import com.flash3388.flashlib.robot.motion.Rotatable;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;

public class ControlledRotate extends ActionBase {

    private final Rotatable mRotatable;
    private final ClosedLoopController mController;
    private final DoubleSupplier mProcessVariable;
    private final double mSetpoint;
    private final boolean mIsContinous;

    public ControlledRotate(Rotatable rotatable,
                            ClosedLoopController controller,
                            DoubleSupplier processVariable,
                            double setpoint,
                            boolean isContinous) {
        mRotatable = rotatable;
        mController = controller;
        mProcessVariable = processVariable;
        mSetpoint = setpoint;
        mIsContinous = isContinous;
    }

    @Override
    public void initialize(ActionControl control) {
        mController.reset();
    }

    @Override
    public void execute(ActionControl control) {
        double processVariable = mProcessVariable.getAsDouble();
        double output = mController.applyAsDouble(processVariable, mSetpoint);

        mRotatable.rotate(output);

        if (!mIsContinous && mController.isInTolerance(processVariable, mSetpoint)) {
            control.finish();
        }
    }

    @Override
    public void end(FinishReason reason) {
        mRotatable.stop();
    }
}
