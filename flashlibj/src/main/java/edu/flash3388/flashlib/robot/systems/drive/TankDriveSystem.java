package edu.flash3388.flashlib.robot.systems.drive;

import edu.flash3388.flashlib.robot.io.devices.actuators.SpeedController;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.algorithms.DriveAlgorithms;

public class TankDriveSystem extends Subsystem implements TankDrive {

    private final SpeedController mRightController;
    private final SpeedController mLeftController;

    private final DriveAlgorithms mDriveAlgorithms;

    public TankDriveSystem(SpeedController rightController, SpeedController leftController, DriveAlgorithms driveAlgorithms) {
        mRightController = rightController;
        mLeftController = leftController;

        mDriveAlgorithms = driveAlgorithms;
    }

    public TankDriveSystem(SpeedController rightController, SpeedController leftController) {
        this(rightController, leftController, new DriveAlgorithms());
    }

    @Override
    public void tankDrive(double right, double left) {
        mRightController.set(right);
        mLeftController.set(left);
    }

    @Override
    public void arcadeDrive(double moveValue, double rotateValue) {
        TankDriveSpeed driveSpeed = mDriveAlgorithms.arcadeDrive(moveValue, rotateValue);
        tankDrive(driveSpeed);
    }

    @Override
    public void stop() {
        mRightController.stop();
        mLeftController.stop();
    }
}
