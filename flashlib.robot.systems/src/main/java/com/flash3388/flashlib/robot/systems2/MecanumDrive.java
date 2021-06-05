package com.flash3388.flashlib.robot.systems2;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.systems.drive.MecanumDriveSpeed;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public interface MecanumDrive extends HolonomicDrive {

    /**
     * Moves the drive system.
     *
     * @param speed supplier of speeds for the system actuator.
     * @return action to perform motion.
     */
    Action mecanumDrive(Supplier<MecanumDriveSpeed> speed);

    /**
     * Moves the drive system.
     *
     * @param speed speeds for the system actuator.
     * @return action to perform motion.
     */
    default Action mecanumDrive(MecanumDriveSpeed speed) {
        return mecanumDrive(Suppliers.of(speed));
    }

    /**
     * Moves the drive system.
     *
     * @param frontRight speed for front right actuator [-1..1].
     * @param backRight speed for back right actuator [-1..1].
     * @param frontLeft speed for front left actuator [-1..1].
     * @param backLeft speed for back left actuator [-1..1].
     * @return action to perform motion.
     */
    default Action mecanumDrive(DoubleSupplier frontRight, DoubleSupplier backRight, DoubleSupplier frontLeft,
                                DoubleSupplier backLeft) {
        return mecanumDrive(()-> new MecanumDriveSpeed(frontRight.getAsDouble(), backRight.getAsDouble(),
                frontLeft.getAsDouble(), backLeft.getAsDouble()));
    }

    /**
     * Moves the drive system.
     *
     * @param frontRight speed for front right actuator [-1..1].
     * @param backRight speed for back right actuator [-1..1].
     * @param frontLeft speed for front left actuator [-1..1].
     * @param backLeft speed for back left actuator [-1..1].
     * @return action to perform motion.
     */
    default Action mecanumDrive(double frontRight, double backRight, double frontLeft, double backLeft) {
        return mecanumDrive(new MecanumDriveSpeed(frontRight, backRight, frontLeft, backLeft));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void stop();
}
