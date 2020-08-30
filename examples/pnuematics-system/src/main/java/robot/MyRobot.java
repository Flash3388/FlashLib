package robot;

import com.flash3388.flashlib.hid.Joystick;
import com.flash3388.flashlib.hid.JoystickButton;
import com.flash3388.flashlib.io.devices.SolenoidGroup;
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
import robot.pnuematics.StubSolenoid;

public class MyRobot extends DelegatingRobotControl implements IterativeRobot {

    private final MotorSystem mShooter;
    private final SolenoidSystem mShooterDirector;

    private final Joystick mStick;

    public MyRobot(RobotControl robotControl) {
        super(robotControl);

        // Let's define a shooter system, which uses a motor to shoot
        mShooter = new MotorSystem(new PwmTalonSrx(getIoInterface().newPwm(RobotMap.SHOOTER_MOTOR)));

        // Out shooter has a piston bellow it, which raises or lowers it.
        // When the piston is open, we'll shoot high.
        // When it's closed, we'll shoot low.
        // To control pistons, se use solenoids. Let's assume we have 2 pistons,
        // and thus 2 solenoids and create a system to control that.
        mShooterDirector = new SolenoidSystem(new SolenoidGroup(
                new StubSolenoid(RobotMap.SHOOTER_SOLENOID1),
                new StubSolenoid(RobotMap.SHOOTER_SOLENOID2)));

        // We'll create some joystick to control our small robot.
        mStick = getHidInterface().newJoystick(RobotMap.STICK);

        // To shoot, we'll use the trigger button from the joystick.
        mStick.getButton(JoystickButton.TRIGGER).whileActive(new RotateAction(mShooter, 0.6));

        // Let's also make buttons to control the pistons, allowing us to raise
        // or lower our shooter.
        // We'll use two buttons, one raises, one lowers:
        mStick.getButton(1).whenActive(new OpenValveAction(mShooterDirector));
        mStick.getButton(2).whenActive(new CloseValveAction(mShooterDirector));
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
