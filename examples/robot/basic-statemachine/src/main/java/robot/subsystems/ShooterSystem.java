package robot.subsystems;

import com.flash3388.flashlib.io.devices.RangeFinder;
import com.flash3388.flashlib.io.devices.actuators.Talon;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.control.PidController;
import com.flash3388.flashlib.robot.systems.ControlledMotorSystem;
import com.flash3388.flashlib.robot.systems.MotorSystem;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleUnaryOperator;

public class ShooterSystem extends ControlledMotorSystem {

    public ShooterSystem(RobotControl control) {
        super(
                new Interface(),
                new PidController(control.getClock(), 0, 0, 0, 0),
                null);
    }

    public Action rotateToTarget() {
        Interface impl = (Interface) mInterface;
        return rotateTo(impl::getWantedSpeedRpmForTarget, false);
    }

    public Action rotateAtTarget() {
        Interface impl = (Interface) mInterface;
        return rotateAt(impl::getWantedSpeedRpmForTarget, true);
    }

    public static class Interface extends MotorSystem.Interface {

        private final RangeFinder mRangeFinder;
        private final DoubleUnaryOperator mDistanceToSpeedFunction;

        public Interface() {
            super(new Talon(null));

            mRangeFinder = null;
            mDistanceToSpeedFunction = null;
        }

        public double getDistanceMToTarget() {
            return mRangeFinder.getRangeCm() / 10.0;
        }

        public double getWantedSpeedRpmForTarget() {
            double distance = getDistanceMToTarget();
            return mDistanceToSpeedFunction.applyAsDouble(distance);
        }
    }
}
