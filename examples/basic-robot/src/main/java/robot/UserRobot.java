package robot;

import com.flash3388.flashlib.math.Mathf;
import com.flash3388.flashlib.robot.DelegatingRobotControl;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.base.iterative.IterativeRobot;
import com.flash3388.flashlib.robot.control.PidController;
import com.flash3388.flashlib.robot.hid.XboxAxis;
import com.flash3388.flashlib.robot.hid.XboxController;
import com.flash3388.flashlib.robot.io.devices.actuators.PwmTalonSrx;
import com.flash3388.flashlib.robot.io.devices.sensors.AnalogGyro;
import com.flash3388.flashlib.robot.io.devices.sensors.Gyro;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.motion.actions.PidAction;
import com.flash3388.flashlib.robot.motion.actions.RotateAction;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.systems.SingleMotorSystem;
import com.flash3388.flashlib.robot.systems.drive.OmniDriveSystem;
import com.flash3388.flashlib.robot.systems.drive.actions.OmniDriveAction;

public class UserRobot extends DelegatingRobotControl implements IterativeRobot {

    private final OmniDriveSystem mDriveSystem;
    private final SingleMotorSystem mShooter;
    private final SingleMotorSystem mTurret;
    private final Gyro mTurretGyro;
    private final XboxController mController;

    private final PidController mTurretPidController;

    public UserRobot(RobotControl robotControl) throws RobotInitializationException {
        super(robotControl);

        mDriveSystem = new OmniDriveSystem(
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_FRONT)),
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_RIGHT)),
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_BACK)),
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_LEFT))
        );
        mShooter = new SingleMotorSystem(new PwmTalonSrx(getIoInterface().newPwm(RobotMap.SHOOTER_MOTOR)));
        mTurret = new SingleMotorSystem(new PwmTalonSrx(getIoInterface().newPwm(RobotMap.TURRET_MOTOR)));
        mTurretGyro = new AnalogGyro(getIoInterface().newAnalogInput(RobotMap.TURRET_GYRO));

        mController = getHidInterface().newXboxController(RobotMap.MAIN_CONTROLLER);

        mTurretPidController = new PidController(1.0, 0.2, 0.3, 0.0);

        // default actions
        mDriveSystem.setDefaultAction(new OmniDriveAction(mDriveSystem,
                mController.getAxis(XboxAxis.LeftStickY),
                mController.getAxis(XboxAxis.LeftStickX))
                .requires(mDriveSystem));
        mShooter.setDefaultAction(new RotateAction(mShooter,
                mController.getAxis(XboxAxis.RT))
                .requires(mShooter));
        mTurret.setDefaultAction(new RotateAction(mTurret,
                mController.getAxis(XboxAxis.RightStickX))
                .requires(mTurret));

        // hid actions
        mController.getDpad().up().whenActive(turretAngleRotationAction(0.0));
        mController.getDpad().right().whenActive(turretAngleRotationAction(90.0));
        mController.getDpad().down().whenActive(turretAngleRotationAction(180.0));
        mController.getDpad().left().whenActive(turretAngleRotationAction(270.0));
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

    private Action turretAngleRotationAction(double destinationAngle) {
        return new PidAction(mTurretPidController, mTurret,
                ()-> Mathf.shortestAngularDistance(mTurretGyro.getAngle(), destinationAngle),
                ()-> 0.0, 1.0)
                .requires(mTurret);
    }
}
