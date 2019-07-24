package com.flash3388.flashlib.robot.systems.drive;

import com.jmath.vectors.Vector2;

public interface MecanumDrive extends HolonomicDrive {

    @Override
    default void move(Vector2 motionVector) {
        holonomicCartesian(motionVector.y(), motionVector.x(), 0.0);
    }

    @Override
    default void rotate(double speed) {
        holonomicPolar(0.0, 0.0, speed);
    }

    @Override
    default void holonomicCartesian(double y, double x, double rotation) {
        holonomicDrive(new Vector2(x, y), rotation);
    }

    @Override
    void holonomicPolar(double magnitude, double direction, double rotation);

    default void mecanumDrive(MecanumDriveSpeed driveSpeed) {
        mecanumDrive(driveSpeed.getFrontRight(), driveSpeed.getBackRight(), driveSpeed.getFrontLeft(), driveSpeed.getBackLeft());
    }

    void mecanumDrive(double frontRight, double backRight, double frontLeft, double backLeft);
}
