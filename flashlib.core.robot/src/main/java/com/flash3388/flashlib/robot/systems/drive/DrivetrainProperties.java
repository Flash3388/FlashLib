package com.flash3388.flashlib.robot.systems.drive;

public class DrivetrainProperties {
    private final double mLength;
    private final double mWidth;
    private final double mDiagonal;

    public DrivetrainProperties(double length, double width) {
        mLength = length;
        mWidth = width;
        
        mDiagonal = Math.sqrt(width * width + length * length);
    }

    public double getDiagonal() {
        return mDiagonal;
    }

    public double getWidth() {
        return mWidth;
    }

    public double getLength() {
        return mLength;
    }
}
