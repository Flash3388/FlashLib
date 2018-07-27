package edu.flash3388.flashlib.robot.modes;

public class RobotMode {

    public static final RobotMode DISABLED = new RobotMode(0);

    private final int mKey;

    public RobotMode(int key) {
        mKey = key;
    }

    public int getKey() {
        return mKey;
    }
}
