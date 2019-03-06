package com.flash3388.flashlib.robot.systems.drive.algorithms;

import com.flash3388.flashlib.robot.systems.drive.MecanumDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.OmniDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.TankDriveSpeed;

public class DriveAlgorithms {

    /**
     * Arcade drive implements a single joystick tank drive. Given move and rotate speed values, the code sets the values
     * to move the tank drive. The move value is responsible for moving the robot forward and backward while the
     * rotate value is responsible for the robot rotation.
     *
     * @param moveValue The value to move forward or backward 1 to -1.
     * @param rotateValue The value to rotate right or left 1 to -1.
     *
     * @return returns drive speed for tank
     */
    public TankDriveSpeed arcadeDrive(double moveValue, double rotateValue){
        double rSpeed, lSpeed;

        if (moveValue > 0.0) {
            if (rotateValue > 0.0) {
                lSpeed = moveValue - rotateValue;
                rSpeed = Math.max(moveValue, rotateValue);
            } else {
                lSpeed = Math.max(moveValue, -rotateValue);
                rSpeed = moveValue + rotateValue;
            }
        } else {
            if (rotateValue > 0.0) {
                lSpeed = -Math.max(-moveValue, rotateValue);
                rSpeed = moveValue + rotateValue;
            } else {
                lSpeed = moveValue - rotateValue;
                rSpeed = -Math.max(-moveValue, -rotateValue);
            }
        }

        return new TankDriveSpeed(rSpeed, lSpeed);
    }

    /**
     * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
     * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
     * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
     * the X value to move the wheels in the front and back.
     *
     * <p>
     * Vectored tank drive is an experimental omni control which uses a motion vector just like Mecanum drive.
     * The control algorithm derives from arcade drive.
     * </p>
     *
     * @param y y-axis value of the vector
     * @param x x-axis value of the vector
     * @param rotation rotation value
     *
     * @return returns drive speeds for omni drive
     */
    public OmniDriveSpeed vectoredOmniDriveCartesian(double y, double x, double rotation){
        TankDriveSpeed yAxis = arcadeDrive(y, rotation);
        TankDriveSpeed xAxis = arcadeDrive(x, rotation);

        // x axis - right is front, left is rear
        return new OmniDriveSpeed(xAxis.getRight(), yAxis.getRight(), xAxis.getLeft(), yAxis.getLeft());
    }

    /**
     * Mecanum drive is a type of holonomic drive base; meaning that it applies the force of the wheel at
     * a 45 angle to the robot instead of on one of its axes. By applying the force at an angle to the robot, you
     * can vary the magnitude of the force vectors to gain translational control of the robot; aka, the robot can
     * move in any direction while keeping the front of the robot in a constant compass direction. This differs
     * from the basic robot drive systems like arcade drive, tank drive, or shopping cart drive require you to
     * turn the front of the robot to travel in another direction.
     *
     * @param magnitude the magnitude of the vector [0...1]
     * @param direction the direction of the vector in degrees [0...360]
     * @param rotation rotation value [-1...1], -1 for left, 1 for right
     *
     * @return returns drive speeds for mecanum
     */
    public MecanumDriveSpeed mecanumDrivePolar(double magnitude, double direction, double rotation){

        double dirInRad = Math.toRadians(direction + 45.0);
        double cosD = Math.cos(dirInRad);
        double sinD = Math.sin(dirInRad);

        double wheelSpeeds[] = {
                (cosD * magnitude - rotation), // front right
                (sinD * magnitude + rotation), // front left
                (sinD * magnitude - rotation),// rear right
                (cosD * magnitude + rotation), // rear left
        };

        normalize(wheelSpeeds);

        return new MecanumDriveSpeed(wheelSpeeds[0], wheelSpeeds[2], wheelSpeeds[1], wheelSpeeds[3]);
    }

    private void normalize(double[] wheelSpeeds) {
        double maxMagnitude = Math.abs(wheelSpeeds[0]);

        for (int i = 1; i < 4; i++) {
            double temp = Math.abs(wheelSpeeds[i]);

            if (maxMagnitude < temp) {
                maxMagnitude = temp;
            }
        }

        if (maxMagnitude > 1.0) {
            for (int i = 0; i < 4; i++) {
                wheelSpeeds[i] = wheelSpeeds[i] / maxMagnitude;
            }
        }
    }
}
