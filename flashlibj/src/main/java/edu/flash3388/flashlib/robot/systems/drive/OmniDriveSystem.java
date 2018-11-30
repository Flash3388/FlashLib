package edu.flash3388.flashlib.robot.systems.drive;

import edu.flash3388.flashlib.robot.io.devices.actuators.SpeedController;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.algorithms.DriveAlgorithms;

public class OmniDriveSystem extends Subsystem implements HolonomicDriveInterface {

    private final SpeedController mFrontController;
    private final SpeedController mRightController;
    private final SpeedController mRearController;
    private final SpeedController mLeftController;

    private final DriveAlgorithms mDriveAlgorithms;

    public OmniDriveSystem(SpeedController frontController, SpeedController rightController,
                           SpeedController leftController, SpeedController rearController,
                           DriveAlgorithms driveAlgorithms) {
        mFrontController = frontController;
        mRightController = rightController;
        mLeftController = leftController;
        mRearController = rearController;

        mDriveAlgorithms = driveAlgorithms;
    }

    public OmniDriveSystem(SpeedController frontController, SpeedController rightController,
                           SpeedController leftController, SpeedController rearController) {
        this(frontController, rightController, leftController, rearController, new DriveAlgorithms());
    }

    public void omniDrive(OmniDriveSpeed driveSpeed) {
        mFrontController.set(driveSpeed.getFront());
        mRearController.set(driveSpeed.getRear());

        mRightController.set(driveSpeed.getRight());
        mLeftController.set(driveSpeed.getLeft());
    }

    @Override
    public void omniDrive(double y, double x) {
        omniDrive(new OmniDriveSpeed(y, x, y, x));
    }

    @Override
    public void holonomicCartesian(double y, double x, double rotation) {
        OmniDriveSpeed driveSpeed = mDriveAlgorithms.vectoredOmniDriveCartesian(y, x, rotation);
        omniDrive(driveSpeed);
    }

    @Override
    public void stop() {
        mFrontController.stop();
        mRearController.stop();
        mRearController.stop();
        mLeftController.stop();
    }
}
