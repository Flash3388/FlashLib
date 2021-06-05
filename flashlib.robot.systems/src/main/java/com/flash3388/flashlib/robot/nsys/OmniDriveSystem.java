package com.flash3388.flashlib.robot.nsys;

import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.nact.DriveOmni;
import com.flash3388.flashlib.robot.nint.OmniDrive;
import com.flash3388.flashlib.robot.systems.drive.OmniDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.algorithms.DriveAlgorithms;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.jmath.vectors.Vector2;

import java.util.function.Supplier;

public class OmniDriveSystem extends Subsystem implements OmniDrive {

    private final com.flash3388.flashlib.robot.systems.drive.OmniDrive mInterface;

    public OmniDriveSystem(com.flash3388.flashlib.robot.systems.drive.OmniDrive drive) {
        mInterface = drive;
    }

    public OmniDriveSystem(SpeedController frontController, SpeedController rightController,
                           SpeedController backController, SpeedController leftController,
                           DriveAlgorithms driveAlgorithms) {
        this(new Interface(frontController, rightController, backController, leftController, driveAlgorithms));
    }

    public OmniDriveSystem(SpeedController frontController, SpeedController rightController,
                           SpeedController backController, SpeedController leftController) {
        this(frontController, rightController, backController, leftController, new DriveAlgorithms());
    }

    @Override
    public Action omniDrive(Supplier<OmniDriveSpeed> speed) {
        return new DriveOmni(mInterface, speed);
    }

    @Override
    public void stop() {
        cancelCurrentAction();
    }

    public static class Interface implements com.flash3388.flashlib.robot.systems.drive.OmniDrive {

        private final SpeedController mFrontController;
        private final SpeedController mRightController;
        private final SpeedController mBackController;
        private final SpeedController mLeftController;

        private final DriveAlgorithms mDriveAlgorithms;

        public Interface(SpeedController frontController, SpeedController rightController,
                         SpeedController backController, SpeedController leftController,
                         DriveAlgorithms driveAlgorithms) {
            mFrontController = frontController;
            mRightController = rightController;
            mBackController = backController;
            mLeftController = leftController;

            mDriveAlgorithms = driveAlgorithms;
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
}
