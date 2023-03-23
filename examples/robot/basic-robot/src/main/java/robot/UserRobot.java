package robot;

import com.flash3388.flashlib.app.StartupException;
import com.flash3388.flashlib.hid.XboxAxis;
import com.flash3388.flashlib.hid.XboxController;
import com.flash3388.flashlib.io.devices.SpeedControllerGroup;
import com.flash3388.flashlib.io.devices.actuators.PwmTalonSrx;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.base.DelegatingRobotControl;
import com.flash3388.flashlib.robot.base.iterative.IterativeRobot;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.motion.actions.RotateAction;
import com.flash3388.flashlib.robot.systems.MotorSystem;
import com.flash3388.flashlib.robot.systems.SolenoidSystem;
import com.flash3388.flashlib.robot.systems.actions.CloseValveAction;
import com.flash3388.flashlib.robot.systems.actions.OpenValveAction;
import com.flash3388.flashlib.robot.systems.drive.OmniDriveSystem;
import com.flash3388.flashlib.robot.systems.drive.actions.OmniDriveAction;
import robot.pnuematics.StubSolenoid;

public class UserRobot extends DelegatingRobotControl implements IterativeRobot {

    private final OmniDriveSystem mDriveSystem;
    private final MotorSystem mShooter;
    private final SolenoidSystem mShooterPiston;

    private final XboxController mController;

    public UserRobot(RobotControl robotControl) throws StartupException {
        super(robotControl);

        // This examples showcases a robot with several systems, controlled
        // directly by the user with HIDs.

        // Our robot will have an Omni-based drive, with one speed controller
        // for each side (front, right, back and left).
        mDriveSystem = new OmniDriveSystem(
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_FRONT)),
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_RIGHT)),
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_BACK)),
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_LEFT))
        );
        // We also have a shooter, based  on 2 motors. We'll use a SpeedControllerGroup
        // to combine them.
        mShooter = new MotorSystem(new SpeedControllerGroup(
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.SHOOTER_MOTOR)),
                new PwmTalonSrx(getIoInterface().newPwm(RobotMap.SHOOTER_MOTOR2))
        ));
        // Out shooter has a piston bellow it, which raises or lowers it.
        // When the piston is open, we'll shoot high.
        // When it's closed, we'll shoot low.
        // To control pistons, se use solenoids. Let's assume we have 1 piston
        // and thus 1 solenoid and create a system to control that.
        mShooterPiston = new SolenoidSystem(new StubSolenoid(RobotMap.SHOOTER_SOLENOID));

        // We'll use an xbox controller to control the robot.
        mController = getHidInterface().newXboxController(RobotMap.XBOX);

        // The drive system can have a default action, which simply
        // moves it based on the xbox controller.
        mDriveSystem.setDefaultAction(new OmniDriveAction(mDriveSystem,
                mController.getAxis(XboxAxis.RightStickY),
                mController.getAxis(XboxAxis.RightStickX)));

        // We'll control the shooter by holding down the RT.
        // This is something that we usually do with buttons and RT is an Axis,
        // so we'll need to make a button out of it.
        // This is quite simple, we just need to provide some threshold in the axis
        // value that would indicate that the button is "active".
        // We also need to indicate if it's directional, i.e. if the button can be
        // active if it passes the threshold to either side (- and +). However, RT
        // has only 0->1 values, so it can't be directional.
        mController.getAxis(XboxAxis.RT).asButton(0.8, false)
                .whileActive(new RotateAction(mShooter, 0.6));

        // We'll use the DPad to control the shooter piston.
        // The DPad as 4 buttons: up, down, left, right. We'll use UP to raise and DOWN to
        // lower the shooter, i.e. open and close the piston respectively.
        mController.getDpad().up().whenActive(new OpenValveAction(mShooterPiston));
        mController.getDpad().down().whenActive(new CloseValveAction(mShooterPiston));
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
