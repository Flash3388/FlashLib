package robot;

import com.flash3388.flashlib.hid.Hid;
import com.flash3388.flashlib.hid.JoystickAxis;
import com.flash3388.flashlib.io.devices.actuators.PwmTalonSrx;
import com.flash3388.flashlib.io.devices.actuators.SpeedController;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.base.DelegatingRobotControl;
import com.flash3388.flashlib.robot.base.iterative.IterativeRobot;
import com.flash3388.flashlib.robot.modes.RobotMode;

public class UserRobot extends DelegatingRobotControl implements IterativeRobot {

    private final SpeedController mDriveRight;
    private final SpeedController mDriveLeft;

    private final Hid mStickRight;
    private final Hid mStickLeft;

    public UserRobot(RobotControl robotControl) throws RobotInitializationException {
        super(robotControl);

        // This example demonstrates user control using joysticks.

        // Creating the speed controllers.
        // Each side of the drive has 1.
        // We'll use PWM-connected TalonSRX for example.
        mDriveRight = new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_RIGHT));
        mDriveLeft = new PwmTalonSrx(getIoInterface().newPwm(RobotMap.DRIVE_MOTOR_LEFT));

        // Creating the joysticks.
        // We will use these joysticks to control the motions of the drive system.
        mStickRight = getHidInterface().newGenericHid(RobotMap.HID_RIGHT);
        mStickLeft = getHidInterface().newGenericHid(RobotMap.HID_LEFT);

        // Go to modePeriodic
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
        // This method is called periodically (times differ, but around 25ms).
        // In here we can implement control of the drive system using the
        // joystick. This control needs to run periodically to update with
        // changes to the joystick.

        // We get the Y axis value from the right joystick.
        // This will move the right side of the robot.
        double right = mStickRight.getAxis(JoystickAxis.Y).getAsDouble();
        mDriveRight.set(right);

        // We do the same for the left side.
        double left = mStickLeft.getAxis(JoystickAxis.Y).getAsDouble();
        mDriveLeft.set(left);
    }
}
