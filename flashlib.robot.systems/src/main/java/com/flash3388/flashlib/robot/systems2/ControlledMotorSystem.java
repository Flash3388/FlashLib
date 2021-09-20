package com.flash3388.flashlib.robot.systems2;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.control.Controller;
import com.flash3388.flashlib.robot.systems2.actions.ContinuousControlledRotate;
import com.flash3388.flashlib.robot.systems2.actions.ControlledRotate;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleSupplier;

public class ControlledMotorSystem extends MotorSystem {

    private final Controller mController;
    private final DoubleSupplier mProcessVariable;

    public ControlledMotorSystem(Controller controller, DoubleSupplier processVariable, SpeedController speedController) {
        super(speedController);
        mController = controller;
        mProcessVariable = processVariable;
    }

    public ControlledMotorSystem(Controller controller, DoubleSupplier processVariable, SpeedController... speedControllers) {
        super(speedControllers);
        mController = controller;
        mProcessVariable = processVariable;
    }

    public Action rotateTo(DoubleSupplier setPoint, double precision) {
        return new ControlledRotate(mInterface, mController,
                mProcessVariable, setPoint, precision)
                .requires(this);
    }

    public Action rotateTo(double setPoint, double precision) {
        return rotateTo(Suppliers.of(setPoint), precision);
    }

    public Action rotateAt(DoubleSupplier setPoint) {
        return new ContinuousControlledRotate(mInterface, mController,
                mProcessVariable, setPoint)
                .requires(this);
    }

    public Action rotateAt(double setPoint) {
        return rotateAt(Suppliers.of(setPoint));
    }
}
