package robot;

import com.flash3388.flashlib.hid.Joystick;
import com.flash3388.flashlib.hid.JoystickAxis;
import com.flash3388.flashlib.io.devices.actuators.PwmTalonSrx;
import com.flash3388.flashlib.io.devices.actuators.SpeedControllerGroup;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.base.DelegatingRobotControl;
import com.flash3388.flashlib.robot.base.iterative.IterativeRobot;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.systems.drive.TankDriveSystem;
import com.flash3388.flashlib.robot.systems.drive.actions.ArcadeDriveAction;
import com.flash3388.flashlib.robot.systems.drive.actions.TankDriveAction;
import com.flash3388.flashlib.scheduling.actions.Actions;

public class UserRobot extends DelegatingRobotControl implements IterativeRobot {

    private final TankDriveSystem mDriveSystem;

    private final Joystick mStickRight;
    private final Joystick mStickLeft;

    public UserRobot(RobotControl robotControl) throws RobotInitializationException {
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
        // We will use these joysticks to control the motions of the drive system.
        mStickRight = getHidInterface().newJoystick(RobotMap.STICK_RIGHT);
        mStickLeft = getHidInterface().newJoystick(RobotMap.STICK_LEFT);

        // We set the default action for the drive system.
        // This action, will run by default on the system.
        // Using it, we can define the default behaviour for the system.
        // In this case, the default behaviour is to move the system as such:
        //      - Right joystick - Y axis: move right side of drive
        //      - Left joystick - Y axis: move left side of drive
        //
        // TankDriveAction defines mDriveSystem as a requirements in
        // the constructor, using `requires` call. This is superrrrr important.
        // It defines to the Scheduler (which runs the actions) that mDriveSystem is used
        // in this action. Using this, the Scheduler will prevent two actions from using the
        // drive system at the same time.
        //
        // It is important to make sure that the action we are using does that.
        // It is something that all actions from FlashLib guarantee.
        mDriveSystem.setDefaultAction(new TankDriveAction(mDriveSystem,
                mStickRight.getAxis(JoystickAxis.Y),
                mStickLeft.getAxis(JoystickAxis.Y)));

        // We can define actions to be executed when a button is pressed.
        // Here we define that when button number 0 (labeled on the joystick) is pressed,
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
        mStickLeft.getButton(0).whenActive(Actions.instant(()-> {
            mStickRight.getAxis(JoystickAxis.Y).invert();
            mStickLeft.getAxis(JoystickAxis.Y).invert();
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
        mStickLeft.getButton(1).toggleWhenActive(new ArcadeDriveAction(mDriveSystem,
                mStickRight.getAxis(JoystickAxis.Y),
                mStickLeft.getAxis(JoystickAxis.Y)));
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
