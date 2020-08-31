package robot;

import com.flash3388.flashlib.hid.Joystick;
import com.flash3388.flashlib.hid.JoystickAxis;
import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.io.devices.actuators.PwmTalonSrx;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.base.DelegatingRobotControl;
import com.flash3388.flashlib.robot.base.iterative.IterativeRobot;
import com.flash3388.flashlib.robot.modes.ManualRobotModeSupplier;
import com.flash3388.flashlib.robot.modes.RobotMode;

public class UserRobot extends DelegatingRobotControl implements IterativeRobot {

    private static final RobotMode RUN_ROBOT = RobotMode.create("run", 1);

    private final ManualRobotModeSupplier mRobotModeSupplier;

    private final SpeedController mDriveRight;
    private final SpeedController mDriveLeft;

    private final Joystick mStickRight;
    private final Joystick mStickLeft;

    public UserRobot(RobotControl robotControl, ManualRobotModeSupplier robotModeSupplier) throws RobotInitializationException {
        super(robotControl);
        mRobotModeSupplier = robotModeSupplier;

        // This example demonstrates controlling the robot mode from the robot itself.

        // Creating the speed controllers.
        // Each side of the drive has 1.
        // We'll use PWM-connected TalonSRX for example.
        mDriveRight = new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_RIGHT));
        mDriveLeft = new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_LEFT));

        // Creating the joysticks.
        // We will use these joysticks to control the motions of the drive system.
        mStickRight = getHidInterface().newJoystick(RobotMap.HID_RIGHT);
        mStickLeft = getHidInterface().newJoystick(RobotMap.HID_LEFT);

        // Go to modePeriodic
    }

    @Override
    public void robotPeriodic() {
        // Let's say that we want to change modes when the user
        // presses a button.
        // The following if, will be true if the button is down
        if (mStickRight.getButton(1).getAsBoolean()) {
            // switch between the modes depending on which mode
            // we used.
            RobotMode newMode = getMode().equals(RobotMode.DISABLED) ?
                    RUN_ROBOT : RobotMode.DISABLED;

            // After changing this, the next run while call the new
            // mode.
            mRobotModeSupplier.set(newMode);
        }
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
        // This method is called periodically (times differ, but around 25ms).
        // In here we can implement control of the drive system using the
        // joystick. This control needs to run periodically to update with
        // changes to the joystick.

        if (mode.equals(RUN_ROBOT)) {
            // We get the Y axis value from the right joystick.
            // This will move the right side of the robot.
            double right = mStickRight.getAxis(JoystickAxis.Y).getAsDouble();
            mDriveRight.set(right);

            // We do the same for the left side.
            double left = mStickLeft.getAxis(JoystickAxis.Y).getAsDouble();
            mDriveLeft.set(left);
        }
    }
}
