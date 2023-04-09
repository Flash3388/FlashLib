package robot;

import com.flash3388.flashlib.app.StartupException;
import com.flash3388.flashlib.hid.XboxAxis;
import com.flash3388.flashlib.hid.XboxButton;
import com.flash3388.flashlib.hid.XboxController;
import com.flash3388.flashlib.io.devices.SpeedControllerGroup;
import com.flash3388.flashlib.io.devices.actuators.PwmTalonSrx;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.base.DelegatingRobotControl;
import com.flash3388.flashlib.robot.base.iterative.IterativeRobot;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.systems.TankDriveSystem;
import com.flash3388.flashlib.scheduling.actions.Actions;

public class UserRobot extends DelegatingRobotControl implements IterativeRobot {

    private final TankDriveSystem mDriveSystem;

    private final XboxController mController;

    public UserRobot(RobotControl robotControl) throws StartupException {
        super(robotControl);

        // Creating the tank drive system.
        // We define the speed controllers for each side, there are 2 per each.
        // So we need to use a SpeedControllerGroup to group each side together
        mDriveSystem = new TankDriveSystem(
                new SpeedControllerGroup(
                        new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_RIGHT_FRONT)),
                        new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_RIGHT_BACK))
                ),
                new SpeedControllerGroup(
                        new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_LEFT_FRONT)),
                        new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_LEFT_BACK))
                )
        );

        // Creating the joysticks.
        // This time we will use an XboxController instead.
        // The XboxController is a more "advanced" controller than the regular joystick,
        // providing more functionality in a single package.
        // So just one, can do the job of two joysticks.
        mController = getHidInterface().newXboxController(RobotMap.XBOX);

        // We set the default action for the drive system.
        // This action, will run by default on the system.
        // Using it, we can define the default behaviour for the system.
        // In this case, the default behaviour is to move the system as such:
        //      - Right stick - Y axis: move right side of drive
        //      - Left stick - Y axis: move left side of drive
        //
        // TankDriveAction defines mDriveSystem as a requirements in
        // the constructor, using `requires` call. This is superrrrr important.
        // It defines to the Scheduler (which runs the actions) that mDriveSystem is used
        // in this action. Using this, the Scheduler will prevent two actions from using the
        // drive system at the same time.
        //
        // It is important to make sure that the action we are using does that.
        // It is something that all actions from FlashLib guarantee.
        mDriveSystem.setDefaultAction(mDriveSystem.tankDrive(
                mController.getAxis(XboxAxis.RightStickY),
                mController.getAxis(XboxAxis.LeftStickY)));

        // We can define actions to be executed when a button is pressed.
        // Here we define that when X button is pressed,
        // the joystick axes are inverted, making forwards backwards, and backwards forwards.
        //
        // `Actions.instant` allows defining an actions which only runs once. The actual content
        // of the action, is defined in the lambda:
        // ()-> {
        //      // do stuff in action
        // }
        //
        // Because we defined that by default, the drive system runs an action that moves using
        // the joysticks; when this is pressed, we will have reverse motion from the drive system.
        mController.getButton(XboxButton.X).whenActive(Actions.instant(()-> {
            mController.getAxis(XboxAxis.RightStickY).invert();
            mController.getAxis(XboxAxis.LeftStickY).invert();
        }));

        // Like before, we are creating another action to run when a button is pressed.
        // This time, when the button is pressed, one of two possibilities can occur:
        //      - if not already running: a non-instant action will run, which will move the drive system
        //        based on the joysticks, but in an arcade-drive algorithm instead of tank-drive.
        //      - if already running: the mentioned action will be canceled, and the default
        //        action will start running.
        //
        // In essence, this button will toggle the action, switching between the default and it.
        // Or in other words: switching between tank-drive and arcade-drive.
        mController.getButton(XboxButton.RB).toggleWhenActive(mDriveSystem.arcadeDrive(
                mController.getAxis(XboxAxis.RightStickY),
                mController.getAxis(XboxAxis.RightStickX)));
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
