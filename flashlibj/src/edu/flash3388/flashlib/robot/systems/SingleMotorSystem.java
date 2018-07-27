package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;

public class MotorSystem extends Subsystem implements YAxisMovable, XAxisMovable, Rotatable {

    private final FlashSpeedController mController;

    public MotorSystem(FlashSpeedController controller) {
        mController = controller;
    }

    @Override
    public void moveY(double speed) {
        mController.set(speed);
    }

    @Override
    public void moveX(double speed) {
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
