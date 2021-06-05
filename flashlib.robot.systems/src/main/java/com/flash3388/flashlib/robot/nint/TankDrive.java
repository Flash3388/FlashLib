package com.flash3388.flashlib.robot.nint;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.systems.drive.ArcadeDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.TankDriveSpeed;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * Interface for tank drive systems.
 *
 * @since FlashLib 3.0.0
 */
public interface TankDrive extends Movable, Rotatable {

    /**
     * {@inheritDoc}
     */
    @Override
    default Action move(DoubleSupplier speed) {
         return tankDrive(speed, speed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default Action rotate(DoubleSupplier speed) {
        return tankDrive(speed, ()-> -speed.getAsDouble());
    }

    /**
     * Tank drive implements a dual joystick drive. Given right and left speed values, the code sets the values to move
     * each side separately.
     *
     * @param speed supplier speeds for the motors, each indicated in percent v-bus [-1, 1].
     * @return action to perform motion.
     */
    Action tankDrive(Supplier<TankDriveSpeed> speed);

    /**
     * Tank drive implements a dual joystick drive. Given right and left speed values, the code sets the values to move
     * each side separately.
     *
     * @param speed speeds for the motors, each indicated in percent v-bus [-1, 1].
     * @return action to perform motion.
     */
    default Action tankDrive(TankDriveSpeed speed) {
        return tankDrive(Suppliers.of(speed));
    }

    /**
     * Tank drive implements a dual joystick drive. Given right and left speed values, the code sets the values to move
     * each side separately.
     *
     * @param right supplier of speed value of the right side of motors 1 to -1.
     * @param left supplier of speed value of the left side of motors 1 to -1.
     * @return action to perform motion.
     */
    default Action tankDrive(DoubleSupplier right, DoubleSupplier left) {
        return tankDrive(()-> new TankDriveSpeed(right.getAsDouble(), left.getAsDouble()));
    }

    /**
     * Tank drive implements a dual joystick drive. Given right and left speed values, the code sets the values to move
     * each side separately.
     *
     * @param right speed value of the right side of motors 1 to -1.
     * @param left speed value of the left side of motors 1 to -1.
     * @return action to perform motion.
     */
    default Action tankDrive(double right, double left) {
        return tankDrive(Suppliers.of(right), Suppliers.of(left));
    }

    /**
     * Arcade drive implements a single joystick tank drive. Given move and rotate speed values, the code sets the values
     * to move the tank drive. The move value is responsible for moving the robot forward and backward while the
     * rotate value is responsible for the robot rotation.
     *
     * @param speed supplier speeds for motors, each indicated in percent v-bus [-1, 1].
     * @return action to perform motion.
     */
    Action arcadeDrive(Supplier<ArcadeDriveSpeed> speed);

    /**
     * Arcade drive implements a single joystick tank drive. Given move and rotate speed values, the code sets the values
     * to move the tank drive. The move value is responsible for moving the robot forward and backward while the
     * rotate value is responsible for the robot rotation.
     *
     * @param speed speeds for motors, each indicated in percent v-bus [-1, 1].
     * @return action to perform motion.
     */
    default Action arcadeDrive(ArcadeDriveSpeed speed) {
        return arcadeDrive(Suppliers.of(speed));
    }

    /**
     * Arcade drive implements a single joystick tank drive. Given move and rotate speed values, the code sets the values
     * to move the tank drive. The move value is responsible for moving the robot forward and backward while the
     * rotate value is responsible for the robot rotation.
     *
     * @param moveValue supplier of value to move forward or backward 1 to -1.
     * @param rotateValue supplier of value to rotate right or left 1 to -1.
     * @return action to perform motion.
     */
    default Action arcadeDrive(DoubleSupplier moveValue, DoubleSupplier rotateValue) {
        return arcadeDrive(()-> new ArcadeDriveSpeed(moveValue.getAsDouble(), rotateValue.getAsDouble()));
    }

    /**
     * Arcade drive implements a single joystick tank drive. Given move and rotate speed values, the code sets the values
     * to move the tank drive. The move value is responsible for moving the robot forward and backward while the
     * rotate value is responsible for the robot rotation.
     *
     * @param moveValue value to move forward or backward 1 to -1.
     * @param rotateValue value to rotate right or left 1 to -1.
     * @return action to perform motion.
     */
    default Action arcadeDrive(double moveValue, double rotateValue) {
        return arcadeDrive(Suppliers.of(moveValue), Suppliers.of(rotateValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void stop();
}
