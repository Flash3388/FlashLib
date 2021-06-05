package com.flash3388.flashlib.robot.systems2;

import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.systems2.actions.DriveMecanum;
import com.flash3388.flashlib.robot.systems.drive.MecanumDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.algorithms.DriveAlgorithms;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.Supplier;

public class MecanumDriveSystem extends Subsystem implements MecanumDrive {

    private final com.flash3388.flashlib.robot.systems.drive.MecanumDrive mInterface;

    public MecanumDriveSystem(com.flash3388.flashlib.robot.systems.drive.MecanumDrive drive) {
        mInterface = drive;
    }

    public MecanumDriveSystem(SpeedController frontRightController, SpeedController backRightController,
                              SpeedController frontLeftController, SpeedController backLeftController,
                              DriveAlgorithms driveAlgorithms) {
        this(new Interface(frontRightController, backRightController, frontLeftController, backLeftController,
                driveAlgorithms));
    }

    public MecanumDriveSystem(SpeedController frontRightController, SpeedController backRightController,
                              SpeedController frontLeftController, SpeedController backLeftController) {
        this(frontRightController, backRightController, frontLeftController, backLeftController,
                new DriveAlgorithms());
    }

    @Override
    public Action mecanumDrive(Supplier<MecanumDriveSpeed> speed) {
        return new DriveMecanum(mInterface, speed)
                .requires(this);
    }

    @Override
    public void stop() {
        cancelCurrentAction();
    }

    public static class Interface implements com.flash3388.flashlib.robot.systems.drive.MecanumDrive {

        private final SpeedController mFrontRightController;
        private final SpeedController mBackRightController;
        private final SpeedController mFrontLeftController;
        private final SpeedController mBackLeftController;

        private final DriveAlgorithms mDriveAlgorithms;

        public Interface(SpeedController frontRightController, SpeedController backRightController,
                         SpeedController frontLeftController, SpeedController backLeftController,
                         DriveAlgorithms driveAlgorithms) {
            mFrontRightController = frontRightController;
            mBackRightController = backRightController;
            mFrontLeftController = frontLeftController;
            mBackLeftController = backLeftController;

            mDriveAlgorithms = driveAlgorithms;
        }

        @Override
        public void mecanumDrive(double frontRight, double backRight, double frontLeft, double backLeft) {
            mFrontRightController.set(frontRight);
            mBackRightController.set(backRight);
            mFrontLeftController.set(frontLeft);
            mBackLeftController.set(backLeft);
        }

        @Override
        public void holonomicPolar(double magnitude, double direction, double rotation) {
            MecanumDriveSpeed driveSpeed = mDriveAlgorithms.mecanumDrivePolar(magnitude, direction, rotation);
            mecanumDrive(driveSpeed);
        }

        @Override
        public void stop() {
            mFrontRightController.stop();
            mBackRightController.stop();
            mFrontLeftController.stop();
            mBackLeftController.stop();
        }
    }
}
