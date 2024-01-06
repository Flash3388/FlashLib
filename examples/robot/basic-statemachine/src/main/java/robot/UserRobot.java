package robot;

import com.flash3388.flashlib.app.StartupException;
import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.hid.XboxAxis;
import com.flash3388.flashlib.hid.XboxButton;
import com.flash3388.flashlib.hid.XboxController;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.base.DelegatingRobotControl;
import com.flash3388.flashlib.robot.base.iterative.IterativeRobot;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.systems.MotorSystem;
import com.flash3388.flashlib.scheduling.statemachines.State;
import com.flash3388.flashlib.scheduling.statemachines.StateMachine;
import robot.subsystems.FeederSystem;
import robot.subsystems.ShooterSystem;

public class UserRobot extends DelegatingRobotControl implements IterativeRobot {

    public enum ShooterState implements State {
        IDLE,
        MANUAL_SPINNING,
        AUTO_SPIN_TO_TARGET,
        AUTO_SHOOT
    }

    public enum FeederState implements State {
        IDLE,
        FEEDING
    }

    private final ShooterSystem mShooter;
    private final MotorSystem mFeeder;

    private final XboxController mController;

    public UserRobot(RobotControl robotControl) throws StartupException {
        super(robotControl);

        mShooter = new ShooterSystem(robotControl);
        mFeeder = new FeederSystem(robotControl);

        mController = getHidInterface().newXboxController(new HidChannel.Stub());

        StateMachine machine = getScheduler().newStateMachine("test-machine");

        // TODO: AUTO TRANSITIONS WHEN ACTIONS ARE STARTED OR FINISHED
        // TODO: LINK MULTIPLE CHANGES FOR TRANSITION
        // TODO: DEFINE EXIT STATES ON TRANSITION
        // TODO: MAINTAIN CERTAIN STATES ON DISABLED
        // TODO: CONFIGURE COMPLEX MULTI-STATE CHANGE TRANSITIONS
        //      like: if state1, transition to state2 via action1, if state3 transition to state2 via action2
        //      basically: if elevator at height1, move to height2 by action up; if elevator at height3, move to height2 by action down
        //      TRANSITIONS MUST BE CONFIGURED COMPLETELY LIKE STATES

        // FEEDER

        machine.configure(FeederState.IDLE)
                .allowTransitionTo(FeederState.FEEDING)
                .onEnter(mFeeder::cancelCurrentAction);

        machine.configure(FeederState.FEEDING)
                .allowTransitionTo(FeederState.IDLE)
                .attach(mFeeder.rotate(0.5))
                    .done();

        // SHOOTER

        machine.configure(ShooterState.IDLE)
                .allowTransitionTo(ShooterState.MANUAL_SPINNING, ShooterState.AUTO_SPIN_TO_TARGET)
                .onEnter(mShooter::cancelCurrentAction);

        machine.configure(ShooterState.MANUAL_SPINNING)
                .allowTransitionTo(ShooterState.IDLE)
                .attach(mShooter.rotate(mController.getAxis(XboxAxis.RT)))
                    .onFinishTransition(machine.newTransition(ShooterState.IDLE))
                    .done();

        machine.configure(ShooterState.AUTO_SPIN_TO_TARGET)
                .allowTransitionTo(ShooterState.IDLE)
                .attach(mShooter.rotateToTarget())
                    .onFinishTransition(machine.newTransition(ShooterState.AUTO_SHOOT, FeederState.FEEDING))
                    .done();

        machine.configure(ShooterState.AUTO_SHOOT)
                .allowTransitionTo(ShooterState.IDLE)
                .attach(mShooter.rotateAtTarget());

        mController.getButton(XboxButton.X).whenActive(machine.newTransition(ShooterState.AUTO_SPIN_TO_TARGET));

        machine.configureIdleStates(ShooterState.IDLE, FeederState.IDLE);
    }

    @Override
    public void robotPeriodic() {

    }

    @Override
    public void robotStop() {

    }

    @Override
    public void disabledInit() {

    }

    @Override
    public void disabledPeriodic() {

    }

    @Override
    public void modeInit(RobotMode mode) {

    }

    @Override
    public void modePeriodic(RobotMode mode) {

    }
}
