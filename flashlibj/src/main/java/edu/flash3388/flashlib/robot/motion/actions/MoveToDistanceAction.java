package edu.flash3388.flashlib.robot.motion.actions;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.control.PidController;
import edu.flash3388.flashlib.robot.motion.Movable;
import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;

import java.util.function.DoubleSupplier;

public class MoveToDistanceAction extends Action {

    private final PidController mPidController;
    private final Movable mMovable;
    private final DoubleSupplier mDistanceSupplier;
    private final double mWantedDistance;
    private final double mDistanceMargin;

    public MoveToDistanceAction(PidController pidController, Movable movable, DoubleSupplier distanceSupplier, double wantedDistance, double distanceMargin) {
        mPidController = pidController;
        mMovable = movable;
        mDistanceSupplier = distanceSupplier;
        mWantedDistance = wantedDistance;
        mDistanceMargin = distanceMargin;

        mPidController.setOutputLimit(1.0);

        if (movable instanceof Subsystem) {
            requires(((Subsystem)movable));
        }
    }

    @Override
    protected void initialize() {
        mPidController.reset();
    }

    @Override
    protected void execute() {
        double pidResult = mPidController.calculate(mDistanceSupplier.getAsDouble(), mWantedDistance);
        mMovable.move(pidResult);
    }

    @Override
    protected boolean isFinished() {
        double distanceToTarget = Math.abs(mDistanceSupplier.getAsDouble() - mWantedDistance);
        return Mathf.constrained(distanceToTarget, 0, mDistanceMargin);
    }

    @Override
    protected void end() {
        mMovable.stop();
    }
}
