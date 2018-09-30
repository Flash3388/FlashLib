package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.io.devices.actuators.SpeedController;
import edu.flash3388.flashlib.robot.motion.Movable;
import edu.flash3388.flashlib.robot.motion.Rotatable;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;

public class SingleMotorSystem extends Subsystem implements Movable, Rotatable {

    private final SpeedController mController;

    public SingleMotorSystem(SpeedController controller) {
        mController = controller;
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
