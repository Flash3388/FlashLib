package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.RobotInterface;
import edu.flash3388.flashlib.robot.hid.xbox.XboxController;

public class Hids {

    private Hids() {}

    public static XboxController newXboxController(RobotInterface robotInterface, int channel) {
        return new XboxController(robotInterface.getHidInterface(), channel);
    }

    public static GenericHid newGenericHid(RobotInterface robotInterface, int channel, int axesCount, int buttonsCount, int povsCount) {
        return new GenericHid(robotInterface.getHidInterface(), channel, axesCount, buttonsCount, povsCount);
    }
}
