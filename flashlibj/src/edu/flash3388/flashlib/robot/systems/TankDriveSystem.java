package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;

public class TankDriveSystem extends Subsystem implements TankDriveInterface {

    private final FlashSpeedController mRightController;
    private final FlashSpeedController mLeftController;

    private final DriveAlgorithms mDriveAlgorithms;

    public TankDriveSystem(FlashSpeedController rightController, FlashSpeedController leftController, DriveAlgorithms driveAlgorithms) {
        mRightController = rightController;
        mLeftController = leftController;

        mDriveAlgorithms = driveAlgorithms;
    }

    public TankDriveSystem(FlashSpeedController rightController, FlashSpeedController leftController) {
        this(rightController, leftController, new DriveAlgorithms());
    }

    @Override
    public void arcadeDrive(double moveValue, double rotateValue) {
        double[] values = mDriveAlgorithms.arcadeDrive(moveValue, rotateValue);
        tankDrive(values[0], values[1]);
    }

    @Override
    public void tankDrive(double right, double left) {
        mRightController.set(right);
        mLeftController.set(left);
    }

    @Override
    public void stop() {
        mRightController.stop();
        mLeftController.stop();
    }
}
