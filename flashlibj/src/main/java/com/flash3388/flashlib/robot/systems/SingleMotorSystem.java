package com.flash3388.flashlib.robot.systems;

import com.flash3388.flashlib.robot.io.devices.actuators.SpeedController;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.robot.motion.Rotatable;
import com.flash3388.flashlib.robot.scheduling.Subsystem;

public class SingleMotorSystem extends Subsystem implements Movable, Rotatable {

    private final SpeedController mController;

    public SingleMotorSystem(SpeedController controller) {
        mController = controller;
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
