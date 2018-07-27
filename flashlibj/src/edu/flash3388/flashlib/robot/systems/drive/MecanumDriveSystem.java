package edu.flash3388.flashlib.robot.systems.drive;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;

public class MecanumDriveSystem extends Subsystem implements HolonomicDriveInterface {

    private final FlashSpeedController mFrontRightController;
    private final FlashSpeedController mRearRightController;
    private final FlashSpeedController mFrontLeftController;
    private final FlashSpeedController mRearLeftController;

    private final DriveAlgorithms mDriveAlgorithms;

    public MecanumDriveSystem(FlashSpeedController frontRightController, FlashSpeedController rearRightController,
                              FlashSpeedController frontLeftController, FlashSpeedController rearLeftController,
                              DriveAlgorithms driveAlgorithms) {
        mFrontRightController = frontRightController;
        mRearRightController = rearRightController;
        mFrontLeftController = frontLeftController;
        mRearLeftController = rearLeftController;

        mDriveAlgorithms = driveAlgorithms;
    }

    public MecanumDriveSystem(FlashSpeedController frontRightController, FlashSpeedController rearRightController,
                              FlashSpeedController frontLeftController, FlashSpeedController rearLeftController) {
        this(frontRightController, rearRightController, frontLeftController, rearLeftController, new DriveAlgorithms());
    }

    @Override
    public void holonomicPolar(double magnitude, double direction, double rotation) {
        double[] values = mDriveAlgorithms.mecanumDrivePolar(magnitude, direction, rotation);

        mFrontRightController.set(values[0]);
        mFrontLeftController.set(values[1]);
        mRearRightController.set(values[2]);
        mRearLeftController.set(values[3]);
    }

    @Override
    public void holonomicCartesian(double y, double x, double rotation) {
        holonomicPolar(Mathf.vecMagnitude(x, y), Mathf.vecAzimuth(y, x), rotation);
    }

    @Override
    public void stop() {
        mFrontRightController.stop();
        mRearRightController.stop();
        mFrontLeftController.stop();
        mRearLeftController.stop();
    }
}
