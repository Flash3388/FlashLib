package com.flash3388.flashlib.robot.systems;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.control.ClosedLoopController;
import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.systems.actions.ControlledRotate;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleSupplier;

public class ControlledMotorSystem extends MotorSystem {

    private final ClosedLoopController mController;
    private final DoubleSupplier mProcessVariable;

    public ControlledMotorSystem(Interface impl,
                                 ClosedLoopController closedLoopController,
                                 DoubleSupplier processVariable) {
        super(impl);
        mController = closedLoopController;
        mProcessVariable = processVariable;
    }

    public ControlledMotorSystem(SpeedController controller,
                                 ClosedLoopController closedLoopController,
                                 DoubleSupplier processVariable) {
        super(controller);
        mController = closedLoopController;
        mProcessVariable = processVariable;
    }

    public Action rotateTo(DoubleSupplier setPoint, boolean shouldStopOnFinish) {
        return new ControlledRotate(mInterface, mController, mProcessVariable, setPoint,
                false, shouldStopOnFinish)
                .requires(this);
    }

    public Action rotateTo(double setPoint, boolean shouldStopOnFinish) {
        return rotateTo(Suppliers.of(setPoint), shouldStopOnFinish);
    }

    public Action rotateTo(double setPoint) {
        return rotateTo(setPoint, true);
    }

    public Action rotateAt(DoubleSupplier setPoint, boolean shouldStopOnFinish) {
        return new ControlledRotate(mInterface, mController, mProcessVariable, setPoint,
                true, shouldStopOnFinish)
                .requires(this);
    }

    public Action rotateAt(double setPoint, boolean shouldStopOnFinish) {
        return rotateAt(Suppliers.of(setPoint), shouldStopOnFinish);
    }

    public Action rotateAt(double setPoint) {
        return rotateAt(setPoint, true);
    }
}
