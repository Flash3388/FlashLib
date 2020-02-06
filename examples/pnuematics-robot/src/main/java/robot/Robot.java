package robot;

import com.flash3388.flashlib.math.Mathf;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.control.PidController;
import com.flash3388.flashlib.robot.hid.xbox.XboxAxis;
import com.flash3388.flashlib.robot.hid.xbox.XboxController;
import com.flash3388.flashlib.robot.io.devices.actuators.PwmTalonSrx;
import com.flash3388.flashlib.robot.io.devices.pneumatics.SolenoidGroup;
import com.flash3388.flashlib.robot.io.devices.sensors.AnalogGyro;
import com.flash3388.flashlib.robot.io.devices.sensors.Gyro;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.motion.actions.PidAction;
import com.flash3388.flashlib.robot.motion.actions.RotateAction;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.systems.SingleMotorSystem;
import com.flash3388.flashlib.robot.systems.drive.OmniDriveSystem;
import com.flash3388.flashlib.robot.systems.drive.actions.OmniDriveAction;
import com.flash3388.flashlib.robot.systems.pneumatics.SingleSolenoidSystem;
import com.flash3388.flashlib.robot.systems.pneumatics.actions.ClosePistonAction;
import com.flash3388.flashlib.robot.systems.pneumatics.actions.OpenPistonAction;
import org.slf4j.Logger;
import robot.pnuematics.StubSolenoid;

public class Robot extends RobotBase {

    public Robot(Logger logger) { super(logger); }

    private SingleMotorSystem mShooter;
    private SingleSolenoidSystem mShooterDirector;
    private XboxController mController;

    @Override
    protected void robotInit() throws RobotInitializationException {
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
    protected void robotPeriodic() {

    }

    @Override
    protected void disabledInit() {

    }

    @Override
    protected void disabledPeriodic() {

    }

    @Override
    protected void modeInit(RobotMode mode) {

    }

    @Override
    protected void modePeriodic(RobotMode mode) {

    }
}
