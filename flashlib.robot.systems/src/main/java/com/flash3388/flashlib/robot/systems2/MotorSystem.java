package com.flash3388.flashlib.robot.systems2;

import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.io.devices.SpeedControllerGroup;
import com.flash3388.flashlib.robot.systems2.actions.Rotate;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleSupplier;

public class MotorSystem extends Subsystem implements Rotatable {

    private final com.flash3388.flashlib.robot.motion.Rotatable mInterface;

    public MotorSystem(com.flash3388.flashlib.robot.motion.Rotatable rotatable) {
        mInterface = rotatable;
    }

    public MotorSystem(SpeedController controller) {
        this(new Interface(controller));
    }

    public MotorSystem(SpeedController... controllers) {
        this(new SpeedControllerGroup(controllers));
    }

    @Override
    public Action rotate(DoubleSupplier speed) {
        return new Rotate(mInterface, speed)
                .requires(this);
    }

    @Override
    public void stop() {
        cancelCurrentAction();
    }

    public static class Interface implements
            com.flash3388.flashlib.robot.motion.Rotatable {

        private final SpeedController mController;

        public Interface(SpeedController controller) {
            mController = controller;
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
}
