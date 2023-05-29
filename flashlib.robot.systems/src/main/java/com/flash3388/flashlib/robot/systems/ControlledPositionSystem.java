package com.flash3388.flashlib.robot.systems;

import com.flash3388.flashlib.control.ClosedLoopController;
import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.robot.systems.actions.ControlledMove;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleSupplier;

public class ControlledPositionSystem extends Subsystem {

    protected final com.flash3388.flashlib.robot.motion.Movable mInterface;
    private final ClosedLoopController mController;
    private final DoubleSupplier mPosition;

    public ControlledPositionSystem(Movable anInterface,
                                    ClosedLoopController controller,
                                    DoubleSupplier position) {
        mInterface = anInterface;
        mController = controller;
        mPosition = position;
    }

    public ControlledPositionSystem(SpeedController speedController,
                                    ClosedLoopController controller,
                                    DoubleSupplier position) {
        this(new Interface(speedController), controller, position);
    }

    public double getPosition() {
        return mPosition.getAsDouble();
    }

    public Action moveTo(double position) {
        return new ControlledMove(mInterface, mController, mPosition, position, false)
                .requires(this);
    }

    public static class Interface implements
            com.flash3388.flashlib.robot.motion.Movable {

        private final SpeedController mController;

        public Interface(SpeedController controller) {
            mController = controller;
        }

        @Override
        public void move(double speed) {
            mController.set(speed);
        }

        @Override
        public void stop() {
            mController.stop();
        }
    }
}
