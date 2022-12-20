package robot;

import com.beans.Property;
import com.flash3388.flashlib.app.StartupException;
import com.flash3388.flashlib.hid.XboxAxis;
import com.flash3388.flashlib.hid.XboxButton;
import com.flash3388.flashlib.hid.XboxController;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.base.DelegatingRobotControl;
import com.flash3388.flashlib.robot.base.iterative.IterativeRobot;
import com.flash3388.flashlib.robot.modes.RobotMode;

public class UserRobot extends DelegatingRobotControl implements IterativeRobot {

    private final XboxController mController;

    public UserRobot(RobotControl robotControl, Property<RobotMode> robotModeProperty) throws StartupException {
        super(robotControl);
        robotModeProperty.set(RobotMode.create("RUN", 1, false));

        mController = getHidInterface().newXboxController(RobotMap.XBOX);
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
        StringBuilder status = new StringBuilder();

        status.append("Axes:\n");
        for (XboxAxis axis : XboxAxis.values()) {
            double value = mController.getAxis(axis).getAsDouble();

            status.append("\tAxis ");
            status.append(axis.toString());
            status.append(": ");
            status.append(value);
            status.append('\n');
        }

        status.append("Buttons:\n");
        for (XboxButton button : XboxButton.values()) {
            boolean value = mController.getButton(button).getAsBoolean();

            status.append("\tButton ");
            status.append(button.toString());
            status.append(": ");
            status.append(value);
            status.append('\n');
        }

        getLogger().info(status.toString());
    }
}
