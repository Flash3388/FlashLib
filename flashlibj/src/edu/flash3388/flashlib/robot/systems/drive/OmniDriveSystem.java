package edu.flash3388.flashlib.robot.systems.drive;

import edu.flash3388.flashlib.robot.io.devices.actuators.FlashSpeedController;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;

public class OmniDriveSystem extends Subsystem implements HolonomicDriveInterface {

    private final FlashSpeedController mFrontController;
    private final FlashSpeedController mRightController;
    private final FlashSpeedController mRearController;
    private final FlashSpeedController mLeftController;

    private final DriveAlgorithms mDriveAlgorithms;

    public OmniDriveSystem(FlashSpeedController frontController, FlashSpeedController rightController,
                           FlashSpeedController leftController, FlashSpeedController rearController,
                           DriveAlgorithms driveAlgorithms) {
        mFrontController = frontController;
        mRightController = rightController;
        mLeftController = leftController;
        mRearController = rearController;

        mDriveAlgorithms = driveAlgorithms;
    }

    public OmniDriveSystem(FlashSpeedController frontController, FlashSpeedController rightController,
                           FlashSpeedController leftController, FlashSpeedController rearController) {
        this(frontController, rightController, leftController, rearController, new DriveAlgorithms());
    }

    @Override
    public void omniDrive(double y, double x) {
        mFrontController.set(y);
        mRearController.set(y);

        mRightController.set(x);
        mLeftController.set(x);
    }

    @Override
    public void holonomicCartesian(double y, double x, double rotation) {
        double[] values = mDriveAlgorithms.vectoredOmniDriveCartesian(y, x, rotation);

        mFrontController.set(values[0]);
        mRightController.set(values[1]);
        mLeftController.set(values[2]);
        mRearController.set(values[3]);
    }

    @Override
    public void stop() {
        mFrontController.stop();
        mRearController.stop();
        mRearController.stop();
        mLeftController.stop();
    }
}
