package robot.subsystems;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.io.devices.actuators.Talon;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.systems.MotorSystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.time.Time;
import robot.actions.Feed;

import java.util.function.BooleanSupplier;

public class FeederSystem extends MotorSystem {

    private static final double ROTATE_SPEED = 0.5;

    public FeederSystem(RobotControl control) {
        super(new Interface());
    }

    public Action feedUntilNoBalls(Time delayUntilStop) {
        return new Feed((Interface) mInterface, Suppliers.of(ROTATE_SPEED), clock, delayUntilStop)
                .requires(this);
    }

    public static class Interface extends MotorSystem.Interface {

        private final BooleanSupplier mHasBalls;

        public Interface() {
            super(new Talon(null));
            mHasBalls = null;
        }

        public boolean hasBalls() {
            return mHasBalls.getAsBoolean();
        }
    }
}
