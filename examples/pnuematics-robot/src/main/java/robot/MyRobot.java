package robot;

import com.flash3388.flashlib.robot.DelegatingRobot;
import com.flash3388.flashlib.robot.IterativeRobot;
import com.flash3388.flashlib.robot.Robot;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.hid.xbox.XboxAxis;
import com.flash3388.flashlib.robot.hid.xbox.XboxController;
import com.flash3388.flashlib.robot.io.devices.actuators.PwmTalonSrx;
import com.flash3388.flashlib.robot.io.devices.pneumatics.SolenoidGroup;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.motion.actions.RotateAction;
import com.flash3388.flashlib.robot.systems.SingleMotorSystem;
import com.flash3388.flashlib.robot.systems.pneumatics.SingleSolenoidSystem;
import com.flash3388.flashlib.robot.systems.pneumatics.actions.ClosePistonAction;
import com.flash3388.flashlib.robot.systems.pneumatics.actions.OpenPistonAction;
import org.slf4j.Logger;
import robot.pnuematics.StubSolenoid;

public class MyRobot extends DelegatingRobot implements IterativeRobot {

    private final SingleMotorSystem mShooter;
    private final SingleSolenoidSystem mShooterDirector;
    private final XboxController mController;

    public MyRobot(Robot robot) {
        super(robot);
        mShooter = new SingleMotorSystem(new PwmTalonSrx(getIoInterface().newPwm(RobotMap.SHOOTER_MOTOR)));
        mShooterDirector = new SingleSolenoidSystem(new SolenoidGroup(
                new StubSolenoid(RobotMap.SHOOTER_SOLENOID1),
                new StubSolenoid(RobotMap.SHOOTER_SOLENOID2)));

        mController = new XboxController(0);

        // default actions
        mShooter.setDefaultAction(new RotateAction(mShooter,
                mController.getAxis(XboxAxis.RT))
                .requires(mShooter));

        // hid actions
        mController.getDPad().getUp().whenPressed(
                new OpenPistonAction(mShooterDirector)
                    .requires(mShooterDirector, mShooter));
        mController.getDPad().getDown().whenPressed(
                new ClosePistonAction(mShooterDirector)
                        .requires(mShooterDirector, mShooter));
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
