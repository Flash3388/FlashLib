package com.flash3388.flashlib.robot.systems;

import com.flash3388.flashlib.io.devices.actuators.SpeedController;
import com.flash3388.flashlib.io.devices.actuators.SpeedControllerGroup;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.robot.motion.Rotatable;
import com.flash3388.flashlib.scheduling.Subsystem;

/**
 * A robot subsystem made up of a motor. Multiple motor may be used with {@link SpeedControllerGroup}.
 *
 * @since FlashLib 1.0.0
 */
public class MotorSystem extends Subsystem implements Movable, Rotatable {

    private final SpeedController mController;

    public MotorSystem(SpeedController controller) {
        mController = controller;
    }

    public MotorSystem(SpeedController... controllers) {
        this(new SpeedControllerGroup(controllers));
    }

    public final SpeedController getController() {
        return mController;
    }

    @Override
    public void move(double speed) {
        mController.set(speed);
    }

    @Override
    public void rotate(double speed) {
        mController.set(speed);
    }

    @Override
    public void stop() {
        mController.stop();
    }
}
