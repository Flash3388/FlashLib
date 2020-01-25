package robot;

import com.flash3388.flashlib.math.Mathf;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.control.PidController;
import com.flash3388.flashlib.robot.hid.xbox.XboxAxis;
import com.flash3388.flashlib.robot.hid.xbox.XboxController;
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
import org.slf4j.Logger;

public class Robot extends RobotBase {

    public Robot(Logger logger) { super(logger); }

    private OmniDriveSystem mDriveSystem;
    private SingleMotorSystem mShooter;
    private SingleMotorSystem mTurret;
    private Gyro mTurretGyro;
    private XboxController mController;

    private PidController mTurretPidController;

    @Override
    protected void robotInit() throws RobotInitializationException {
        mDriveSystem = new OmniDriveSystem(
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_FRONT)),
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_RIGHT)),
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_BACK)),
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_LEFT))
        );
        mShooter = new SingleMotorSystem(new PwmTalonSrx(getIoInterface().newPwm(RobotMap.SHOOTER_MOTOR)));
        mTurret = new SingleMotorSystem(new PwmTalonSrx(getIoInterface().newPwm(RobotMap.TURRET_MOTOR)));
        mTurretGyro = new AnalogGyro(getIoInterface().newAnalogInput(RobotMap.TURRET_GYRO));

        mController = new XboxController(0);

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
        mController.getDPad().getUp().whenPressed(turretAngleRotationAction(0.0));
        mController.getDPad().getRight().whenPressed(turretAngleRotationAction(90.0));
        mController.getDPad().getDown().whenPressed(turretAngleRotationAction(180.0));
        mController.getDPad().getLeft().whenPressed(turretAngleRotationAction(270.0));
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

    private Action turretAngleRotationAction(double destinationAngle) {
        return new PidAction(mTurretPidController, mTurret,
                ()-> Mathf.shortestAngularDistance(mTurretGyro.getAngle(), destinationAngle),
                ()-> 0.0, 1.0)
                .requires(mTurret);
    }
}
