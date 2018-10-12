package edu.flash3388.flashlib.robot.hid;

@FunctionalInterface
public interface POVRange {

    POVRange UP = degrees -> (degrees >= 315 || degrees <= 45) && degrees > 0;

    POVRange DOWN = degrees -> degrees >= 135 && degrees <= 255;

    POVRange LEFT = degrees -> degrees >= 255 && degrees <= 315;

    POVRange RIGHT = degrees -> degrees >= 45 && degrees <= 135;

    POVRange FULL = degrees -> degrees > 0;

    boolean isInRange(int degrees);
}
