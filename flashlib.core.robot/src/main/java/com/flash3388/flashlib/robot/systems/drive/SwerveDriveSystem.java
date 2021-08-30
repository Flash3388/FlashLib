package com.flash3388.flashlib.robot.systems.drive;

import com.jmath.vectors.Vector2;

public class SwerveDriveSystem implements HolonomicDrive {

    private final DrivetrainProperties mDrivetrain;
    private final SwerveWheel mFrontRightWheel;
    private final SwerveWheel mFrontLeftWheel;
    private final SwerveWheel mRearRightWheel;
    private final SwerveWheel mRearLeftWheel;

    public SwerveDriveSystem(DrivetrainProperties drivetrain, SwerveWheel frontRightWheel,
            SwerveWheel frontLeftWheel, SwerveWheel rearRightWheel, SwerveWheel rearLeftWheel) {
        mDrivetrain = drivetrain;
        mFrontRightWheel = frontRightWheel;
        mFrontLeftWheel = frontLeftWheel;
        mRearRightWheel = rearRightWheel;
        mRearLeftWheel = rearLeftWheel;
    }

    public SwerveDriveSystem(SwerveWheel frontRightWheel,SwerveWheel frontLeftWheel, 
            SwerveWheel rearRightWheel, SwerveWheel rearLeftWheel, double drivetrainWidth, 
            double drivetrainLength) {
        this(new DrivetrainProperties(drivetrainWidth, drivetrainLength), 
            frontRightWheel, frontLeftWheel, rearRightWheel, rearLeftWheel);
    }

    //for user friendlyness
    public void drive(double x, double y, double rotation) {
        holonomicCartesian(y, x, rotation);
    }
    
    @Override
    public void move(Vector2 motionVector) {
        holonomicCartesian(motionVector.y(), motionVector.x(), 0);
    }
    
    @Override
    public void rotate(double speed) {
        holonomicCartesian(0, 0, speed);
    }
    
    @Override
    public void holonomicPolar(double magnitude, double direction, double rotation) {
        Vector2 motionVector = Vector2.polar(magnitude, direction);
        holonomicCartesian(motionVector.y(), motionVector.x(), rotation);
    }
    
    @Override
    public void holonomicCartesian(double y, double x, double rotation) {//maths
        double a = x - rotation * (mDrivetrain.getLength() / mDrivetrain.getDiagonal());
        double b = x + rotation * (mDrivetrain.getLength() / mDrivetrain.getDiagonal());
        double c = y - rotation * (mDrivetrain.getWidth() / mDrivetrain.getDiagonal());
        double d = y + rotation * (mDrivetrain.getWidth() / mDrivetrain.getDiagonal());

        double frontRightSpeed = Math.sqrt((b * b) + (d * d));
        double frontLeftSpeed = Math.sqrt((b * b) + (c * c));
        double rearRightSpeed = Math.sqrt((a * a) + (d * d));
        double rearLeftSpeed = Math.sqrt((a * a) + (c * c));

        double frontRightAngle = PidSwerveWheel.convertAngle(Math.toDegrees(Math.atan2(b, d)));
        double frontLeftAngle = PidSwerveWheel.convertAngle(Math.toDegrees(Math.atan2(b, c)));
        double rearRightAngle = PidSwerveWheel.convertAngle(Math.toDegrees(Math.atan2(a, d))); 
        double rearLeftAngle = PidSwerveWheel.convertAngle(Math.toDegrees(Math.atan2(a, c)));
        
        mFrontRightWheel.move(Vector2.polar(frontRightSpeed, frontRightAngle));
        mFrontLeftWheel.move(Vector2.polar(frontLeftSpeed, frontLeftAngle));
        mRearRightWheel.move(Vector2.polar(rearRightSpeed, rearRightAngle));
        mRearLeftWheel.move(Vector2.polar(rearLeftSpeed, rearLeftAngle));
    }
    
    @Override
    public void stop() {
        mFrontLeftWheel.stop();
        mFrontRightWheel.stop();
        mRearRightWheel.stop();
        mRearLeftWheel.stop();
    }
}
