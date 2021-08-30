package com.flash3388.flashlib.robot.systems.drive;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.control.PidController;
import com.flash3388.flashlib.robot.motion.Rotatable;
import com.jmath.ExtendedMath;
import com.jmath.units.Angle;
import com.jmath.units.AngleUnit;
import com.jmath.vectors.Vector2;
import com.jmath.vectors.Vectors;

public class PidSwerveWheel implements SwerveWheel {

    private final PidController mPidController;
    private final SpeedController mForwardController;
    private final SpeedController mRotationController;
    private final DoubleSupplier mAngleDegreeSupplier;

    public PidSwerveWheel(PidController pidController, SpeedController forwardController,
            SpeedController rotationController, DoubleSupplier angleDegreeSupplier) {
        mPidController = pidController;
        mForwardController = forwardController;
        mRotationController = rotationController;
        mAngleDegreeSupplier = angleDegreeSupplier;

        resetPid();
    }

    public PidSwerveWheel(SpeedController forwardController, SpeedController rotationController,
            DoubleSupplier angleDegreeSupplier, double kp, double ki, double kd, double kf) {
        this(new PidController(kp, kd, kd, kf), forwardController, rotationController, angleDegreeSupplier);
    }

    public void resetPid() {
        mPidController.reset();
    }

    @Override
    public void move(Vector2 motionVector) {
        double targetAngle = motionVector.angle();
        double currentAngle = convertAngle(mAngleDegreeSupplier.getAsDouble());

        double nextAngle = calcNextAngle(targetAngle, currentAngle);
        double pidOut = mPidController.applyAsDouble(currentAngle, nextAngle);

        rotate(pidOut);
        move(pidOut);
    }

    @Override
    public void rotate(double speed) {
        mRotationController.set(speed);
    }
    
    @Override
    public void stop() {
        mForwardController.stop();
        mRotationController.stop();
    }

    private double calcNextAngle(double targetAngle, double currentAngle) {
        targetAngle = convertAngle(targetAngle);
        currentAngle = convertAngle(currentAngle);
        double distance = (targetAngle - currentAngle + 180 ) % 360 - 180;

        return currentAngle + distance;
    }

    private double convertAngle(double angle) {
        return angle < 0 ? 360 + angle : angle;
    }
}
