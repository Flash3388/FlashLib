package com.flash3388.flashlib.robot.systems;

import com.flash3388.flashlib.control.ClosedLoopController;
import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.systems.actions.ControlledRotate;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleSupplier;

public class ControlledMotorSystem extends MotorSystem {

    private final ClosedLoopController mController;
    private final DoubleSupplier mProcessVariable;

    public ControlledMotorSystem(SpeedController controller,
                                 ClosedLoopController closedLoopController,
                                 DoubleSupplier processVariable) {
        super(controller);
        mController = closedLoopController;
        mProcessVariable = processVariable;
    }

    public Action rotateTo(double setPoint) {
        return new ControlledRotate(mInterface, mController, mProcessVariable, setPoint, false)
                .requires(this);
    }

    public Action rotateAt(double setPoint) {
        return new ControlledRotate(mInterface, mController, mProcessVariable, setPoint, true)
                .requires(this);
    }
}
