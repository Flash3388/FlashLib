package edu.flash3388.flashlib.robot.systems.drive;

import com.jmath.vectors.Vector2;
import edu.flash3388.flashlib.robot.io.devices.actuators.SpeedController;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.algorithms.DriveAlgorithms;

public class OmniDriveSystem extends Subsystem implements OmniDrive {

    private final SpeedController mFrontController;
    private final SpeedController mRightController;
    private final SpeedController mBackController;
    private final SpeedController mLeftController;

    private final DriveAlgorithms mDriveAlgorithms;

    public OmniDriveSystem(SpeedController frontController, SpeedController rightController,
                           SpeedController leftController, SpeedController backController,
                           DriveAlgorithms driveAlgorithms) {
        mFrontController = frontController;
        mRightController = rightController;
        mLeftController = leftController;
        mBackController = backController;

        mDriveAlgorithms = driveAlgorithms;
    }

    public OmniDriveSystem(SpeedController frontController, SpeedController rightController,
                           SpeedController leftController, SpeedController backController) {
        this(frontController, rightController, leftController, backController, new DriveAlgorithms());
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
