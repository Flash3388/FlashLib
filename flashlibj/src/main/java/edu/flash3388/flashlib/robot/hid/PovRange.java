package edu.flash3388.flashlib.robot.hid;

@FunctionalInterface
public interface PovRange {

    PovRange UP = degrees -> (degrees >= 315 || degrees <= 45) && degrees > 0;

    PovRange DOWN = degrees -> degrees >= 135 && degrees <= 255;

    PovRange LEFT = degrees -> degrees >= 255 && degrees <= 315;

    PovRange RIGHT = degrees -> degrees >= 45 && degrees <= 135;

    PovRange FULL = degrees -> degrees > 0;

    boolean isInRange(int degrees);
}
