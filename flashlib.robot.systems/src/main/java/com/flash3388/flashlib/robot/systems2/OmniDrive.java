package com.flash3388.flashlib.robot.systems2;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.systems.drive.OmniDriveSpeed;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * Interface for omni drive systems.
 *
 * @since FlashLib 3.0.0
 */
public interface OmniDrive extends Movable, Rotatable {

    /**
     * {@inheritDoc}
     */
    @Override
    default Action move(DoubleSupplier speed) {
        return omniDrive(speed, Suppliers.of(0.0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default Action rotate(DoubleSupplier speed) {
        return omniDrive(()-> {
            double speedValue = speed.getAsDouble();
            return new OmniDriveSpeed(speedValue, -speedValue, -speedValue, speedValue);
        });
    }

    /**
     * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
     * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
     * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
     * the X value to move the wheels in the front and back.
     *
     * @param speed supplier of speeds for the motors, indicated as percent v-bus [-1, 1].
     * @return action to perform motion.
     */
    Action omniDrive(Supplier<OmniDriveSpeed> speed);

    /**
     * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
     * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
     * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
     * the X value to move the wheels in the front and back.
     *
     * @param speed supplier of speeds for the motors, indicated as percent v-bus [-1, 1].
     * @return action to perform motion.
     */
    default Action omniDrive(OmniDriveSpeed speed) {
        return omniDrive(Suppliers.of(speed));
    }

    /**
     * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
     * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
     * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
     * the X value to move the wheels in the front and back.
     *
     * @param front supplier of speed for the front side (moving right-left along the x axis)
     * @param right supplier of speed for the right side (moving forward-backward along the y axis)
     * @param back supplier of speed for the back side (moving right-left along the x axis)
     * @param left supplier of speed for the left side (moving forward-backward along the y axis)
     * @return action to perform motion.
     */
    default Action omniDrive(DoubleSupplier front, DoubleSupplier right, DoubleSupplier back, DoubleSupplier left) {
        return omniDrive(()-> new OmniDriveSpeed(front.getAsDouble(), right.getAsDouble(),
                back.getAsDouble(), left.getAsDouble()));
    }

    /**
     * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
     * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
     * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
     * the X value to move the wheels in the front and back.
     *
     * @param front speed for the front side (moving right-left along the x axis)
     * @param right speed for the right side (moving forward-backward along the y axis)
     * @param back speed for the back side (moving right-left along the x axis)
     * @param left speed for the left side (moving forward-backward along the y axis)
     * @return action to perform motion.
     */
    default Action omniDrive(double front, double right, double back, double left) {
        return omniDrive(new OmniDriveSpeed(front, right, back, left));
    }

    /**
     * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
     * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
     * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
     * the X value to move the wheels in the front and back.
     *
     * @param y supplier of speed for the right and left sides (moving along the y axis)
     * @param x supplier of speed for the front and back sides (moving along the x axis)
     * @return action to perform motion.
     */
    default Action omniDrive(DoubleSupplier y, DoubleSupplier x) {
        return omniDrive(()-> new OmniDriveSpeed(y.getAsDouble(), x.getAsDouble()));
    }

    /**
     * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
     * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
     * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
     * the X value to move the wheels in the front and back.
     *
     * @param y speed for the right and left sides (moving along the y axis)
     * @param x speed for the front and back sides (moving along the x axis)
     * @return action to perform motion.
     */
    default Action omniDrive(double y, double x) {
        return omniDrive(new OmniDriveSpeed(y, x));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void stop();
}
