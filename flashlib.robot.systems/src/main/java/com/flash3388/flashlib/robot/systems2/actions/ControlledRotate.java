package com.flash3388.flashlib.robot.systems2.actions;

import com.flash3388.flashlib.robot.control.Controller;
import com.flash3388.flashlib.robot.motion.Rotatable;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;

public class ControlledRotate extends ActionBase {

    private final Rotatable mInterface;
    private final Controller mController;
    private final DoubleSupplier mProcessVariable;
    private final DoubleSupplier mSetPoint;
    private final double mThreshold;

    public ControlledRotate(Rotatable anInterface, Controller controller, DoubleSupplier processVariable, DoubleSupplier setPoint, double threshold) {
        mInterface = anInterface;
        mController = controller;
        mProcessVariable = processVariable;
        mSetPoint = setPoint;
        mThreshold = threshold;
    }

    @Override
    public void initialize() {
        mController.reset();
    }

    @Override
    public void execute() {
        double processVariable = mProcessVariable.getAsDouble();
        double setPoint = mSetPoint.getAsDouble();
        double output = mController.calculate(processVariable, setPoint);
        mInterface.rotate(output);
    }

    @Override
    public boolean isFinished() {
        double processVariable = mProcessVariable.getAsDouble();
        double setPoint = mSetPoint.getAsDouble();

        return ExtendedMath.constrained(
                processVariable - setPoint,
                -mThreshold,
                mThreshold
        );
    }

    @Override
    public void end(boolean wasInterrupted) {
        mInterface.stop();
    }

}
