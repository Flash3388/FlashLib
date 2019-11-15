package com.flash3388.flashlib.robot.systems.drive;

import com.flash3388.flashlib.robot.io.devices.actuators.SpeedController;
import com.flash3388.flashlib.robot.scheduling.Subsystem;
import com.flash3388.flashlib.robot.systems.drive.algorithms.DriveAlgorithms;

public class MecanumDriveSystem extends Subsystem implements MecanumDrive {

    private final SpeedController mFrontRightController;
    private final SpeedController mBackRightController;
    private final SpeedController mFrontLeftController;
    private final SpeedController mBackLeftController;

    private final DriveAlgorithms mDriveAlgorithms;

    public MecanumDriveSystem(SpeedController frontRightController, SpeedController backRightController,
                              SpeedController frontLeftController, SpeedController backLeftController,
                              DriveAlgorithms driveAlgorithms) {
        mFrontRightController = frontRightController;
        mBackRightController = backRightController;
        mFrontLeftController = frontLeftController;
        mBackLeftController = backLeftController;

        mDriveAlgorithms = driveAlgorithms;
    }

    public MecanumDriveSystem(SpeedController frontRightController, SpeedController backRightController,
                              SpeedController frontLeftController, SpeedController backLeftController) {
        this(frontRightController, backRightController, frontLeftController, backLeftController, new DriveAlgorithms());
    }

    public final SpeedController getFrontRightController() {
        return mFrontRightController;
    }

    public final SpeedController getBackRightController() {
        return mBackRightController;
    }

    public final SpeedController getFrontLeftController() {
        return mFrontLeftController;
    }

    public final SpeedController getBackLeftController() {
        return mBackLeftController;
    }

    @Override
    public void mecanumDrive(double frontRight, double backRight, double frontLeft, double backLeft) {
        mFrontRightController.set(frontRight);
        mBackRightController.set(backRight);
        mFrontLeftController.set(frontLeft);
        mBackLeftController.set(backLeft);
    }

    @Override
    public void holonomicPolar(double magnitude, double direction, double rotation) {
        MecanumDriveSpeed driveSpeed = mDriveAlgorithms.mecanumDrivePolar(magnitude, direction, rotation);
        mecanumDrive(driveSpeed);
    }

    @Override
    public void stop() {
        mFrontRightController.stop();
        mBackRightController.stop();
        mFrontLeftController.stop();
        mBackLeftController.stop();
    }
}
