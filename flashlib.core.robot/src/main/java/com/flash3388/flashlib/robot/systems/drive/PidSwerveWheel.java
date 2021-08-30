package com.flash3388.flashlib.robot.systems.drive;

import java.util.function.DoubleSupplier;

import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.control.PidController;
import com.jmath.vectors.Vector2;

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

    //this is bad, but I assume there is something built in, so I will wait for the PR on this one
    public static double convertAngle(double angle) {
        return angle < 0 ? 360 + angle : angle;
    }

    public void resetPid() {
        mPidController.reset();
    }

    @Override
    public void move(Vector2 motionVector) {
        double forwardSpeed = motionVector.magnitude();
        double targetAngle = motionVector.angle();
        double currentAngle = convertAngle(mAngleDegreeSupplier.getAsDouble());

        double nextAngle = calcNextAngle(targetAngle, currentAngle);
        double pidOut = mPidController.applyAsDouble(currentAngle, nextAngle);

        rotate(pidOut);
        move(forwardSpeed);
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
}
