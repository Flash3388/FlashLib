package edu.flash3388.flashlib.robot.frc.modes;

import edu.flash3388.flashlib.robot.modes.RobotMode;

public class FRCRobotMode extends RobotMode {

    public static final RobotMode TELEOP = new RobotMode(1);
    public static final RobotMode AUTONOMOUS = new RobotMode(2);
    public static final RobotMode TEST = new RobotMode(3);

    private FRCRobotMode(int key) {
        super(key);
    }
}
