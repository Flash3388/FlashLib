package com.flash3388.flashlib.robot.nsys;

import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.nact.DriveArcade;
import com.flash3388.flashlib.robot.nact.DriveTank;
import com.flash3388.flashlib.robot.nint.TankDrive;
import com.flash3388.flashlib.robot.systems.drive.ArcadeDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.TankDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.algorithms.DriveAlgorithms;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.Supplier;

public class TankDriveSystem extends Subsystem implements TankDrive {

    private final com.flash3388.flashlib.robot.systems.drive.TankDrive mInterface;

    public TankDriveSystem(com.flash3388.flashlib.robot.systems.drive.TankDrive tankDrive) {
        mInterface = tankDrive;
    }

    public TankDriveSystem(SpeedController rightController, SpeedController leftController,
                           DriveAlgorithms driveAlgorithms) {
        this(new Interface(rightController, leftController, driveAlgorithms));
    }

    public TankDriveSystem(SpeedController rightController, SpeedController leftController) {
        this(rightController, leftController, new DriveAlgorithms());
    }

    @Override
    public Action tankDrive(Supplier<TankDriveSpeed> speed) {
        return new DriveTank(mInterface, speed)
                .requires(this);
    }

    @Override
    public Action arcadeDrive(Supplier<ArcadeDriveSpeed> speed) {
        return new DriveArcade(mInterface, speed)
                .requires(this);
    }

    @Override
    public void stop() {
        cancelCurrentAction();
    }

    private static class Interface implements com.flash3388.flashlib.robot.systems.drive.TankDrive {

        private final SpeedController mRightController;
        private final SpeedController mLeftController;

        private final DriveAlgorithms mDriveAlgorithms;

        public Interface(SpeedController rightController, SpeedController leftController,
                         DriveAlgorithms driveAlgorithms) {
            mRightController = rightController;
            mLeftController = leftController;

            mDriveAlgorithms = driveAlgorithms;
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
}
