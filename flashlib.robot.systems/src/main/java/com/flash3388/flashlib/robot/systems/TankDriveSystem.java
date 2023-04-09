package com.flash3388.flashlib.robot.systems;

import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.motion.ArcadeDriveSpeed;
import com.flash3388.flashlib.robot.motion.DriveAlgorithms;
import com.flash3388.flashlib.robot.motion.TankDriveSpeed;
import com.flash3388.flashlib.robot.systems.actions.DriveArcade;
import com.flash3388.flashlib.robot.systems.actions.DriveTank;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.Supplier;

public class TankDriveSystem extends Subsystem implements TankDrive {

    private final com.flash3388.flashlib.robot.motion.TankDrive mInterface;

    public TankDriveSystem(com.flash3388.flashlib.robot.motion.TankDrive tankDrive) {
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

    public static class Interface implements
            com.flash3388.flashlib.robot.motion.TankDrive {

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
