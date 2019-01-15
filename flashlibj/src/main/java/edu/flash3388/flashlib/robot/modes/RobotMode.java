package edu.flash3388.flashlib.robot.modes;

public class RobotMode {

    public static final RobotMode DISABLED = new RobotMode("DISABLED", 0);

    private final String mName;
    private final int mKey;

    public RobotMode(String name, int key) {
        mName = name;
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public int getKey() {
        return mKey;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(mKey);
    }

    public boolean equals(RobotMode other) {
        return other != null && mKey == other.mKey;
    }

    @Override
    public String toString() {
        return mName;
    }
}
