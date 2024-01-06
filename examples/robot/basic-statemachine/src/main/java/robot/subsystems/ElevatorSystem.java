package robot.subsystems;

import com.flash3388.flashlib.io.devices.RelativeEncoder;
import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.io.devices.actuators.Talon;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.statemachines.State;
import com.flash3388.flashlib.scheduling.statemachines.StateMachine;
import robot.actions.ElevatorUpToEdge;
import robot.actions.ElevatorUpToHeight;

import java.util.function.BooleanSupplier;

public class ElevatorSystem extends Subsystem {

    private static final double SPEED_UP = 0.5;
    private static final double SPEED_DOWN = 0.3;

    private static final double HEIGHT1 = 1.0;
    private static final double HEIGHT2 = 1.0;
    private static final double HEIGHT3 = 1.5;

    public enum States implements State {
        IDLE,
        MANUAL,
        AT_HEIGHT_1,
        AT_HEIGHT_2,
        AT_HEIGHT_3,
        AT_LOWER_EDGE,
        AT_UPPER_EDGE,
    }

    private final SpeedController mMotor;
    private final RelativeEncoder mEncoder;
    private final BooleanSupplier mUpLatch;
    private final BooleanSupplier mDownLatch;

    public ElevatorSystem() {
        mMotor = new Talon(null);
        mEncoder = null;
        mUpLatch = null;
        mDownLatch = null;
    }

    public void configureStateMachine(RobotControl control, StateMachine machine) {

    }

    public boolean isAtUpperEdge() {
        return mUpLatch.getAsBoolean();
    }

    public boolean isAtLowerEdge() {
        return mDownLatch.getAsBoolean();
    }

    public double getHeightMeters() {
        return mEncoder.getDistancePassed();
    }

    public void up() {
        mMotor.set(SPEED_UP);
    }

    public void down() {
        mMotor.set(SPEED_DOWN);
    }

    public void stop() {
        mMotor.stop();
    }
}
