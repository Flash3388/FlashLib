package com.flash3388.flashlib.robot.systems.drive;

import com.flash3388.flashlib.robot.io.devices.actuators.SpeedController;
import com.flash3388.flashlib.robot.scheduling.Subsystem;
import com.flash3388.flashlib.robot.systems.drive.algorithms.DriveAlgorithms;
import com.jmath.vectors.Vector2;

public class OmniDriveSystem extends Subsystem implements OmniDrive {

    private final SpeedController mFrontController;
    private final SpeedController mRightController;
    private final SpeedController mBackController;
    private final SpeedController mLeftController;

    private final DriveAlgorithms mDriveAlgorithms;

    public OmniDriveSystem(SpeedController frontController, SpeedController rightController,
                           SpeedController backController, SpeedController leftController,
                           DriveAlgorithms driveAlgorithms) {
        mFrontController = frontController;
        mRightController = rightController;
        mBackController = backController;
        mLeftController = leftController;

        mDriveAlgorithms = driveAlgorithms;
    }

    public OmniDriveSystem(SpeedController frontController, SpeedController rightController,
                           SpeedController backController, SpeedController leftController) {
        this(frontController, rightController, backController, leftController, new DriveAlgorithms());
    }

    public final SpeedController getFrontController() {
        return mFrontController;
    }

    public final SpeedController getBackController() {
        return mBackController;
    }

    public final SpeedController getRightController() {
        return mRightController;
    }

    public final SpeedController getLeftController() {
        return mLeftController;
    }

    @Override
    public void omniDrive(double front, double right, double back, double left) {
        mFrontController.set(front);
        mRightController.set(right);
        mBackController.set(back);
        mLeftController.set(left);
    }

    @Override
    public void holonomicCartesian(double y, double x, double rotation) {
        if (rotation != 0.0) {
            OmniDriveSpeed driveSpeed = mDriveAlgorithms.vectoredOmniDriveCartesian(y, x, rotation);
            omniDrive(driveSpeed);
        } else {
            omniDrive(y, x);
        }
    }

    @Override
    public void holonomicPolar(double magnitude, double direction, double rotation) {
        Vector2 vector = Vector2.polar(magnitude, direction);
        holonomicCartesian(vector.y(), vector.x(), rotation);
    }

    @Override
    public void stop() {
        mFrontController.stop();
        mRightController.stop();
        mBackController.stop();
        mLeftController.stop();
    }
}
