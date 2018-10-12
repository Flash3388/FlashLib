package edu.flash3388.flashlib.robot.hid;

@FunctionalInterface
public interface POVRange {

    public static final POVRange UP = new POVRange() {
        @Override
        public boolean isInRange(int degrees) {
            return (degrees >= 315 || degrees <= 45) && degrees > 0;
        }
    };

    public static final POVRange DOWN = new POVRange() {
        @Override
        public boolean isInRange(int degrees) {
            return degrees >= 135 && degrees <= 255;
        }
    };

    public static final POVRange LEFT = new POVRange() {
        @Override
        public boolean isInRange(int degrees) {
            return degrees >= 255 && degrees <= 315;
        }
    };

    public static final POVRange RIGHT = new POVRange() {
        @Override
        public boolean isInRange(int degrees) {
            return degrees >= 45 && degrees <= 135;
        }
    };

    public static final POVRange FULL = new POVRange() {
        @Override
        public boolean isInRange(int degrees) {
            return degrees > 0;
        }
    };

    boolean isInRange(int degrees);
}
