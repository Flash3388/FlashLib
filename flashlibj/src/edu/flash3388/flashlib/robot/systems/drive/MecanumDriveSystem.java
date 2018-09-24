package edu.flash3388.flashlib.robot.systems.drive;

import edu.flash3388.flashlib.math.Vector2;
import edu.flash3388.flashlib.robot.io.devices.actuators.SpeedController;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;

public class MecanumDriveSystem extends Subsystem implements HolonomicDriveInterface {

    private final SpeedController mFrontRightController;
    private final SpeedController mRearRightController;
    private final SpeedController mFrontLeftController;
    private final SpeedController mRearLeftController;

    private final DriveAlgorithms mDriveAlgorithms;

    public MecanumDriveSystem(SpeedController frontRightController, SpeedController rearRightController,
                              SpeedController frontLeftController, SpeedController rearLeftController,
                              DriveAlgorithms driveAlgorithms) {
        mFrontRightController = frontRightController;
        mRearRightController = rearRightController;
        mFrontLeftController = frontLeftController;
        mRearLeftController = rearLeftController;

        mDriveAlgorithms = driveAlgorithms;
    }

    public MecanumDriveSystem(SpeedController frontRightController, SpeedController rearRightController,
                              SpeedController frontLeftController, SpeedController rearLeftController) {
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
        Vector2 vector = new Vector2(x, y);
        holonomicPolar(vector.length(), vector.angle(), rotation);
    }

    @Override
    public void stop() {
        mFrontRightController.stop();
        mRearRightController.stop();
        mFrontLeftController.stop();
        mRearLeftController.stop();
    }
}
