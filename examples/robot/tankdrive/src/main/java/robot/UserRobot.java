package robot;

import com.beans.BooleanProperty;
import com.beans.properties.SimpleBooleanProperty;
import com.flash3388.flashlib.hid.Joystick;
import com.flash3388.flashlib.hid.JoystickAxis;
import com.flash3388.flashlib.io.devices.SpeedControllerGroup;
import com.flash3388.flashlib.io.devices.actuators.PwmTalonSrx;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.base.DelegatingRobotControl;
import com.flash3388.flashlib.robot.base.iterative.IterativeRobot;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.systems.drive.TankDriveSystem;
import com.flash3388.flashlib.robot.systems.drive.actions.ArcadeDriveAction;
import com.flash3388.flashlib.robot.systems.drive.actions.TankDriveAction;
import com.flash3388.flashlib.scheduling.actions.Actions;
import com.flash3388.flashlib.scheduling.triggers.Trigger;
import com.flash3388.flashlib.scheduling.triggers.Triggers;

public class UserRobot extends DelegatingRobotControl implements IterativeRobot {

    // Triggers are boolean-based objects that can be active or inactive.
    // It is possible to attach actions to triggers, which run depending on the state
    // of the trigger.
    // For example, a button is a type of a trigger.
    // It is possible to create custom triggers.

    private final BooleanProperty mTriggerActivator;

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

        // Creates a new trigger, based on a property. Triggers normally depend on a BooleanSupplier, using a property
        // allows us to modify the value here manually.
        // With this trigger, we can run actions depending on the status of the trigger, i.e. the status of the property.
        mTriggerActivator = new SimpleBooleanProperty(false);
        Trigger trigger = Triggers.onCondition(mTriggerActivator);

        // Configuring to run a TankDriveAction while the trigger is active. This means that when the property is set
        // to true, the action will start, but when the property is set to false, the action is be cancelled.
        trigger.whileActive(new TankDriveAction(mDriveSystem, mStickRight.getAxis(JoystickAxis.Y),
                mStickLeft.getAxis(JoystickAxis.Y)));
        // Configuring to run a ArcadeDriveAction while the trigger is inactive. This means that when the property is set
        // to false, the action will start, but when the property is set to true, the action is be cancelled.
        trigger.whileInactive(new ArcadeDriveAction(mDriveSystem, mStickRight.getAxis(JoystickAxis.Y),
                mStickRight.getAxis(JoystickAxis.X)));
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
