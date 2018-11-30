package edu.flash3388.flashlib.robot.systems.drive;

import com.jmath.vectors.Vector2;
import edu.flash3388.flashlib.robot.io.devices.actuators.SpeedController;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.algorithms.DriveAlgorithms;

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

    public void mecanumDrive(MecanumDriveSpeed driveSpeed) {
        mFrontRightController.set(driveSpeed.getFrontRight());
        mFrontLeftController.set(driveSpeed.getFrontLeft());
        mRearRightController.set(driveSpeed.getRearRight());
        mRearLeftController.set(driveSpeed.getRearLeft());
    }

    @Override
    public void holonomicPolar(double magnitude, double direction, double rotation) {
        MecanumDriveSpeed driveSpeed = mDriveAlgorithms.mecanumDrivePolar(magnitude, direction, rotation);
        mecanumDrive(driveSpeed);
    }

    @Override
    public void holonomicCartesian(double y, double x, double rotation) {
        Vector2 vector = new Vector2(x, y);
        holonomicPolar(vector.magnitude(), vector.angle(), rotation);
    }

    @Override
    public void stop() {
        mFrontRightController.stop();
        mRearRightController.stop();
        mFrontLeftController.stop();
        mRearLeftController.stop();
    }
}
