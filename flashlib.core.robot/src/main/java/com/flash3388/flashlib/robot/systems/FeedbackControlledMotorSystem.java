package com.flash3388.flashlib.robot.systems;

import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.control.Controller;
import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;

public class FeedbackControlledMotorSystem extends MotorSystem {

    private final Controller mSystemController;
    private final DoubleSupplier mProcessVariable;
    private final double mTargetThreshold;

    public FeedbackControlledMotorSystem(SpeedController controller, Controller systemController, DoubleSupplier processVariable, double targetThreshold) {
        super(controller);
        mSystemController = systemController;
        mProcessVariable = processVariable;
        mTargetThreshold = targetThreshold;
    }

    public FeedbackControlledMotorSystem(Controller systemController, DoubleSupplier processVariable, double targetThreshold, SpeedController... controllers) {
        super(controllers);
        mSystemController = systemController;
        mProcessVariable = processVariable;
        mTargetThreshold = targetThreshold;
    }

    public void reset() {
        mSystemController.reset();
    }

    public void moveTo(double setPoint) {
        double speed = mSystemController.calculate(mProcessVariable.getAsDouble(), setPoint);
        move(speed);
    }

    public void rotateTo(double setPoint) {
        double speed = mSystemController.calculate(mProcessVariable.getAsDouble(), setPoint);
        rotate(speed);
    }

    public boolean isAt(double setPoint) {
        return ExtendedMath.constrained(
                mProcessVariable.getAsDouble(),
                setPoint - mTargetThreshold,
                setPoint + mTargetThreshold
        );
    }
}
